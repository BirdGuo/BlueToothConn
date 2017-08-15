package com.gxw.bluetoothhelper.interfaces;

/**
 * Created by guoxw on 2017/8/15 0015.
 *
 * @auther guoxw
 * @createTime 2017/8/15 0015 17:29
 * @packageName com.gxw.bluetoothhelper.interfaces
 */

public interface BTInterface {

    void startConnect();

    void connectSuccess();

    void connectFail();

    void disConnect();

}
