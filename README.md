# BlueToothConn

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
    btHelperManager.openBTServer();
```
2.  client starts to connect server
```
    //add device to be connected
    btHelperManager.setBluetoothDeviceToConn(bluetoothDevice);
    //stop scanning
    btHelperManager.cancelDiscover();
    //start connecting
    btHelperManager.clientConnectToServer();
```

### Send Message
- send text message
```
    btManager.sendTextMessage(et_detail.getText().toString().trim());
```
- send file message
```
    btManager.sendFileMessage(Environment.getExternalStorageDirectory() + "/3.png");
```

## Issue
- disconnect() function has some problems 

  It will throw a socket closed exception when you want to disconnect,due to receiving message thread uses while(true) 

- The coupling between Server ,client and manager is a bit high
  
## Connect Me
  
[Email](mailto:603004002@qq.com)



