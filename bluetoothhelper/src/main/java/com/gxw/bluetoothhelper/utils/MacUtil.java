package com.gxw.bluetoothhelper.utils;

import android.content.Context;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * Created by guoxw on 2017/8/7 0007.
 *
 * @auther guoxw
 * @createTime 2017/8/7 0007 15:22
 * @packageName com.gxw.bluetoothconn.utils
 */

public class MacUtil {


    /**
     * 会在/data/data/com.android.providers.settings/databases/settings.db数据库里增加bluetooth_address,保存蓝牙mac地址
     * 这个方法的优点是不需要权限，缺点是只有4.2之后的系统才可使用此方法
     * 但是这个跟手机系统中显示的不一致
     *
     * @param context
     *
     * @return
     */
    public static String getBTMac(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
    }

    /**
     * 获取手机的MAC地址
     *
     * @return
     */
    public static String getMac() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return macSerial;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

}
