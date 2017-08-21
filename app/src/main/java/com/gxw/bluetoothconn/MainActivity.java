package com.gxw.bluetoothconn;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gxw.bluetoothhelper.BlueToothFactory.BTHelperFactory;
import com.gxw.bluetoothhelper.constant.Constants;
import com.gxw.bluetoothhelper.managers.BTHelperManager;
import com.gxw.bluetoothhelper.server.BTClient;
import com.gxw.bluetoothhelper.server.BTServer;
import com.gxw.bluetoothhelper.utils.MacUtil;

import java.util.ArrayList;
import java.util.Set;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private String TAG = MainActivity.class.getName().toString();

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    //    private BluetoothAdapter mAdapter;
    private BluetoothManager bluetoothManager;
    private BTServer btServer;

    private ArrayAdapter<String> arrayAdapter, arrayAdapterCouldConn;

    private ListView lv_already_conn, lv_could_conn;
    private TextView tv_local_info, tv_conn_info;
    private Button btn_open_server;

    private ArrayList<String> deviceCouldConn, deviceCouldConnTemp;
    private ArrayList<BluetoothDevice> bluetoothDevices;

    private BTHelperManager btHelperManager;

    //region handler
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
                case 0x0003:
                    tv_conn_info.setVisibility(View.VISIBLE);
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    tv_conn_info.setText("设备" + device.getName() + "已经接入");
                    Toast.makeText(MainActivity.this, "设备" + device.getName() + "已经接入", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, DeviceDetailActivity.class);
                    startActivity(intent);
                    break;
                case 0x0004:
                    tv_conn_info.setVisibility(View.VISIBLE);
                    BluetoothDevice device1 = (BluetoothDevice) msg.obj;
                    tv_conn_info.setText("设备" + device1.getName() + "已经接入");
                    Toast.makeText(MainActivity.this, "设备" + device1.getName() + "已经接入", Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(MainActivity.this, DeviceDetailActivity.class);
                    startActivity(intent1);
                    break;
            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method

        lv_already_conn = (ListView) findViewById(R.id.lv_already_conn);
        lv_could_conn = (ListView) findViewById(R.id.lv_could_conn);
        tv_local_info = (TextView) findViewById(R.id.tv_local_info);
        tv_conn_info = (TextView) findViewById(R.id.tv_conn_info);
        btn_open_server = (Button) findViewById(R.id.btn_open_server);
        btn_open_server.setOnClickListener(this);

//        mAdapter = BluetoothAdapter.getDefaultAdapter();
//        btServer = BTServer.getInstance(mAdapter);

        BTHelperFactory btHelperFactory = new BTHelperFactory();
        btHelperManager = btHelperFactory.createBTManager(this);

        deviceCouldConn = new ArrayList<>();
        deviceCouldConnTemp = new ArrayList<>();
        bluetoothDevices = new ArrayList<>();

        arrayAdapterCouldConn = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceCouldConn);

        lv_could_conn.setAdapter(arrayAdapterCouldConn);
        lv_could_conn.setOnItemClickListener(this);

        /**
         * 判断是否支持蓝牙设备
         */
        if (!btHelperManager.checkIsSupportBlueTooth()) {
            Toast.makeText(this, "不支持蓝牙设备", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        /**
         * 判断手机是否支持BLE
         */
        if (!btHelperManager.checkIsSupportBLE()) {
            Toast.makeText(MainActivity.this, "不支持ble", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     *
     * @return the string
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {//蓝牙已打开
//                    localBlueToothInfo(mAdapter);

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

        if (!btHelperManager.checkBlueToothIsOpen()) {//蓝牙未打开
            //提示用户打开；
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {//已打开
//            localBlueToothInfo(mAdapter);
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
        btHelperManager.cancelDiscover();
        unregisterReceiver(mBluetoothReceiver);

        Constants.bluetoothSocket = null;
    }

    /**
     * 初始化蓝牙信息
     *
     * @param mBluetoothAdapter
     */
    private void localBlueToothInfo(BluetoothAdapter mBluetoothAdapter) {

        //开启可见性
//        initBlueToothCouldBeSee();
        btHelperManager.initBlueToothCouldBeSee(300);
        //获取蓝牙操作类
//        initBlueToothManager();
        //注册广播监听器
//        initBroadCast();
        btHelperManager.getBlueToothBroad().regBTBroad();
        //开启扫描 6.0以上必须加入定位权限，不然扫描不到设备
//        mAdapter.startDiscovery();
        btHelperManager.startDiscovery();
        //获取本机蓝牙名称
//        String name = mBluetoothAdapter.getName();
        String name = bluetoothManager.getBlutToothDeviceName();
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


    /**
     * 注册广播
     */
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

    private void initBlueToothManager() {
        //首先获取BluetoothManager
        bluetoothManager = (BluetoothManager) MainActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    //region 广播
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "mBluetoothReceiver action =" + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//每扫描到一个设备，系统都会发送此广播。
                //获取蓝牙设备
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (scanDevice == null || scanDevice.getName() == null) return;

                //去重
                boolean isContain = false;
                for (int i = 0; i < bluetoothDevices.size(); i++) {
                    if (bluetoothDevices.get(i).getAddress().equalsIgnoreCase(scanDevice.getAddress())) {
                        isContain = true;
                        break;
                    }
                }
                if (!isContain) {
                    bluetoothDevices.add(scanDevice);
                    deviceCouldConnTemp.add(scanDevice.getName() + ";" + scanDevice.getAddress());
                    Log.d(TAG, "name=" + scanDevice.getName() + "address=" + scanDevice.getAddress());
                    handler.sendEmptyMessage(0x0001);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对
                        Log.d("BlueToothTestActivity", "正在配对......");
//                        onRegisterBltReceiver.onBltIng(device);
                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束
                        Log.d("BlueToothTestActivity", "完成配对");
//                        onRegisterBltReceiver.onBltEnd(device);
                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对
                        Log.d("BlueToothTestActivity", "取消配对");
//                        onRegisterBltReceiver.onBltNone(device);
                    default:
                        break;
                }
            }
        }

    };
    //endregion


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//        String item = arrayAdapterCouldConn.getItem(i);
//        String[] split = item.split(";");
        BluetoothDevice bluetoothDevice = bluetoothDevices.get(i);
        Log.i(TAG, "deviceName:" + bluetoothDevice.getName());
        BTClient.getInstance(mAdapter).connect(bluetoothDevice, handler);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open_server:
                tv_conn_info.setVisibility(View.VISIBLE);
                tv_conn_info.setText("正在等待设备加入");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        btServer.run(handler);
                    }
                }).start();
                break;
        }
    }
}
