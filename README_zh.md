# BlueToothConn

蓝牙扫描连接通信

## 使用
### 初始化工厂类
```
    BTHelperFactory btHelperFactory = new BTHelperFactory();
    btHelperManager = btHelperFactory.createBTManager(this);
    btHelperManager.setBtInterface(this);
```

### 获取蓝牙设备列表
- 获取已配对过得设备列表
```
    ArrayList<BlueToothBean> deviceBondeds = btHelperManager.getDeviceBonded();
```

- 获取扫描到的设备列表

    通过接口回调方式获取。注意系统版本大于6.0需要获取定位权限
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

### 连接设备
1.  启动服务端(在客户端前先启动)
```
    btHelperManager.setmHandler(handler);
    btHelperManager.openBTServer();
```
2.  客户端启动连接
```
    //传入需要连接的设备
    btHelperManager.setBluetoothDeviceToConn(bluetoothDevice);
    //设置handler回调
    btHelperManager.setmHandler(handler);
    //停止扫描
    btHelperManager.cancelDiscover();
    //开始连接
    btHelperManager.clientConnectToServer();
```

### 发送消息
- 发送文字
```
    SendMessageUtil.sendMessage(et_detail.getText().toString().trim(), Constants.bluetoothSocket);
```
- 发送文件
```
    SendMessageUtil.sendMessageByFile(Environment.getExternalStorageDirectory() + "/3.png", Constants.bluetoothSocket);
```

## 问题
- 断开连接方法写的不是很好 

  由于收消息的线程是个while(true),当一方断开连接时，会造成抛出socket closed异常。
  
- 连接的Socket保存方式为static

  将socket存为一个静态变量，但是容易忘记置空和引起泄露
  
- 收发消息

  这是单独写static的方法，想把它并入Manager中
  
## 联系我
  
[邮箱](mailto:603004002@qq.com)
