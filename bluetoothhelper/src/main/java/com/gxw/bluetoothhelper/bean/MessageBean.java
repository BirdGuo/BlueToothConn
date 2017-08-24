package com.gxw.bluetoothhelper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by guoxw on 2017/8/14 0014.
 *
 * @author guoxw
 * @createTime 2017 /8/14 0014 15:54
 * @packageName com.gxw.bluetoothconn.bean
 */
public class MessageBean implements Serializable {

    private int type;
    private byte[] myMessage;

    private String fileName;
    private String filePath;

    /**
     * Instantiates a new Message bean.
     */
    public MessageBean() {
    }

    /**
     * Instantiates a new Message bean.
     *
     * @param type
     *         the type
     * @param myMessage
     *         the my message
     * @param fileName
     *         the file name
     * @param filePath
     *         the file path
     */
    public MessageBean(int type, byte[] myMessage, String fileName, String filePath) {
        this.type = type;
        this.myMessage = myMessage;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type
     *         the type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Get my message byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getMyMessage() {
        return myMessage;
    }

    /**
     * Sets my message.
     *
     * @param myMessage
     *         the my message
     */
    public void setMyMessage(byte[] myMessage) {
        this.myMessage = myMessage;
    }

    /**
     * Gets file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets file name.
     *
     * @param fileName
     *         the file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets file path.
     *
     * @param filePath
     *         the file path
     */
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
