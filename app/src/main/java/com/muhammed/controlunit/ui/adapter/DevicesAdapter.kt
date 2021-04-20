package com.muhammed.controlunit.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muhammed.controlunit.R
import com.muhammed.controlunit.model.Device

class DevicesAdapter(
    private val itemClickListener: DeviceAdapterClickListener
) :
    RecyclerView.Adapter<DevicesAdapter.DeviceHolder>() {


    private var pairedDevices :List<Device> =ArrayList()
    fun addDevices( pairedDevices :List<Device>){
        this.pairedDevices=pairedDevices
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.device_item, parent, false)
        return DeviceHolder(v)
    }

    override fun getItemCount(): Int {

        return pairedDevices.size
    }

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        val device = pairedDevices[position]

        holder.deviceName.text = device.deviceName
        holder.deviceAddress.text = device.deviceAddress

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(device)
        }


    }

    class DeviceHolder(view: View) : RecyclerView.ViewHolder(view) {

        val deviceName: TextView = view.findViewById(R.id.device_name)
        val deviceAddress: TextView = view.findViewById(R.id.device_address)

    }

}