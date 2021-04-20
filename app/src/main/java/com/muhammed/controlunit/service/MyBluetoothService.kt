package com.muhammed.controlunit.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class MyBluetoothService(private val handler: Handler) {

    private val TAG = "MyBluetoothService"
    companion object {
        private val NAME_SECURE = "BluetoothChatSecure"
        private val NAME_InSECURE = "BluetoothChatInSecure"
        private val MY_SECURE_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66")
        private val MY_INSECURE_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

        val STATE_NONE = 0
        val STATE_LISTEN = 1
        val STATE_CONNECTING = 2
        val STATE_CONNECTED = 3
    }

    private val mAdapter: BluetoothAdapter
    private var mHandler: Handler? = null

    //    private val mSecureAcceptThread: AcceptThread? = null
//    private val mInsecureAcceptThread: AcceptThread? = null
//    private val mConnectThread: ConnectThread? = null
//    private val mConnectedThread: ConnectedThread? = null
    private var mState = 0
    private var mNewState = 0

    init {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState =
            STATE_NONE;
        mNewState = mState;
        mHandler = handler
    }

    /**
     * Update UI title according to the current state of the chat connection
     */
//    @Synchronized
//    private fun updateUserInterfaceTitle() {
//        mState = getState()
//        Log.d(TAG, "updateUserInterfaceTitle() $mNewState -> $mState")
//        mNewState = mState
//
//        // Give the new state to the Handler so the UI Activity can update
//        mHandler!!.obtainMessage(Constants.MESSAGE_STATE_CHANGE, mNewState, -1).sendToTarget()
//    }

    /**
     * Return the current connection state.
     */
    @Synchronized
    fun getState(): Int {
        return mState
    }


    inner class ConnectedThread(
        private val socket: BluetoothSocket,
        private val socketType: String
    ) : Thread() {


        private val mmSocket: BluetoothSocket
        private val mmInStream: InputStream
        private val mmOutStream: OutputStream

        init {

            mmSocket = socket
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (e: IOException) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn!!;
            mmOutStream = tmpOut!!
        }

    }
}
//          MyBluetoothService.mState = STATE_CONNECTED;
//        }
//
//
//
//        public void run()
//        {
//            Log.i(TAG, "BEGIN mConnectedThread");
//            byte[] buffer = new byte[1024];
//            int bytes;
//
//            // Keep listening to the InputStream while connected
//            while (mState == STATE_CONNECTED) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
//
//                    // Send the obtained bytes to the UI Activity
//                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
//                        .sendToTarget();
//                } catch (IOException e) {
//                    Log.e(TAG, "disconnected", e);
//                    connectionLost();
//                    break;
//                }
//            }
//        }


