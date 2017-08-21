package com.gxw.bluetoothhelper.interfaces;

import com.gxw.bluetoothhelper.bean.BlueToothBean;

import java.util.ArrayList;

/**
 * Created by guoxw on 2017/8/15 0015.
 *
 * @auther guoxw
 * @createTime 2017 /8/15 0015 17:29
 * @packageName com.gxw.bluetoothhelper.interfaces
 */
public interface BTInterface {

    /**
     * Scan device start.
     */
    void scanDeviceStart();

    /**
     * Has new devices all.
     *
     * @param blueToothBeen
     *         the blue tooth been
     */
    void hasNewDevicesAll(ArrayList<BlueToothBean> blueToothBeen);

    /**
     * Has new device.
     *
     * @param blueToothBean
     *         the blue tooth bean
     */
    void hasNewDevice(BlueToothBean blueToothBean);

    /**
     * Scan device end.
     */
    void scanDeviceEnd();

}
