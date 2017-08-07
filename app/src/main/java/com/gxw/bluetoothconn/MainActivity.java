package com.gxw.bluetoothconn;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gxw.bluetoothconn.utils.MacUtil;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName().toString();

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothAdapter mAdapter;
    private ArrayAdapter<String> arrayAdapter, arrayAdapterCouldConn;
    private ListView lv_already_conn, lv_could_conn;
    private TextView tv_local_info;

    private ArrayList<String> deviceCouldConn, deviceCouldConnTemp;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x0001:

                    deviceCouldConn.clear();
                    deviceCouldConn.addAll(deviceCouldConnTemp);

                    arrayAdapterCouldConn.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method

        lv_already_conn = (ListView) findViewById(R.id.lv_already_conn);
        lv_could_conn = (ListView) findViewById(R.id.lv_could_conn);
        tv_local_info = (TextView) findViewById(R.id.tv_local_info);
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        deviceCouldConn = new ArrayList<>();
        deviceCouldConnTemp = new ArrayList<>();
        arrayAdapterCouldConn = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceCouldConn);

        lv_could_conn.setAdapter(arrayAdapterCouldConn);

        /**
         * 判断是否支持蓝牙设备
         */
        if (mAdapter == null) {
            Toast.makeText(this, "不支持蓝牙设备", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        /**
         * 判断手机是否支持BLE
         */
        if (!MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(MainActivity.this, "不支持ble", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {//蓝牙已打开
                    localBlueToothInfo(mAdapter);
                } else {//蓝牙未打开
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "蓝牙未打开");
                    Toast.makeText(this, "蓝牙未打开", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "--------------onStart------------");

        if (mAdapter.isEnabled()) {//蓝牙未打开
            //提示用户打开；
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {//已打开
            localBlueToothInfo(mAdapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "--------------onResume------------");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "--------------onPause------------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "--------------onStop------------");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "--------------onDestroy------------");
        mAdapter.cancelDiscovery();
        unregisterReceiver(mBluetoothReceiver);
    }

    private void localBlueToothInfo(BluetoothAdapter mBluetoothAdapter) {

        //开启可见性
        initBlueToothCouldBeSee();
        //注册广播监听器
        initBroadCast();
        //开启扫描
        mAdapter.startDiscovery();
        //获取本机蓝牙名称
        String name = mBluetoothAdapter.getName();
        //获取本机蓝牙地址
//        String address = mBluetoothAdapter.getAddress();//直接获取在6.0以上是02:00:00:00:00:00
        //在6.0版本以后，获取硬件ID变得更加严格，所以通过设备的地址映射得到mac地址
        String btMac = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btMac = MacUtil.getBTMac(this);
        } else {
            btMac = mBluetoothAdapter.getAddress();
        }
//        Log.d(TAG, "bluetooth name =" + name + " address =" + address);
        /**
         * 发现 安卓6.0系统用户返回的Mac地址都是 02:00:00:00:00:00，被误判为作弊用户，实际上是谷歌在6.0及以后版本对获取wifi及蓝牙MacAddress 做的改动。
         * Most notably, Local WiFi and Bluetooth MAC addresses are no longer available.
         * The getMacAddress() method of a WifiInfo object and the BluetoothAdapter.getDefaultAdapter().getAddress()
         * method will both return 02:00:00:00:00:00 from now on.
         */
        tv_local_info.setText(name + "     " + btMac);

        //获取已配对蓝牙设备
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        Log.d(TAG, "bonded device size =" + devices.size());
        ArrayList<String> deviceAlreadyConn = new ArrayList<>();

        for (BluetoothDevice bonddevice : devices) {
//            Log.d(TAG, "bonded device name =" + bonddevice.getName() + " address" + bonddevice.getAddress());
            String device = bonddevice.getName() + "   " + bonddevice.getAddress();
            deviceAlreadyConn.add(device);
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceAlreadyConn);
        lv_already_conn.setAdapter(arrayAdapter);
    }

    private void initBlueToothCouldBeSee() {
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mBluetoothReceiver, filter);
//        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        registerReceiver(mBluetoothReceiver, filter);

        if (mAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //有时候扫描不到某设备，这是因为该设备对外不可见或者距离远，需要设备该蓝牙可见，这样该才能被搜索到。可见时间默认值为120s，最多可设置300。
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }

    }

    private void initBroadCast() {
        IntentFilter filter = new IntentFilter();
        //发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //查找结束
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBluetoothReceiver, filter);
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "mBluetoothReceiver action =" + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (scanDevice == null || scanDevice.getName() == null) return;
                deviceCouldConnTemp.add(scanDevice.getName() + "   " + scanDevice.getAddress());
                Log.d(TAG, "name=" + scanDevice.getName() + "address=" + scanDevice.getAddress());
                handler.sendEmptyMessage(0x0001);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            }
        }

    };

}
