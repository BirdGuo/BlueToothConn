package com.gxw.bluetoothconn;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static com.gxw.bluetoothconn.Constants.MESSAGE_STATE_CHANGE;

public class DeviceDetailActivity extends AppCompatActivity {

    private String TAG = DeviceDetailActivity.class.getName().toString();

    private int mState;
//    private ConnectThread mConnectThread;
//    private ConnectedThread mConnectedThread;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * Set the current state of the chat connection
     *
     * @param state
     *         An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
    }

//    /**
//     * Start the ConnectThread to initiate a connection to a remote device.
//     *
//     * @param device
//     *         The BluetoothDevice to connect
//     * @param secure
//     *         Socket Security type - Secure (true) , Insecure (false)
//     */
//    public synchronized void connect(BluetoothDevice device, boolean secure) {
//        Log.d(TAG, "connect to: " + device);
//
//        // Cancel any thread attempting to make a connection
//        if (mState == STATE_CONNECTING) {
//            if (mConnectThread != null) {
//                mConnectThread.cancel();
//                mConnectThread = null;
//            }
//        }
//
//        // Cancel any thread currently running a connection
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel();
//            mConnectedThread = null;
//        }
//
//        // Start the thread to connect with the given device
//        mConnectThread = new ConnectThread(device, secure);
//        mConnectThread.start();
//        setState(STATE_CONNECTING);
//    }
//
//    /**
//     * Start the ConnectedThread to begin managing a Bluetooth connection
//     *
//     * @param socket
//     *         The BluetoothSocket on which the connection was made
//     * @param device
//     *         The BluetoothDevice that has been connected
//     */
//    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
//            device, final String socketType) {
//        Log.d(TAG, "connected, Socket Type:" + socketType);
//
//        // Cancel the thread that completed the connection
//        if (mConnectThread != null) {
//            mConnectThread.cancel();
//            mConnectThread = null;
//        }
//
//        // Cancel any thread currently running a connection
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel();
//            mConnectedThread = null;
//        }
//
//        // Cancel the accept thread because we only want to connect to one device
//        if (mSecureAcceptThread != null) {
//            mSecureAcceptThread.cancel();
//            mSecureAcceptThread = null;
//        }
//        if (mInsecureAcceptThread != null) {
//            mInsecureAcceptThread.cancel();
//            mInsecureAcceptThread = null;
//        }
//
//        // Start the thread to manage the connection and perform transmissions
//        mConnectedThread = new ConnectedThread(socket, socketType);
//        mConnectedThread.start();
//
//        // Send the name of the connected device back to the UI Activity
//        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.DEVICE_NAME, device.getName());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
//
//        setState(STATE_CONNECTED);
//    }
//
//    /**
//     * This thread runs while attempting to make an outgoing connection
//     * with a device. It runs straight through; the connection either
//     * succeeds or fails.
//     */
//    private class ConnectThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final BluetoothDevice mmDevice;
//        private String mSocketType;
//
//        public ConnectThread(BluetoothDevice device, boolean secure) {
//            mmDevice = device;
//            BluetoothSocket tmp = null;
//            mSocketType = secure ? "Secure" : "Insecure";
//
//            // Get a BluetoothSocket for a connection with the
//            // given BluetoothDevice
//            try {
//                if (secure) {
//                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
//                } else {
//                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
//            }
//            mmSocket = tmp;
//        }
//
//        public void run() {
//            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
//            setName("ConnectThread" + mSocketType);
//
//            // Always cancel discovery because it will slow down a connection
//            mAdapter.cancelDiscovery();
//
//            // Make a connection to the BluetoothSocket
//            try {
//                // This is a blocking call and will only return on a
//                // successful connection or an exception
//                mmSocket.connect();
//            } catch (IOException e) {
//                // Close the socket
//                try {
//                    mmSocket.close();
//                } catch (IOException e2) {
//                    Log.e(TAG, "unable to close() " + mSocketType +
//                            " socket during connection failure", e2);
//                }
//                connectionFailed();
//                return;
//            }
//
//            // Reset the ConnectThread because we're done
//            synchronized (ConstantsService.this) {
//                mConnectThread = null;
//            }
//
//            // Start the connected thread
//            connected(mmSocket, mmDevice, mSocketType);
//        }
//
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
//            }
//        }
//    }
//
//    /**
//     * This thread runs during a connection with a remote device.
//     * It handles all incoming and outgoing transmissions.
//     */
//    private class ConnectedThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//
//        public ConnectedThread(BluetoothSocket socket, String socketType) {
//            Log.d(TAG, "create ConnectedThread: " + socketType);
//            mmSocket = socket;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//            // Get the BluetoothSocket input and output streams
//            try {
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) {
//                Log.e(TAG, "temp sockets not created", e);
//            }
//
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//
//        public void run() {
//            Log.i(TAG, "BEGIN mConnectedThread");
//            byte[] buffer = new byte[1024];
//            int bytes;
//
//            // Keep listening to the InputStream while connected
//            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
//
//                    // Send the obtained bytes to the UI Activity
//                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
//                            .sendToTarget();
//                } catch (IOException e) {
//                    Log.e(TAG, "disconnected", e);
//                    connectionLost();
//                    // Start the service over to restart listening mode
//                    ConstantsService.this.start();
//                    break;
//                }
//            }
//        }
//
//        /**
//         * Write to the connected OutStream.
//         *
//         * @param buffer
//         *         The bytes to write
//         */
//        public void write(byte[] buffer) {
//            try {
//                mmOutStream.write(buffer);
//
//                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
//            } catch (IOException e) {
//                Log.e(TAG, "Exception during write", e);
//            }
//        }
//
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "close() of connect socket failed", e);
//            }
//        }
//    }

}
