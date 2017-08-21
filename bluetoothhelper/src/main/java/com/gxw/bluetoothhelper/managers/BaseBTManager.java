package com.gxw.bluetoothhelper.managers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.gxw.bluetoothhelper.bean.BlueToothBean;
import com.gxw.bluetoothhelper.constant.Constants;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @auther guoxw
 * @createTime 2017/8/21 0021 17:11
 * @packageName com.gxw.bluetoothhelper.managers
 */

public abstract class BaseBTManager {

    public BluetoothAdapter mAdapter;
    private Context mContext;

    public BaseBTManager(Context context) {
        this.mContext = context;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 获取已配对的蓝牙设备列表
     *
     * @return deviceHasConnected
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

    public boolean checkBlueToothIsOpen() {
        if (mAdapter != null) {
            return mAdapter.isEnabled();
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
     * @return deviceName
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
        if (mAdapter != null)
            mAdapter.cancelDiscovery();
    }

    /**
     * 开启扫描
     * <p>6.0以上必须加入定位权限，不然扫描不到设备</p>
     */
    public abstract void startDiscovery();



}
