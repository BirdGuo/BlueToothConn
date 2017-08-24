package com.gxw.bluetoothhelper.interfaces;

import com.gxw.bluetoothhelper.bean.BlueToothBean;

import java.util.ArrayList;

/**
 * Created by guoxw on 2017/8/15 0015.
 *
 * @author guoxw
 * @createTime 2017 /8/15 0015 17:29
 * @packageName com.gxw.bluetoothhelper.interfaces
 */
public interface BTInterface {

    /**
     * 开始扫描
     */
    void scanDeviceStart();

    /**
     * 返回所有扫描到的设备
     *
     * @param blueToothBeens
     *         所有设备
     */
    void hasNewDevicesAll(ArrayList<BlueToothBean> blueToothBeens);

    /**
     * 返回最新扫描到的设备
     *
     * @param blueToothBean
     */
    void hasNewDevice(BlueToothBean blueToothBean);

    /**
     * 扫描结束
     */
    void scanDeviceEnd();

}
