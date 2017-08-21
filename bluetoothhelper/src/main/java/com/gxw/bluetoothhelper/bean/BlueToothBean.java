package com.gxw.bluetoothhelper.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @auther guoxw
 * @createTime 2017/8/21 0021 15:45
 * @packageName com.gxw.bluetoothhelper.bean
 */

public class BlueToothBean {

    private BluetoothDevice bluetoothDevice;
    /**
     * @see com.gxw.bluetoothhelper.constant.Constants
     */
    private int connectState;


    private int type;

    public BlueToothBean() {
    }

    public BlueToothBean(BluetoothDevice bluetoothDevice, int connectState, int type) {
        this.bluetoothDevice = bluetoothDevice;
        this.connectState = connectState;
        this.type = type;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getConnectState() {
        return connectState;
    }

    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BlueToothBean{" +
                "bluetoothDevice=" + bluetoothDevice +
                ", connectState=" + connectState +
                '}';
    }
}
