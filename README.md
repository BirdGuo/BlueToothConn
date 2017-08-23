# BlueToothConn
============================================ 

Android scan bluetooth devices and connection and communication

[简体中文](https://github.com/BirdGuo/BlueToothConn/blob/master/README_zh.md)

## Usage
### Init Factory
```
    BTHelperFactory btHelperFactory = new BTHelperFactory();
    btHelperManager = btHelperFactory.createBTManager(this);
    btHelperManager.setBtInterface(this);
```

### Get bluetooth devices
- get bluetooth devices already bonded
```
    ArrayList<BlueToothBean> deviceBondeds = btHelperManager.getDeviceBonded();
```

- get bluetooth devices be scanned

    Through the interface callback way to obtain. Note System version greater than 6.0 requires location access to targeting
```
    /**
     * Has new devices all.
     *
     * @param blueToothBeens
     *         the blue tooth beens
     */
    void hasNewDevicesAll(ArrayList<BlueToothBean> blueToothBeens);
    
    /**
     * Has new device.
     *
     * @param blueToothBean
     *         the blue tooth bean
     */
    void hasNewDevice(BlueToothBean blueToothBean);
```

### Connect device
1.  start server(server should be started before client)
```
    btHelperManager.setmHandler(handler);
    btHelperManager.openBTServer();
```
2.  client starts to connect server
```
    //add device to be connected
    btHelperManager.setBluetoothDeviceToConn(bluetoothDevice);
    //add handler
    btHelperManager.setmHandler(handler);
    //stop scanning
    btHelperManager.cancelDiscover();
    //start connecting
    btHelperManager.clientConnectToServer();
```

### Send Message
- send text message
```
    SendMessageUtil.sendMessage(et_detail.getText().toString().trim(), Constants.bluetoothSocket);
```
- send file message
```
    SendMessageUtil.sendMessageByFile(Environment.getExternalStorageDirectory() + "/3.png", Constants.bluetoothSocket);
```

## Issue
- disconnect() function has some problems 

  It will throw a socket closed exception when you want to disconnect,due to receiving message thread uses while(true) 
  
- bluetoothSocket is saved as static

  It's easy to casue forgetting to set null and cause leakage 
  
- Send message and receive message

  They are static methods ,i want to merge them into bluetoothManager
  
## Connect Me
  
[Email](mailto:603004002@qq.com)



