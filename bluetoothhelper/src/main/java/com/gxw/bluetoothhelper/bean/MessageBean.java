package com.gxw.bluetoothhelper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by guoxw on 2017/8/14 0014.
 *
 * @auther guoxw
 * @createTime 2017/8/14 0014 15:54
 * @packageName com.gxw.bluetoothconn.bean
 */

public class MessageBean implements Serializable {

    private int type;
    private byte[] myMessage;

    private String fileName;
    private String filePath;

    public MessageBean() {
    }

    public MessageBean(int type, byte[] myMessage, String fileName, String filePath) {
        this.type = type;
        this.myMessage = myMessage;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(byte[] myMessage) {
        this.myMessage = myMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "type=" + type +
                ", myMessage=" + Arrays.toString(myMessage) +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
