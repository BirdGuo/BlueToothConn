package com.gxw.bluetoothhelper.BlueToothFactory;

import android.content.Context;

import com.gxw.bluetoothhelper.interfaces.IBTHelperFactory;
import com.gxw.bluetoothhelper.managers.BTHelperManager;

/**
 * Created by guoxw on 2017/8/21 0021.
 *
 * @auther guoxw
 * @createTime 2017/8/21 0021 17:02
 * @packageName com.gxw.bluetoothhelper.BlueToothFactory
 */

public class BTHelperFactory implements IBTHelperFactory {

    @Override
    public BTHelperManager createBTManager(Context context) {
        return new BTHelperManager(context);
    }
}
