package com.gxw.bluetoothhelper.managers;

import android.content.Context;

import com.gxw.bluetoothhelper.boradcasts.BlueToothBroad;
import com.gxw.bluetoothhelper.interfaces.BTInterface;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @auther guoxw
 * @createTime 2017/8/21 0021 15:20
 * @packageName com.gxw.bluetoothhelper.utils
 */

public class BTHelperManager extends BaseBTManager {

    private Context mContext;
    private BlueToothBroad blueToothBroad;

    private BTInterface btInterface;

    public BTHelperManager(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.blueToothBroad = new BlueToothBroad(mContext);
    }

    @Override
    public void startDiscovery() {
        mAdapter.startDiscovery();
        btInterface.scanDeviceStart();
    }

    public void setBtInterface(BTInterface btInterface) {
        this.btInterface = btInterface;
        blueToothBroad.setBtInterface(this.btInterface);
    }

    public BlueToothBroad getBlueToothBroad() {
        return blueToothBroad;
    }
}
