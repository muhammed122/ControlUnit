package com.muhammed.controlunit.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.muhammed.controlunit.R
import com.muhammed.controlunit.databinding.FragmentHomeScreenBinding
import com.muhammed.controlunit.helper.*
import com.muhammed.controlunit.model.Device
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HomeScreen : Fragment() {

    private val SPEECH_REC_CODE = 102

    //binding
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    //codes
    private val LOCATION_PERMISSION_CODE = 1
    private val REQUEST_ENABLE_BT = 0


    @Inject
    lateinit var bluetoothManager: MyBluetoothManager


    private fun enableBluetooth() {
        if (!bluetoothManager.checkDeviceHasBluetooth()) {
            showSnackBarMessage("This Device Not Support Bluetooth")
            requireActivity().finish()
            return
        }
        if (!bluetoothManager.checkBluetoothEnabled()) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableBluetooth()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeScreenBinding.bind(view)


        binding.connectionIcon.setOnClickListener {
            bluetoothConnection()
        }

        binding.shutDown.setOnClickListener {
            sendMessage(SHUTDOWN)

        }

        binding.sprayIcon.setOnClickListener {
            sendMessage(SPRAY)
        }

        binding.fLightIcon.setOnClickListener {
            sendMessage(FrontLIGHT)
        }

        binding.voiceDownIcon.setOnClickListener {
            sendMessage(VOLUME_DOWN)
        }
        binding.voiceUpIcon.setOnClickListener {
            sendMessage(VOLUME_UP)
        }
        binding.deviceTwoIcon.setOnClickListener {
            sendMessage(DEVICE_Two)
        }

        binding.muteIcon.setOnClickListener {
            sendMessage(MUTE)
        }


        binding.bLightIcon.setOnClickListener {
            sendMessage(BACK_LIGHT)
        }

        binding.voiceCmdIcon.setOnClickListener {
            if (!bluetoothManager.isConnected())
                Toast.makeText(requireContext(), "No Bluetooth Connection ", Toast.LENGTH_SHORT)
                    .show()
            else
                speechService()
        }

        binding.deviceOneIcon.setOnClickListener {
            sendMessage(DEVICE_ONE)
        }


        binding.crazyIcon.setOnClickListener {
            sendMessage(CRAZY)
        }

        binding.fbOff.setOnClickListener {
            sendMessage(FEEDBACKOFF)
        }


        binding.fbSound.setOnClickListener {
            sendMessage(FEEDBACKSOUND)
        }

        binding.fbTone.setOnClickListener {
            sendMessage(FEEDBACKTONE)
        }

        binding.sModeIcon.setOnClickListener {
            sendMessage(SPRAY_MODE)
        }


        binding.bAutoIcon2.setOnClickListener {
            sendMessage(BACK_LIGHT_AUTO)
        }

        binding.fAutoIcon.setOnClickListener {
            sendMessage(FrontLIGHTAUTO)
        }


    }

    private fun showSnackBarMessage(message: String) {
        val snack = Snackbar.make(binding.homeLayout, message, Snackbar.LENGTH_LONG)
        snack.show()
    }

    private fun sendMessage(message: String) {
        if (bluetoothManager.isConnected()) {
            lifecycleScope.launch {
                Log.d("dddddd", "sendMessage: $message")
                bluetoothManager.writeBluetooth(message.toByteArray())

            }
            if (message == SHUTDOWN)
                if (bluetoothManager.isConnected()) {
                    bluetoothManager.closeBluetooth()
                }

        } else
            showSnackBarMessage("No Bluetooth Connection")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetoothManager.checkBluetoothEnabled())
                    showSnackBarMessage("Enabled ")
            } else {
                Toast.makeText(requireContext(), "Enable Bluetooth to use App ", Toast.LENGTH_LONG)
                    .show()
                requireActivity().finish()
            }
        } else if (requestCode == SPEECH_REC_CODE && resultCode == Activity.RESULT_OK) {
            val res = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            sendMessage(res?.get(0).toString())
        }

    }


    private fun checkLocationPermission() {

        if (permissionAllowed())
        //    bluetoothManager.startBluetoothScan()
            bluetoothManager.discoverDevices()
        else
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

                bluetoothManager.discoverDevices()
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
                    bluetoothManager.discoverDevices()
                } else {
                    showSnackBarMessage("You ")
                }
                return
            }
        }
    }


    private fun speechService() {

        if (!SpeechRecognizer.isRecognitionAvailable(requireContext()))
            Toast.makeText(requireContext(), "Voice command is not available", Toast.LENGTH_SHORT)
                .show()
        else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say your command!")
            startActivityForResult(intent, SPEECH_REC_CODE)
        }

    }


    // Create a BroadcastReceiver for ACTION_FOUND.
//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.action) {
//                BluetoothDevice.ACTION_FOUND -> {
//                    // Discovery has found a device. Get the BluetoothDevice
//                    // object and its info from the Intent.
//                    val device: BluetoothDevice? =
//                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                    val deviceName = device?.name
//                    val deviceHardwareAddress = device?.address // MAC address
//
////                    runBlocking {
////                        bluetoothManager.connectToDevice(
////                            Device(
////                                deviceName!!,
////                                deviceHardwareAddress!!
////                            )
////                        )
////                    }
//
//                    bluetoothManager.connect(device!!)
//
//                }
//            }
//        }
//    }


    private fun bluetoothConnection() {
        if (bluetoothManager.checkBluetoothEnabled()) {
            if (!bluetoothManager.isConnected())
                startActivity(Intent(requireActivity(), ConnectionActivity::class.java))
            else
                showSnackBarMessage("Already Connected")
        } else
            showSnackBarMessage("Bluetooth Not Enabled")
    }


//    private fun observes() {
//        bluetoothManager.devicesLiveData.observe(
//            requireActivity(),
//            androidx.lifecycle.Observer { list ->
//
//                val bundle = Bundle()
//                bundle.putSerializable("devices", list)
//                findNavController().navigate(
//                    R.id.action_homeScreen_to_devicesDialogFragment2,
//                    bundle
//                )
//
//
//            })

//        bluetoothManager.messageLiveData.observe(
//            requireActivity(),
//            androidx.lifecycle.Observer { message ->
//                showSnackBarMessage(message)
//            })


}


//    private fun checkBTPermissions() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            var permissionCheck: Int =
//                requireActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
//            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
//            if (permissionCheck != 0) {
//                requestPermissions(
//                    arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ), 1001
//                ) //Any number
//            }
//        } else {
//            Log.d(
//                "dddddddd",
//                "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP."
//            )
//        }
//    }


