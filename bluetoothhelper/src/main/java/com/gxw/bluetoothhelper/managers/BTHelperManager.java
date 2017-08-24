package com.gxw.bluetoothhelper.managers;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.text.TextUtils;

import com.gxw.bluetoothhelper.bean.MessageBean;
import com.gxw.bluetoothhelper.boradcasts.BlueToothBroad;
import com.gxw.bluetoothhelper.interfaces.BTInterface;
import com.gxw.bluetoothhelper.interfaces.IBTConnect;
import com.gxw.bluetoothhelper.server.BTClient;
import com.gxw.bluetoothhelper.server.BTServer;
import com.gxw.bluetoothhelper.utils.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @author guoxw
 * @createTime 2017 /8/21 0021 15:20
 * @packageName com.gxw.bluetoothhelper.utils
 */
public class BTHelperManager extends BaseBTManager {

    private Context mContext;

    /**
     * 广播接收器
     */
    private BlueToothBroad blueToothBroad;

    /**
     * 服务端
     */
    private BTServer btServer;

    /**
     * 客户端
     */
    private BTClient btClient;

    /**
     * 需要连接的设备
     */
    private BluetoothDevice bluetoothDeviceToConn;

    /**
     * 消息接口
     */
    private IBTConnect ibtConnect;

    /**
     * 连接接口
     */
    private BTInterface btInterface;

    /**
     * 构造器
     *
     * @param mContext
     *         the m context
     */
    public BTHelperManager(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.blueToothBroad = new BlueToothBroad(mContext);
        btServer = BTServer.getInstance(mAdapter);
        btServer.setBaseBTManager(this);
        btClient = BTClient.getInstance(mAdapter);
        btClient.setBaseBTManager(this);
    }


    /**
     * Sets bluetooth device to conn.
     *
     * @param bluetoothDeviceToConn
     *         the bluetooth device to conn
     */
    public void setBluetoothDeviceToConn(BluetoothDevice bluetoothDeviceToConn) {
        this.bluetoothDeviceToConn = bluetoothDeviceToConn;
    }

    /**
     * Sets ibt connect.
     * <p>
     * 此处耦合性很高
     *
     * @param ibtConnect
     *         the ibt connect
     */
    public void setIbtConnect(IBTConnect ibtConnect) {
        this.ibtConnect = ibtConnect;
        btServer.setIbtConnect(this.ibtConnect);
        btClient.setIbtConnect(this.ibtConnect);
    }

    /**
     * Sets bt interface.
     *
     * @param btInterface
     *         the bt interface
     */
    public void setBtInterface(BTInterface btInterface) {
        this.btInterface = btInterface;
        blueToothBroad.setBtInterface(this.btInterface);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                btServer.run();
            }
        }).start();
    }

    @Override
    public void clientConnectToServer() {
        if (bluetoothDeviceToConn != null) {
            btClient.connect(bluetoothDeviceToConn);
        }
    }

    @Override
    public void receiveMessage() {
        BluetoothSocket bluetoothSocket = getBluetoothSocket();
        if (bluetoothSocket == null) return;
        try {

            while (true) {
                InputStream inputStream1 = bluetoothSocket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream1);
                MessageBean messageBean = (MessageBean) objectInputStream.readObject();
                byte[] myMessage = messageBean.getMyMessage();
                if (messageBean.getType() == 1) {//文本消息
                    String trueMessage = new String(myMessage);
                    ibtConnect.receiveTextMessage(trueMessage);
                } else if (messageBean.getType() == 2) {//文件

                    File file = new File(messageBean.getFilePath());
                    FileUtil.bytesToFile(file, messageBean.getMyMessage());

                    ibtConnect.receiveFileMessage(file.getPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            disConnectBlutTooth();
            ibtConnect.connectClose();
        }
    }

    @Override
    public void sendTextMessage(String message) {
        BluetoothSocket bluetoothSocket = getBluetoothSocket();
        if (bluetoothSocket == null || TextUtils.isEmpty(message) || !bluetoothSocket.isConnected())
            return;
        try {
            byte[] bytes = message.getBytes("utf-8");
            MessageBean messageBean = new MessageBean(1, bytes, "", "");

            OutputStream outputStream1 = bluetoothSocket.getOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(outputStream1);
            outputStream.writeObject(messageBean);
            outputStream.flush();
            outputStream1.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendFileMessage(String filePath) {
        BluetoothSocket bluetoothSocket = getBluetoothSocket();
        if (bluetoothSocket == null || TextUtils.isEmpty(filePath) || !bluetoothSocket.isConnected())
            return;
        try {
            OutputStream outputStream = bluetoothSocket.getOutputStream();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            //要传输的文件路径
            File file = new File(filePath);
            //说明不存在该文件
            if (!file.exists()) return;
            //说明该文件是一个文件夹
            if (file.isDirectory()) return;

            MessageBean messageBean = new MessageBean();
            messageBean.setType(2);
            messageBean.setFileName(file.getName());
            messageBean.setFilePath(file.getPath());


            byte[] buffer = null;
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();

            messageBean.setMyMessage(buffer);

            objectOutputStream.writeObject(messageBean);
            objectOutputStream.flush();

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
