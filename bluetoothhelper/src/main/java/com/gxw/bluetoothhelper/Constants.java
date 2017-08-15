package com.gxw.bluetoothhelper;

import android.bluetooth.BluetoothSocket;

import java.util.UUID;

/**
 * Created by guoxw on 2017/8/7 0007.
 *
 * @auther guoxw
 * @createTime 2017/8/7 0007 16:44
 * @packageName com.gxw.bluetoothconn
 */

public class Constants {

    /**
     * 蓝牙UUID
     */
    public static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //不管是蓝牙连接方还是服务器方，得到socket对象后都传入
    public static BluetoothSocket bluetoothSocket;
}
