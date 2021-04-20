package com.muhammed.controlunit.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.muhammed.controlunit.MainActivity
import com.muhammed.controlunit.R
import com.muhammed.controlunit.databinding.FragmentDialogDevicesBinding
import com.muhammed.controlunit.databinding.FragmentHomeScreenBinding
import com.muhammed.controlunit.helper.LoadingDialog
import com.muhammed.controlunit.helper.MyBluetoothManager
import com.muhammed.controlunit.model.Device
import com.muhammed.controlunit.ui.adapter.DeviceAdapterClickListener
import com.muhammed.controlunit.ui.adapter.DevicesAdapter
import com.victor.loading.rotate.RotateLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DevicesDialogFragment : Fragment(), DeviceAdapterClickListener {


    //binding
    private var _binding: FragmentDialogDevicesBinding? = null
    private val binding get() = _binding!!

    //codes
    private val LOCATION_PERMISSION_CODE = 1
    private val REQUEST_ENABLE_BT = 0


    @Inject
    lateinit var bluetoothManager: MyBluetoothManager

    lateinit var pairedDevicesAdapter: DevicesAdapter
    lateinit var scanDevicesAdapter: DevicesAdapter

    var scanList = ArrayList<Device>()


    private fun enableBluetooth() {
        if (!bluetoothManager.checkDeviceHasBluetooth())
            return
        if (!bluetoothManager.checkBluetoothEnabled()) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetoothManager.checkBluetoothEnabled())
                    showSnackBarMessage("Enabled ")
            } else {
                showSnackBarMessage("Something Wrong")
                enableBluetooth()

            }
        }

    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_CODE
                )
            }
        } else {
            discoverDevices()
        }
    }


    private fun permissionAllowed(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
            return true;
        return false;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    discoverDevices()
                } else {
                    showSnackBarMessage("Need Location Permission To get nearest bluetooth devices")
                }
                return
            }
        }
    }


    private fun discoverDevices() {
        bluetoothManager.discoverDevices()
        scanList.clear()
        binding.scanBtn.isEnabled = false
        binding.scanBtn.text = ""
        binding.rotateloading.start()
    }

    private fun initRecyclers() {

        pairedDevicesAdapter =
            DevicesAdapter(this)
        binding.recyclerPairedDevices.apply {
            adapter = pairedDevicesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        scanDevicesAdapter =
            DevicesAdapter(this)

        binding.recyclerScanDevices.apply {
            adapter = scanDevicesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        scanDevicesAdapter.addDevices(scanList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        requireActivity().registerReceiver(receiver, filter)

        lifecycleScope.launch {
            bluetoothManager.getPairedDevices()

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_devices, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDialogDevicesBinding.bind(view)
        initRecyclers()



        bluetoothManager.pairedDevicesLiveData.observe(viewLifecycleOwner, Observer { data ->
            if (data.size == 0) {
                binding.noDeviceMessage.visibility = View.VISIBLE
                binding.recyclerPairedDevices.visibility = View.GONE
            } else {
                binding.noDeviceMessage.visibility = View.GONE
                binding.recyclerPairedDevices.visibility = View.VISIBLE
                pairedDevicesAdapter.addDevices(data!!)
            }
        })

        bluetoothManager.connectLiveData.observe(viewLifecycleOwner, Observer { state ->
            if (state) {
                showSnackBarMessage("Connected")
                LoadingDialog.hideAlertDialog()
                findNavController().popBackStack()
            } else {
                showSnackBarMessage("unable to connect,Try again..!")
                LoadingDialog.hideAlertDialog()
            }

        })




        binding.scanBtn.setOnClickListener {
            if (!bluetoothManager.checkBluetoothEnabled())
                showSnackBarMessage("Enable bluetooth first")
            else {
                if (permissionAllowed()) {
                    discoverDevices()
                } else
                    checkLocationPermission()
            }
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address

                    val dev: Device?
                    dev = if (deviceName == null) {
                        Device("New Device ", deviceHardwareAddress!!)
                    } else {
                        Device(deviceName, deviceHardwareAddress!!)
                    }

                    scanList.add(dev)
                    scanDevicesAdapter.notifyDataSetChanged()
                }
                ACTION_DISCOVERY_FINISHED -> {
                    binding.scanBtn.isEnabled = true
                    binding.scanBtn.text = "Scan"
                    binding.rotateloading.stop()
                }
                ACTION_STATE_CHANGED -> {

                    val state =
                        intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> {
                            Toast.makeText(
                                requireContext(),
                                "You can not turn OFF bluetooth while Search is running",
                                Toast.LENGTH_LONG
                            ).show()
//                            requireActivity().finish()
                        }
                        BluetoothAdapter.STATE_ON -> {
                            return
                        }
                    }

                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(receiver)
    }

    private fun showSnackBarMessage(message: String) {
        val snack = Snackbar.make(binding.connectionLayout, message, Snackbar.LENGTH_LONG)
        snack.show()
    }


    override fun onItemClick(device: Device) {
        LoadingDialog.showDialog(requireActivity())
        lifecycleScope.launch {
            bluetoothManager.connectToDevice(device)
        }
    }


}