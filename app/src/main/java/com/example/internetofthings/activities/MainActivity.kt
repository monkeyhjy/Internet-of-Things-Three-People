package com.example.internetofthings.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.internetofthings.R
import com.example.internetofthings.controlGroup.zigbeecontrol.SensorControl
import com.example.internetofthings.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    companion object {
        private const val LED_STATUS_MSG = 0x01
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSensorControl: SensorControl
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Log.d("wangyong", "handleMessage")
            val data = msg.data
            when (msg.what) {
                LED_STATUS_MSG -> {
                    when (data.getInt("led_id")) {
                        0x01 -> {
                            isLed1On = if (data.getInt("led_status") == 0x01) {
                                binding.led1.setImageResource(R.drawable.smarthome_led_on)
                                true
                            } else {
                                binding.led1.setImageResource(R.drawable.smarthome_led_off)
                                false
                            }
                        }
                        0x02 -> {
                            isLed2On = if (data.getInt("led_status") == 0x01) {
                                binding.led2.setImageResource(R.drawable.smarthome_led_on)
                                true
                            } else {
                                binding.led2.setImageResource(R.drawable.smarthome_led_off)
                                false
                            }
                        }
                        0x03 -> {
                            isLed3On = if (data.getInt("led_status") == 0x01) {
                                binding.led3.setImageResource(R.drawable.smarthome_led_on)
                                true
                            } else {
                                binding.led3.setImageResource(R.drawable.smarthome_led_off)
                                false
                            }
                        }
                        0x04 -> {
                            isLed4On = if (data.getInt("led_status") == 0x01) {
                                binding.led4.setImageResource(R.drawable.smarthome_led_on)
                                true
                            } else {
                                binding.led4.setImageResource(R.drawable.smarthome_led_off)
                                false
                            }
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
                else -> {
                    //do nothing
                }
            }
            super.handleMessage(msg)
        }
    }
    private val ledListener =
        SensorControl.LedListener { led_id, led_status ->
            val bundle = Bundle().apply {
                putInt("led_id", led_id.toInt())
                putInt("led_status", led_status.toInt())
            }
            val msg = Message().apply {
                what = LED_STATUS_MSG
                data = bundle
            }
            mHandler.sendMessage(msg)
            Log.d("wangyong", "LedListener")
        }
    private var isLed1On = false
    private var isLed2On = false
    private var isLed3On = false
    private var isLed4On = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEvent()
    }

    override fun onStart() {
        super.onStart()
        Log.d("wangyong", "onStart")
        mSensorControl.actionControl(true)
    }

    override fun onStop() {
        super.onStop()
        Log.d("wangyong", "onStop")
        mSensorControl.actionControl(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSensorControl.removeLedListener(ledListener)
        mSensorControl.closeSerialDevice()
    }

    private fun initEvent() {
        mSensorControl = SensorControl().apply {
            addLedListener(ledListener)
        }
        binding.led1.setOnClickListener {
            if (isLed1On) {
                mSensorControl.led1_Off(false)
            } else {
                mSensorControl.led1_On(false)
            }
        }
        binding.led2.setOnClickListener {
            if (isLed2On) {
                mSensorControl.led2_Off(false)
            } else {
                mSensorControl.led2_On(false)
            }
        }
        binding.led3.setOnClickListener {
            if (isLed3On) {
                mSensorControl.led3_Off(false)
            } else {
                mSensorControl.led3_On(false)
            }
        }
        binding.led4.setOnClickListener {
            if (isLed4On) {
                mSensorControl.led4_Off(false)
            } else {
                mSensorControl.led4_On(false)
            }
        }
        binding.goToCharge.setOnClickListener {
            startActivity(
                Intent(this, ChargeActivity::class.java)
            )
        }
    }
}
