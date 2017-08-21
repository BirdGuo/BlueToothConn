package com.gxw.bluetoothhelper.boradcasts;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.gxw.bluetoothhelper.bean.BlueToothBean;
import com.gxw.bluetoothhelper.constant.Constants;
import com.gxw.bluetoothhelper.interfaces.BTInterface;

import java.util.ArrayList;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @auther guoxw
 * @createTime 2017/8/21 0021 15:13
 * @packageName com.gxw.bluetoothhelper
 */

public class BlueToothBroad extends BroadcastReceiver {

    private ArrayList<BlueToothBean> blueToothBeens;
    private BTInterface btInterface;

    private Context mContext;

    public BlueToothBroad(Context context) {
        this.blueToothBeens = new ArrayList<BlueToothBean>();
        this.mContext = context;
    }

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
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
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
        }
    }
}
