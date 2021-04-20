package com.muhammed.controlunit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.github.loadingview.LoadingView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    lateinit var navController: NavController
    lateinit var bottomNavigationView: BottomNavigationView



    private fun initView() {
        bottomNavigationView = findViewById(R.id.bottom_nav)
        navController = Navigation.findNavController(this, R.id.nav_host)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }


}