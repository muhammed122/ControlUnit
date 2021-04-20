package com.muhammed.controlunit.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.muhammed.controlunit.R
import com.muhammed.controlunit.databinding.FragmentSpecialControlScreenBinding
import com.muhammed.controlunit.helper.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SpecialControlScreen : Fragment() {

    //binding
    private var _binding: FragmentSpecialControlScreenBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var bluetoothManager: MyBluetoothManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_special_control_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSpecialControlScreenBinding.bind(view)

        binding.playBtn.setOnClickListener {
            sendMessage(PLAY_ALL)
        }
        binding.oneBtn.setOnClickListener {
            sendMessage(ONE)
        }
        binding.twoBtn.setOnClickListener {
            sendMessage(TWO)
        }
        binding.threeBtn.setOnClickListener {
            sendMessage(THREE)
        }
        binding.fourBtn.setOnClickListener {
            sendMessage(FOUR)
        }
        binding.fiveBtn.setOnClickListener {
            sendMessage(FIVE)
        }
        binding.sixBtn.setOnClickListener {
            sendMessage(SIX)
        }
        binding.sevenBtn.setOnClickListener {
            sendMessage(SEVEN)
        }
        binding.eightBtn.setOnClickListener {
            sendMessage(EIGHT)
        }
        binding.nineBtn.setOnClickListener {
            sendMessage(NINE)
        }
        binding.tenBtn.setOnClickListener {
            sendMessage(TEN)
        }

        binding.pressHere.setOnClickListener {
          sendMessage(getDate())
        }

    }

    private fun sendMessage(message: String) {
        if (bluetoothManager.isConnected())
            lifecycleScope.launch {
                Log.d("dddddd", "sendMessage: $message")
                bluetoothManager.writeBluetooth(message.toByteArray())
            }
        else
            showSnackBarMessage("No Bluetooth Connection")
    }

    private fun showSnackBarMessage(message: String) {
        val snack = Snackbar.make(binding.parentSpecialLayout, message, Snackbar.LENGTH_LONG)
        snack.show()
    }

    private fun getDate(): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val format = SimpleDateFormat("dd-MM-yyyy")
        return format.format(calendar.time).toString()
    }


}