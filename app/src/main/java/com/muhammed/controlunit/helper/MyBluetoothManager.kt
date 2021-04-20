package com.muhammed.controlunit.helper

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.drovox.captinapp.utils.SingleLiveEvent
import com.muhammed.controlunit.model.Device
import com.muhammed.controlunit.model.DeviceList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class MyBluetoothManager(private val context: Context) {

    private val _connectLiveData = SingleLiveEvent<Boolean>();
    val connectLiveData: SingleLiveEvent<Boolean> = _connectLiveData


    var soket: BluetoothSocket? = null

    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val _pairedDevicesLiveData = MutableLiveData<DeviceList>()
    val pairedDevicesLiveData: LiveData<DeviceList> = _pairedDevicesLiveData


    val bluetoothAdapter: BluetoothAdapter by lazy {
        (context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }


    private inner class ConnectedThread(private val bluetoothDevice: BluetoothDevice) : Thread() {
        var tmp: BluetoothSocket? = null

        init {
            Log.d("dddd  name", "${bluetoothDevice.address}: ")
            try {
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)
            } catch (e: Exception) {
                Log.d("dddd", "error create: ")
            }

            soket = tmp
        }

        override fun run() {
            Log.d("dddddddd", "connectToDevice:  ${bluetoothDevice.name}")
            if (bluetoothAdapter.isDiscovering)
                bluetoothAdapter.cancelDiscovery()
            try {
                soket!!.connect()
            } catch (e: Exception) {
                Log.d("ddddddddd", "run: coonection failded ${e.localizedMessage}")
                try {

                    soket!!.close()
                    return
                } catch (e: Exception) {
                    Log.d("ddddddddd", "run:  fail closed ${e.localizedMessage}")
                }
            }

            Log.d("ddddddddddddd", "run: connected")

        }

    }

    @Synchronized
    fun connect(bluetoothDevice: BluetoothDevice) {
        val connectedThread = ConnectedThread(bluetoothDevice)
        connectedThread.start()

    }


    fun checkDeviceHasBluetooth(): Boolean {
        if (bluetoothAdapter == null) {
            return false
        }
        return true
    }


    fun checkBluetoothEnabled(): Boolean {
        if (bluetoothAdapter.isEnabled) {
            return true
        }
        mmSocket=null
        return false
    }


    fun closeBluetooth() {
        bluetoothAdapter.disable()
        mmSocket?.close()
    }

    suspend fun getPairedDevices() = withContext(IO) {
        val devices = bluetoothAdapter.bondedDevices
        val deviceList = DeviceList()

        for (device in devices) {
            deviceList.add(Device(device.name, device.address))
        }
        _pairedDevicesLiveData.postValue(deviceList)

    }


    fun discoverDevices() {
        if (bluetoothAdapter.isDiscovering)
            bluetoothAdapter.cancelDiscovery()
        bluetoothAdapter.startDiscovery()
    }


    private val _messageLiveData = MutableLiveData<String>();
    val messageLiveData: LiveData<String> = _messageLiveData
    suspend fun writeBluetooth(data: ByteArray?) = withContext(Dispatchers.IO) {
        if (mmSocket != null) {
            try {
                mmSocket?.outputStream?.write(data)
            } catch (e: IOException) {

            }
        }
    }

    private var mmSocket: BluetoothSocket? = null
    suspend fun connectToDevice(device: Device) = withContext(IO) {
        val dev = bluetoothAdapter.getRemoteDevice(device.deviceAddress)
        try {
            if (mmSocket == null) {
                mmSocket = dev.createRfcommSocketToServiceRecord(uuid)
                bluetoothAdapter.cancelDiscovery()
            }
        } catch (e: IOException) {

        }
        try {
            mmSocket!!.connect()

        } catch (e: IOException) {
            _connectLiveData.postValue(false)
            try {
                mmSocket!!.close()
                mmSocket =null
            } catch (exception: IOException) {

            }
        }
        if (mmSocket != null && mmSocket!!.isConnected) {

            _connectLiveData.postValue(true)
        }


    }

    fun isConnected(): Boolean {
        return mmSocket != null && mmSocket!!.isConnected
    }





}


