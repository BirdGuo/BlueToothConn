package com.gxw.bluetoothhelper.constant;

import java.util.UUID;

/**
 * Created by guoxw on 2017/8/7 0007.
 *
 * @author guoxw
 * @createTime 2017 /8/7 0007 16:44
 * @packageName com.gxw.bluetoothconn
 */
public class Constants {

    /**
     * 蓝牙UUID
     */
    public static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    /**
     * 连接状态 未连接
     */
    public static final int STATE_NORMAL = 0x0000;
    /**
     * 连接状态 连接中
     */
    public static final int STATE_CONNECTING = 0x0001;
    /**
     * 连接状态 已连接
     */
    public static final int STATE_CONNECTED = 0x0002;
    /**
     * 连接状态 连接失败
     */
    public static final int STATE_CONNECTFAIL = 0x0003;

    /**
     * 设备来源 发现的设备
     */
    public static final int DEVICE_FROM_DISCOVER = 0x0001;
    /**
     * 设备来源 配对过得设备
     */
    public static final int DEVICE_FROM_BONDED = 0x0002;

}
