package com.gxw.bluetoothhelper.managers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.gxw.bluetoothhelper.bean.BlueToothBean;
import com.gxw.bluetoothhelper.constant.Constants;
import com.gxw.bluetoothhelper.utils.MacUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @author guoxw
 * @createTime 2017 /8/21 0021 17:11
 * @packageName com.gxw.bluetoothhelper.managers
 */
public abstract class BaseBTManager {

    private String TAG = BaseBTManager.class.getName().toString();

    /**
     * 蓝牙适配器
     */
    public BluetoothAdapter mAdapter;
    private Context mContext;

    /**
     * 蓝牙socket
     */
    private BluetoothSocket bluetoothSocket;

    /**
     * 构造器
     *
     * @param context
     *         the context
     */
    public BaseBTManager(Context context) {
        this.mContext = context;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 设置蓝牙socket
     *
     * @param bluetoothSocket
     *         the bluetooth socket
     */
    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    /**
     * 获取蓝牙socket
     *
     * @return the bluetooth socket
     */
    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    /**
     * 获取已配对的蓝牙设备列表
     *
     * @return deviceHasConnected 已经配对过的设备
     */
    public ArrayList<BlueToothBean> getDeviceBonded() {

        //获取已配对蓝牙设备
        Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
        ArrayList<BlueToothBean> deviceHasConnected = new ArrayList<BlueToothBean>();
        for (BluetoothDevice bonddevice : devices) {
            deviceHasConnected.add(new BlueToothBean(bonddevice, Constants.STATE_NORMAL, Constants.DEVICE_FROM_BONDED));
        }

        return deviceHasConnected;
    }

    /**
     * 检查蓝牙是否已打开
     *
     * @return true 已打开;false 未打开
     */
    public boolean checkBlueToothIsOpen() {
        if (mAdapter != null) {
            return mAdapter.isEnabled();
        }
        return false;
    }

    /**
     * 打开蓝牙
     */
    public void openBlueTooth() {
        // 若蓝牙没打开
        if (mAdapter != null && !mAdapter.isEnabled()) {
            mAdapter.enable();  //打开蓝牙，需要BLUETOOTH_ADMIN权限
        }
    }

    /**
     * 关闭蓝牙
     */
    public void closeBlueTooth() {
        //若已打开
        if (mAdapter != null && mAdapter.isEnabled()) {
            mAdapter.disable();
        }
    }

    /**
     * 是否已连接
     *
     * @return true 已连接；false 未连接
     */
    public boolean checkIsConnect() {

        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            return true;
        }
        return false;

    }

    /**
     * 开启蓝牙可见性
     *
     * @param seconds
     *         时间小于等于300秒，默认为120秒
     */
    public void initBlueToothCouldBeSee(int seconds) {
        if (mAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //有时候扫描不到某设备，这是因为该设备对外不可见或者距离远，需要设备该蓝牙可见，这样该才能被搜索到。可见时间默认值为120s，最多可设置300。
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, seconds);
            mContext.startActivity(discoverableIntent);
        }
    }

    /**
     * 获取本机蓝牙名称
     *
     * @return deviceName 设备名称
     */
    public String getBlutToothDeviceName() {
        if (mAdapter != null)
            return mAdapter.getName();
        return "";
    }

    /**
     * 判断是否支持蓝牙设备
     *
     * @return true 支持；false 不支持
     */
    public boolean checkIsSupportBlueTooth() {
        if (mAdapter == null) {
            return false;
        }
        return true;
    }

    /**
     * 判断手机是否支持BLE
     *
     * @return true 支持；false 不支持
     */
    public boolean checkIsSupportBLE() {
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        return true;
    }

    /**
     * 停止扫描
     */
    public void cancelDiscover() {
        if (mAdapter != null && mAdapter.isDiscovering())
            mAdapter.cancelDiscovery();
    }

    /**
     * 获取蓝牙Mac地址
     * <p>
     * 发现 安卓6.0系统用户返回的Mac地址都是 02:00:00:00:00:00，被误判为作弊用户，实际上是谷歌在6.0及以后版本对获取wifi及蓝牙MacAddress 做的改动。
     * Most notably, Local WiFi and Bluetooth MAC addresses are no longer available.
     * The getMacAddress() method of a WifiInfo object and the BluetoothAdapter.getDefaultAdapter().getAddress()
     * method will both return 02:00:00:00:00:00 from now on.
     *
     * @return bluetooth mac地址
     */
    public String getBluetoothMac() {
        String btMac = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //在6.0版本以后，获取硬件ID变得更加严格，所以通过设备的地址映射得到mac地址 但是这个地址同样不对
            btMac = MacUtil.getBTMac(mContext);
        } else {
            btMac = mAdapter.getAddress();
        }
        return btMac;
    }

    /**
     * 断开连接
     */
    public void disConnectBlutTooth() {

        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 开启扫描
     * <p>6.0以上必须加入定位权限，不然扫描不到设备</p>
     */
    public abstract void startDiscovery();

    /**
     * 注册广播
     */
    public abstract void regBoradCastReceiver();

    /**
     * 取消注册
     */
    public abstract void unregBroadCastReceiver();

    /**
     * 开启服务端
     */
    public abstract void openBTServer();

    /**
     * 开启客户端
     */
    public abstract void clientConnectToServer();

    /**
     * 接收消息
     */
    public abstract void receiveMessage();

    /**
     * 发送文本消息
     *
     * @param message
     *         需要发送的文字
     */
    public abstract void sendTextMessage(String message);

    /**
     * 发送文件消息
     *
     * @param filePath
     *         需要发送的文件路径
     */
    public abstract void sendFileMessage(String filePath);


}
