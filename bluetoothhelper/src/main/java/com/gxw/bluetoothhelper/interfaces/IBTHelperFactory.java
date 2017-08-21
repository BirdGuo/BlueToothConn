package com.gxw.bluetoothhelper.interfaces;

import android.content.Context;

import com.gxw.bluetoothhelper.managers.BTHelperManager;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @auther guoxw
 * @createTime 2017/8/21 0021 17:07
 * @packageName com.gxw.bluetoothhelper.interfaces
 */

public interface IBTHelperFactory {

    BTHelperManager createBTManager(Context context);

}
