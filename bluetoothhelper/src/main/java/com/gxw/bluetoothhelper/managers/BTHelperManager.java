package com.gxw.bluetoothhelper.managers;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

import com.gxw.bluetoothhelper.boradcasts.BlueToothBroad;
import com.gxw.bluetoothhelper.interfaces.BTInterface;
import com.gxw.bluetoothhelper.server.BTClient;
import com.gxw.bluetoothhelper.server.BTServer;

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

    private BTServer btServer;
    private BTClient btClient;

    private Handler mHandler;

    private BluetoothDevice bluetoothDeviceToConn;

    public BTHelperManager(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.blueToothBroad = new BlueToothBroad(mContext);
        btServer = BTServer.getInstance(mAdapter);
        btClient = BTClient.getInstance(mAdapter);
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void setBluetoothDeviceToConn(BluetoothDevice bluetoothDeviceToConn) {
        this.bluetoothDeviceToConn = bluetoothDeviceToConn;
    }

    @Override
    public void startDiscovery() {
        mAdapter.startDiscovery();
        btInterface.scanDeviceStart();
    }

    @Override
    public void regBoradCastReceiver() {
        blueToothBroad.regBTBroad();
    }

    @Override
    public void unregBroadCastReceiver() {
        blueToothBroad.removeBTBroad();
    }

    @Override
    public void openBTServer() {
        if (mHandler != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    btServer.run(mHandler);
                }
            }).start();
        }
    }

    @Override
    public void clientConnectToServer() {
        if (mHandler != null && bluetoothDeviceToConn != null) {
            btClient.connect(bluetoothDeviceToConn, mHandler);
        }
    }


    public void setBtInterface(BTInterface btInterface) {
        this.btInterface = btInterface;
        blueToothBroad.setBtInterface(this.btInterface);
    }


}
