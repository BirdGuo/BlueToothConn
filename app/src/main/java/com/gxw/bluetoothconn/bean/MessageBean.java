package com.gxw.bluetoothconn.bean;

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

    public MessageBean(int type, byte[] myMessage) {
        this.type = type;
        this.myMessage = myMessage;
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


}
