package com.gxw.bluetoothhelper.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.gxw.bluetoothhelper.constant.Constants;
import com.gxw.bluetoothhelper.interfaces.IBTConnect;
import com.gxw.bluetoothhelper.managers.BaseBTManager;

import java.io.IOException;

/**
 * Created by guoxw on 2017/8/8 0008.
 *
 * @author guoxw
 * @createTime 2017 /8/8 0008 09:22
 * @packageName com.gxw.bluetoothconn.server
 */
public class BTServer {

    private static BTServer btServer;

    private BluetoothAdapter tmBluetoothAdapter;
    private BaseBTManager baseBTManager;

    private IBTConnect ibtConnect;

    /**
     * 单例获取服务端
     *
     * @param tmBluetoothAdapter
     *         the tm bluetooth adapter
     *
     * @return the instance
     */
    public static BTServer getInstance(BluetoothAdapter tmBluetoothAdapter) {
        if (btServer == null) {
            btServer = new BTServer(tmBluetoothAdapter);
        }
        return btServer;
    }

    /**
     * 构造器
     *
     * @param tmBluetoothAdapter
     *         the tm bluetooth adapter
     */
    public BTServer(BluetoothAdapter tmBluetoothAdapter) {
        this.tmBluetoothAdapter = tmBluetoothAdapter;
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
     * 这个操作应该放在子线程中，因为存在线程阻塞的问题
     */
    public void run() {
        //服务器端的bltsocket需要传入uuid和一个独立存在的字符串，以便验证，通常使用包名的形式
        BluetoothServerSocket bluetoothServerSocket = null;
        try {
            bluetoothServerSocket = tmBluetoothAdapter.listenUsingRfcommWithServiceRecord("com.gxw.bluetoothconn", Constants.SPP_UUID);
            while (true) {
                //注意，当accept()返回BluetoothSocket时，socket已经连接了，因此不应该调用connect方法。
                //这里会线程阻塞，直到有蓝牙设备链接进来才会往下走
                BluetoothSocket socket = bluetoothServerSocket.accept();
                if (socket != null) {
//                    Constants.bluetoothSocket = socket;
                    baseBTManager.setBluetoothSocket(socket);
                    //回调结果通知
                    BluetoothDevice remoteDevice = socket.getRemoteDevice();
                    ibtConnect.connectSuccess(remoteDevice);
                    //如果你的蓝牙设备只是一对一的连接，则执行以下代码
                    bluetoothServerSocket.close();
                    //如果你的蓝牙设备是一对多的，则应该调用break；跳出循环
                    //break;
                }
                break;
            }
        } catch (IOException e) {
            baseBTManager.disConnectBlutTooth();
            ibtConnect.connectClose();
        }

    }

}
