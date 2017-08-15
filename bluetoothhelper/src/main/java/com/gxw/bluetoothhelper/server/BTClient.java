package com.gxw.bluetoothhelper.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gxw.bluetoothhelper.Constants;

import java.io.IOException;

/**
 * Created by guoxw on 2017/8/8 0008.
 *
 * @auther guoxw
 * @createTime 2017/8/8 0008 09:53
 * @packageName com.gxw.bluetoothconn.server
 */

public class BTClient {

    private BluetoothAdapter bluetoothAdapter;

    private static BTClient btClient = null;

    public static BTClient getInstance(BluetoothAdapter bluetoothAdapter) {
        if (btClient == null) {
            btClient = new BTClient(bluetoothAdapter);
        }
        return btClient;
    }

    public BTClient(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    /**
     * 尝试连接一个设备，子线程中完成，因为会线程阻塞
     *
     * @param btDev
     *         蓝牙设备对象
     * @param handler
     *         结果回调事件
     *
     * @return
     */
    public void connect(BluetoothDevice btDev, Handler handler) {
        BluetoothSocket mBluetoothSocket = null;
        try {
            //通过和服务器协商的uuid来进行连接
            mBluetoothSocket = btDev.createRfcommSocketToServiceRecord(Constants.SPP_UUID);
            if (mBluetoothSocket != null)
                //全局只有一个bluetooth，所以我们可以将这个socket对象保存在appliaction中
                Constants.bluetoothSocket = mBluetoothSocket;
            //通过反射得到bltSocket对象，与uuid进行连接得到的结果一样，但这里不提倡用反射的方法
            //mBluetoothSocket = (BluetoothSocket) btDev.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDev, 1);
            Log.d("blueTooth", "开始连接...");
            //在建立之前调用
            if (bluetoothAdapter.isDiscovering()) {
                //停止搜索
                bluetoothAdapter.cancelDiscovery();
            }
            //如果当前socket处于非连接状态则调用连接
            if (!mBluetoothSocket.isConnected()) {
                //你应当确保在调用connect()时设备没有执行搜索设备的操作。
                // 如果搜索设备也在同时进行，那么将会显著地降低连接速率，并很大程度上会连接失败。
                mBluetoothSocket.connect();
            }
            Log.d("blueTooth", "已经链接");
            if (handler == null) return;
            //结果回调
            Message message = new Message();
            message.what = 0x0004;
            message.obj = btDev;
            handler.sendMessage(message);
        } catch (Exception e) {
            Log.e("blueTooth", "...链接失败");
            try {
//                getmBluetoothSocket().close();
                if (mBluetoothSocket != null)
                    mBluetoothSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

}
