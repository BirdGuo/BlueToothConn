package com.gxw.bluetoothhelper.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @author guoxw
 * @createTime 2017 /8/21 0021 15:45
 * @packageName com.gxw.bluetoothhelper.bean
 */
public class BlueToothBean {

    /**
     * 具体设备
     */
    private BluetoothDevice bluetoothDevice;

    /**
     * 连接状态
     *
     * @see com.gxw.bluetoothhelper.constant.Constants#STATE_NORMAL
     * @see com.gxw.bluetoothhelper.constant.Constants#STATE_CONNECTED
     * @see com.gxw.bluetoothhelper.constant.Constants#STATE_CONNECTFAIL
     * @see com.gxw.bluetoothhelper.constant.Constants#STATE_CONNECTING
     */
    private int connectState;

    /**
     * 设备来源
     *
     * @see com.gxw.bluetoothhelper.constant.Constants#DEVICE_FROM_BONDED
     * @see com.gxw.bluetoothhelper.constant.Constants#DEVICE_FROM_DISCOVER
     */
    private int from;

    /**
     * 蓝牙设备构造器.
     */
    public BlueToothBean() {
    }

    /**
     * 蓝牙设备构造器.
     *
     * @param bluetoothDevice
     *         具体设备
     * @param connectState
     *         连接状态
     * @param from
     *         来源
     */
    public BlueToothBean(BluetoothDevice bluetoothDevice, int connectState, int from) {
        this.bluetoothDevice = bluetoothDevice;
        this.connectState = connectState;
        this.from = from;
    }

    /**
     * Gets bluetooth device.
     *
     * @return the bluetooth device
     */
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    /**
     * Sets bluetooth device.
     *
     * @param bluetoothDevice
     *         the bluetooth device
     */
    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    /**
     * Gets connect state.
     *
     * @return the connect state
     */
    public int getConnectState() {
        return connectState;
    }

    /**
     * Sets connect state.
     *
     * @param connectState
     *         the connect state
     */
    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }

    /**
     * Gets from.
     *
     * @return the from
     */
    public int getFrom() {
        return from;
    }

    /**
     * Sets from.
     *
     * @param from
     *         the from
     */
    public void setFrom(int from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "BlueToothBean{" +
                "bluetoothDevice=" + bluetoothDevice +
                ", connectState=" + connectState +
                '}';
    }
}
