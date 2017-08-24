package com.gxw.bluetoothconn;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
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
import com.gxw.bluetoothhelper.bean.BlueToothBean;
import com.gxw.bluetoothhelper.interfaces.BTInterface;
import com.gxw.bluetoothhelper.interfaces.IBTConnect;
import com.gxw.bluetoothhelper.managers.BTHelperManager;
import com.gxw.bluetoothhelper.server.BTServer;

import java.util.ArrayList;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, BTInterface, IBTConnect {

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

    private ArrayList<String> deviceCouldConns, deviceCouldConnTemps;
    private ArrayList<BlueToothBean> deviceCouldConnBeans, deviceCouldConnBeanTemps;


    private BTHelperManager btHelperManager;

    //region handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x0001:

                    deviceCouldConns.clear();
                    deviceCouldConns.addAll(deviceCouldConnTemps);

                    deviceCouldConnBeans.clear();
                    deviceCouldConnBeans.addAll(deviceCouldConnBeanTemps);

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

        BTHelperFactory btHelperFactory = BTHelperFactory.getInstance();
        btHelperManager = btHelperFactory.createBTManager(this);
        btHelperManager.setBtInterface(this);
        btHelperManager.setIbtConnect(this);

        deviceCouldConns = new ArrayList<>();
        deviceCouldConnTemps = new ArrayList<>();

        deviceCouldConnBeans = new ArrayList<>();
        deviceCouldConnBeanTemps = new ArrayList<>();

        arrayAdapterCouldConn = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceCouldConns);

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
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "--------------onStart------------");
        btHelperManager.openBlueTooth();

        localBlueToothInfo();
//        if (!btHelperManager.checkBlueToothIsOpen()) {//蓝牙未打开
//            //提示用户打开；
////            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
////            btHelperManager.openBlueTooth();
//        } else {//已打开
//            localBlueToothInfo();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!btHelperManager.checkIsConnect()) {
            tv_conn_info.setText("");
            tv_conn_info.setVisibility(View.GONE);
            btn_open_server.setClickable(true);
        } else {
            btn_open_server.setClickable(false);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btHelperManager.disConnectBlutTooth();
        btHelperManager.cancelDiscover();
        btHelperManager.unregBroadCastReceiver();
    }

    /**
     * 初始化蓝牙信息
     */
    private void localBlueToothInfo() {

        //开启可见性
        btHelperManager.initBlueToothCouldBeSee(300);
        //注册广播监听器
        btHelperManager.regBoradCastReceiver();
        //开启扫描 6.0以上必须加入定位权限，不然扫描不到设备
        btHelperManager.startDiscovery();
        //获取本机蓝牙名称
        String name = btHelperManager.getBlutToothDeviceName();
        //获取本机蓝牙地址
        String btMac = btHelperManager.getBluetoothMac();
        tv_local_info.setText(name + "     " + btMac);

        //获取已配对蓝牙设备
        ArrayList<BlueToothBean> deviceBondeds = btHelperManager.getDeviceBonded();
        ArrayList<String> deviceAlreadyConn = new ArrayList<>();

        for (BlueToothBean deviceBonded : deviceBondeds) {
            String device = deviceBonded.getBluetoothDevice().getName() + "   " + deviceBonded.getBluetoothDevice().getAddress();
            deviceAlreadyConn.add(device);
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceAlreadyConn);
        lv_already_conn.setAdapter(arrayAdapter);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        BluetoothDevice bluetoothDevice = deviceCouldConnBeans.get(i).getBluetoothDevice();

        Log.i(TAG, bluetoothDevice.getName() + "   " + bluetoothDevice.getAddress());

        btHelperManager.setBluetoothDeviceToConn(bluetoothDevice);

        btHelperManager.cancelDiscover();

        btHelperManager.clientConnectToServer();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open_server:
                tv_conn_info.setVisibility(View.VISIBLE);
                tv_conn_info.setText("正在等待设备加入");
                btHelperManager.openBTServer();
                break;
        }
    }

    @Override
    public void scanDeviceStart() {
        Log.i(TAG, "----------scanDeviceStart-------");
    }

    @Override
    public void hasNewDevicesAll(ArrayList<BlueToothBean> blueToothBeens) {
        Log.i(TAG, "----------hasNewDevicesAll-------" + blueToothBeens.size());
        deviceCouldConnTemps.clear();
        for (BlueToothBean blueToothBean : blueToothBeens) {
            deviceCouldConnTemps.add(blueToothBean.getBluetoothDevice().getName() + "  " + blueToothBean.getBluetoothDevice().getAddress());
        }

        deviceCouldConnBeanTemps.clear();
        deviceCouldConnBeanTemps.addAll(blueToothBeens);

        //保持listview数据一致
        handler.sendEmptyMessage(0x0001);
    }

    @Override
    public void hasNewDevice(BlueToothBean blueToothBean) {
        Log.i(TAG, "----------hasNewDevice-------" + blueToothBean.getBluetoothDevice().getName());
    }

    @Override
    public void scanDeviceEnd() {
        Log.i(TAG, "----------scanDeviceEnd-------");
    }

    @Override
    public void connectSuccess(BluetoothDevice bluetoothDevice) {
        Message message = new Message();
        message.obj = bluetoothDevice;
        message.what = 0x0003;
        handler.sendMessage(message);
    }

    @Override
    public void connectClose() {

    }

    @Override
    public void receiveTextMessage(String message) {

    }

    @Override
    public void receiveFileMessage(String filePath) {

    }
}
