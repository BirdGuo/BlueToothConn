package com.gxw.bluetoothhelper.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * Created by guoxw on 2017/8/24 0024.
 *
 * @author guoxw
 * @createTime 2017 /8/24 0024 11:04
 * @packageName com.gxw.bluetoothhelper.interfaces
 */
public interface IBTConnect {

    /**
     * 连接成功
     *
     * @param bluetoothDevice
     *         返回连接设备
     */
    void connectSuccess(BluetoothDevice bluetoothDevice);

    /**
     * 连接断开
     */
    void connectClose();

    /**
     * 收到文字消息
     *
     * @param message
     *         具体文字
     */
    void receiveTextMessage(String message);

    /**
     * 收到文件消息
     *
     * @param filePath
     *         文件路径
     */
    void receiveFileMessage(String filePath);

}
