package com.gxw.bluetoothhelper.utils;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gxw.bluetoothhelper.bean.MessageBean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by guoxw on 2017/8/8 0008.
 *
 * @auther guoxw
 * @createTime 2017/8/8 0008 11:08
 * @packageName com.gxw.bluetoothconn.utils
 */

public class ReceiveMessageUtil {

    private static String TAG = ReceiveMessageUtil.class.getName().toString();

    /**
     * 接收消息
     *
     * @param handler
     * @param bluetoothSocket
     */
    public static void receiveMessage(Handler handler, BluetoothSocket bluetoothSocket) {
        if (bluetoothSocket == null || handler == null) return;
        try {

            InputStream inputStream1 = bluetoothSocket.getInputStream();

            while (true) {

                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream1);
                MessageBean messageBean = (MessageBean) objectInputStream.readObject();
                byte[] myMessage = messageBean.getMyMessage();
                if (messageBean.getType() == 1) {//文本消息
                    Message message = new Message();
                    String trueMessage = new String(myMessage);
                    Log.i(TAG, "trueMessage:" + trueMessage);
                    message.obj = trueMessage;
                    message.what = 1;
                    handler.sendMessage(message);
                } else if (messageBean.getType() == 2) {//文件
                    Message message = new Message();

                    File file = new File(messageBean.getFilePath());
                    FileUtil.bytesToFile(file, messageBean.getMyMessage());

                    message.obj = "文件:保存成功";
                    message.what = 2;
                    handler.sendMessage(message);

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
