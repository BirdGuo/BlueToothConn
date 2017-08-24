package com.gxw.bluetoothhelper.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.gxw.bluetoothhelper.constant.Constants;
import com.gxw.bluetoothhelper.interfaces.IBTConnect;
import com.gxw.bluetoothhelper.managers.BaseBTManager;

/**
 * Created by guoxw on 2017/8/8 0008.
 *
 * @author guoxw
 * @createTime 2017 /8/8 0008 09:53
 * @packageName com.gxw.bluetoothconn.server
 */
public class BTClient {

    private BluetoothAdapter bluetoothAdapter;

    private BaseBTManager baseBTManager;
    private IBTConnect ibtConnect;

    private static BTClient btClient = null;

    /**
     * 单例获取客户端
     *
     * @param bluetoothAdapter
     *         蓝牙适配器
     *
     * @return BTClient
     */
    public static BTClient getInstance(BluetoothAdapter bluetoothAdapter) {
        if (btClient == null) {
            btClient = new BTClient(bluetoothAdapter);
        }
        return btClient;
    }

    /**
     * 构造器
     *
     * @param bluetoothAdapter
     *         the bluetooth adapter
     */
    public BTClient(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    /**
     * Sets base bt manager.
     *
     * @param baseBTManager
     *         the base bt manager
     */
    public void setBaseBTManager(BaseBTManager baseBTManager) {
        this.baseBTManager = baseBTManager;
    }

    /**
     * Sets ibt connect.
     *
     * @param ibtConnect
     *         the ibt connect
     */
    public void setIbtConnect(IBTConnect ibtConnect) {
        this.ibtConnect = ibtConnect;
    }

    /**
     * 尝试连接一个设备，子线程中完成，因为会线程阻塞
     *
     * @param btDev
     *         蓝牙设备对象
     *
     * @return
     */
    public void connect(BluetoothDevice btDev) {
        BluetoothSocket mBluetoothSocket = null;
        try {

            Log.d("blueTooth", btDev.getName() + "   " + btDev.getAddress());

            //通过和服务器协商的uuid来进行连接
            mBluetoothSocket = btDev.createRfcommSocketToServiceRecord(Constants.SPP_UUID);
            if (mBluetoothSocket != null) {
                baseBTManager.setBluetoothSocket(mBluetoothSocket);
            }
            //通过反射得到bltSocket对象，与uuid进行连接得到的结果一样，但这里不提倡用反射的方法
            //mBluetoothSocket = (BluetoothSocket) btDev.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDev, 1);
            Log.d("blueTooth", "开始连接...");
            //在建立之前调用
            if (bluetoothAdapter.isDiscovering()) {
                //停止搜索
                Log.i("blueTooth", "-----------停止搜索---------");
                bluetoothAdapter.cancelDiscovery();
            }
            //如果当前socket处于非连接状态则调用连接
            if (!mBluetoothSocket.isConnected()) {
                //你应当确保在调用connect()时设备没有执行搜索设备的操作。
                // 如果搜索设备也在同时进行，那么将会显著地降低连接速率，并很大程度上会连接失败。
                mBluetoothSocket.connect();
            }
            Log.d("blueTooth", "已经链接");

            ibtConnect.connectSuccess(btDev);
        } catch (Exception e) {
            Log.e("blueTooth", "...链接失败");
            baseBTManager.disConnectBlutTooth();
            ibtConnect.connectClose();
        }
    }


}
