package com.gxw.bluetoothhelper.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.gxw.bluetoothhelper.constant.Constants;
import com.gxw.bluetoothhelper.constant.HandlerCode;

import java.io.IOException;

/**
 * Created by guoxw on 2017/8/8 0008.
 *
 * @auther guoxw
 * @createTime 2017/8/8 0008 09:22
 * @packageName com.gxw.bluetoothconn.server
 */

public class BTServer {

    private static BTServer btServer;

    private BluetoothAdapter tmBluetoothAdapter;

    public static BTServer getInstance(BluetoothAdapter tmBluetoothAdapter) {
        if (btServer == null) {
            btServer = new BTServer(tmBluetoothAdapter);
        }
        return btServer;
    }

    public BTServer(BluetoothAdapter tmBluetoothAdapter) {
        this.tmBluetoothAdapter = tmBluetoothAdapter;
    }

    /**
     * 这个操作应该放在子线程中，因为存在线程阻塞的问题
     */
    public void run(Handler handler) {
        //服务器端的bltsocket需要传入uuid和一个独立存在的字符串，以便验证，通常使用包名的形式
        BluetoothServerSocket bluetoothServerSocket = null;
        try {
            bluetoothServerSocket = tmBluetoothAdapter.listenUsingRfcommWithServiceRecord("com.gxw.bluetoothconn", Constants.SPP_UUID);
            while (true) {
                //注意，当accept()返回BluetoothSocket时，socket已经连接了，因此不应该调用connect方法。
                //这里会线程阻塞，直到有蓝牙设备链接进来才会往下走
                BluetoothSocket socket = bluetoothServerSocket.accept();
                if (socket != null) {
                    Constants.bluetoothSocket = socket;
                    //回调结果通知
                    Message message = new Message();
                    message.what = HandlerCode.BTSERVER_CONNECTED;
                    message.obj = socket.getRemoteDevice();
                    handler.sendMessage(message);
                    //如果你的蓝牙设备只是一对一的连接，则执行以下代码
//                    getBluetoothServerSocket().close();
                    bluetoothServerSocket.close();
                    //如果你的蓝牙设备是一对多的，则应该调用break；跳出循环
                    //break;
                }
                break;
            }
        } catch (IOException e) {
            try {
//                    getBluetoothServerSocket().close();
                if (bluetoothServerSocket != null)
                    bluetoothServerSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

}
