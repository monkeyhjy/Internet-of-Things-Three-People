package com.example.internetofthings.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.ImageView
import com.example.internetofthings.R
import com.example.internetofthings.controlGroup.zigbeecontrol.SensorControl
import com.example.internetofthings.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mSensorControl: SensorControl
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {

            super.handleMessage(msg)
        }
    }
    private var isLed1On = false
    private var isLed2On = false
    private var isLed3On = false
    private var isLed4On = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mSensorControl = SensorControl().apply {
            addLedListener { led_id, led_status -> TODO("Not yet implemented") }
        }
        initEvent()
    }

    private fun initEvent() {
        binding.led1.setOnClickListener {
            isLed1On = !isLed1On
            changeLedImgSource(it as ImageView, isLed1On)
        }

        binding.led2.setOnClickListener {
            isLed2On = !isLed2On
            changeLedImgSource(it as ImageView, isLed2On)
        }

        binding.led3.setOnClickListener {
            isLed3On = !isLed3On
            changeLedImgSource(it as ImageView, isLed3On)
        }

        binding.led4.setOnClickListener {
            isLed4On = !isLed4On
            changeLedImgSource(it as ImageView, isLed4On)
        }
    }

    private fun changeLedImgSource(imageView: ImageView, isLedOn: Boolean) {
        if(isLedOn) {
            imageView.setImageResource(R.drawable.smarthome_led_on)
        }else {
            imageView.setImageResource(R.drawable.smarthome_led_off)
        }
    }
}