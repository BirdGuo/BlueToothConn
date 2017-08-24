package com.gxw.bluetoothhelper.interfaces;

import android.content.Context;

import com.gxw.bluetoothhelper.managers.BTHelperManager;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @author guoxw
 * @createTime 2017 /8/21 0021 17:07
 * @packageName com.gxw.bluetoothhelper.interfaces
 */
public interface IBTHelperFactory {

    /**
     * 构造 BTHelperManager
     *
     * @param context
     *         the context
     *
     * @return BTHelperManager
     */
    BTHelperManager createBTManager(Context context);

}
