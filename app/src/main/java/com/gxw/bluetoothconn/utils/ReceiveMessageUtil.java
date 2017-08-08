package com.gxw.bluetoothconn.utils;

import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by guoxw on 2017/8/8 0008.
 *
 * @auther guoxw
 * @createTime 2017/8/8 0008 11:08
 * @packageName com.gxw.bluetoothconn.utils
 */

public class ReceiveMessageUtil {

    /**
     * 接收消息
     *
     * @param handler
     * @param bluetoothSocket
     */
    public static void receiveMessage(Handler handler, BluetoothSocket bluetoothSocket) {
        if (bluetoothSocket == null || handler == null) return;
        try {
            InputStream inputStream = bluetoothSocket.getInputStream();
            // 从客户端获取信息
            BufferedReader bff = new BufferedReader(new InputStreamReader(inputStream));
            String json;

            while (true) {
                while ((json = bff.readLine()) != null) {
                    Message message = new Message();
                    message.obj = json;
                    message.what = 1;
                    handler.sendMessage(message);
                    //说明接下来会接收到一个文件流
                    if ("file".equals(json)) {
                        FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/test.gif");
                        int length;
                        int fileSzie = 0;
                        byte[] b = new byte[1024];
                        // 2、把socket输入流写到文件输出流中去
                        while ((length = inputStream.read(b)) != -1) {
                            fos.write(b, 0, length);
                            fileSzie += length;
                            System.out.println("当前大小：" + fileSzie);
                            //这里通过先前传递过来的文件大小作为参照，因为该文件流不能自主停止，所以通过判断文件大小来跳出循环
                        }
                        fos.close();
                        message.obj = "文件:保存成功";
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
