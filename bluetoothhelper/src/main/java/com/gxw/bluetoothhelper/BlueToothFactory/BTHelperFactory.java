package com.gxw.bluetoothhelper.BlueToothFactory;

import android.content.Context;

import com.gxw.bluetoothhelper.interfaces.IBTHelperFactory;
import com.gxw.bluetoothhelper.managers.BTHelperManager;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @author guoxw
 * @createTime 2017 /8/21 0021 17:02
 * @packageName com.gxw.bluetoothhelper.BlueToothFactory
 */
public class BTHelperFactory implements IBTHelperFactory {

    private static BTHelperManager btHelperManager;

    private static BTHelperFactory btHelperFactory;

    /**
     * 获取工厂单例
     *
     * @return the instance
     */
    public static BTHelperFactory getInstance() {
        if (btHelperFactory == null) {
            btHelperFactory = new BTHelperFactory();
        }
        return btHelperFactory;
    }

    /**
     * 单例构建BTHelperManager
     *
     * @param context
     *         the context
     *
     * @return btHelperManager静态变量
     */
    @Override
    public BTHelperManager createBTManager(Context context) {
        if (btHelperManager == null)
            btHelperManager = new BTHelperManager(context);
        return btHelperManager;
    }
}
