package com.gxw.bluetoothhelper.boradcasts;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.gxw.bluetoothhelper.bean.BlueToothBean;
import com.gxw.bluetoothhelper.constant.Constants;
import com.gxw.bluetoothhelper.interfaces.BTInterface;

import java.util.ArrayList;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @author guoxw
 * @createTime 2017 /8/21 0021 15:13
 * @packageName com.gxw.bluetoothhelper
 */
public class BlueToothBroad extends BroadcastReceiver {

    private String TAG = BlueToothBroad.class.getName().toString();

    private ArrayList<BlueToothBean> blueToothBeens;
    private BTInterface btInterface;

    private Context mContext;

    /**
     * 初始化构造器
     *
     * @param context
     *         the context
     */
    public BlueToothBroad(Context context) {
        this.blueToothBeens = new ArrayList<BlueToothBean>();
        this.mContext = context;
    }

    /**
     * 设置扫描接口回调
     *
     * @param btInterface
     *         the bt interface
     */
    public void setBtInterface(BTInterface btInterface) {
        this.btInterface = btInterface;
    }

    /**
     * 添加启动广播
     */
    public void regBTBroad() {

        IntentFilter filter = new IntentFilter();
        //发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备开启关闭状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //连接状态改变(收不到)
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        //查找结束
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(this, filter);

    }

    /**
     * 移除广播
     */
    public void removeBTBroad() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {//每扫描到一个设备，系统都会发送此广播。
            //获取蓝牙设备
            BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (scanDevice == null || scanDevice.getName() == null) return;

            //去重
            boolean isContain = false;
            for (int i = 0; i < blueToothBeens.size(); i++) {
                if (blueToothBeens.get(i).getBluetoothDevice().getAddress().equalsIgnoreCase(scanDevice.getAddress())) {
                    isContain = true;
                    break;
                }
            }
            if (!isContain) {
                BlueToothBean blueToothBean = new BlueToothBean(scanDevice, Constants.STATE_NORMAL, Constants.DEVICE_FROM_DISCOVER);
                blueToothBeens.add(blueToothBean);
                btInterface.hasNewDevice(blueToothBean);
                btInterface.hasNewDevicesAll(blueToothBeens);
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            btInterface.scanDeviceEnd();
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            Log.i(TAG, "状态改变");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDING:
                    Log.i(TAG, "正在配对...");
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.i(TAG, "完成配对");
                    break;
                case BluetoothDevice.BOND_NONE:
                    Log.i(TAG, "取消配对");
                    break;

                default:
                    break;
            }
        } else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {//收不到
            int state = (Integer) intent.getExtras().get(BluetoothAdapter.EXTRA_CONNECTION_STATE);
            Log.i(TAG, "链接状态改变");
            switch (state) {
                case BluetoothAdapter.STATE_CONNECTED:
                    Log.i(TAG, "已连接");
                    break;
                case BluetoothAdapter.STATE_CONNECTING:
                    Log.i(TAG, "正在连接");
                    break;
                case BluetoothAdapter.STATE_DISCONNECTED:
                    Log.i(TAG, "已断开");
//                    mListAdapter.notifyDataSetChanged();
                    break;
                case BluetoothAdapter.STATE_DISCONNECTING:
                    Log.i(TAG, "正在断开...");
                    break;

                default:
                    break;
            }
        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            Log.i(TAG, "蓝牙开关状态改变");
            int state = (Integer) intent.getExtras().get(
                    BluetoothAdapter.EXTRA_STATE);
            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    Log.i(TAG, "蓝牙已打开");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.i(TAG, "正在打开蓝牙...");
                    break;
                case BluetoothAdapter.STATE_OFF:
                    Log.i(TAG, "蓝牙已关闭");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.i(TAG, "正在关闭蓝牙...");
                    break;

                default:
                    break;
            }
        }

    }
}
