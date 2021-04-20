package com.muhammed.controlunit.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.muhammed.controlunit.helper.MyBluetoothManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
     fun provideBluetoothAdapter(): BluetoothAdapter {

        return BluetoothAdapter.getDefaultAdapter()
    }


    @Singleton
    @Provides
    fun provideBluetoothManager(@ApplicationContext appContext: Context): MyBluetoothManager {
        return MyBluetoothManager(appContext)
    }


}