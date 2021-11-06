package com.example.internetofthings.activities

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.internetofthings.R
import com.example.internetofthings.controlGroup.rfidcontrol.ModulesControl
import com.example.internetofthings.controlGroup.zigbeecontrol.Command
import com.example.internetofthings.controlGroup.zigbeecontrol.SensorControl
import com.example.internetofthings.databinding.ActivityMainBinding
import com.example.internetofthings.service.CardService


class MainActivity : AppCompatActivity() {
    companion object {
        private const val LED_STATUS_MSG = 0x01
        const val TYPE = "RESUME"
        const val ACTION = "RESUME_ACTION"
        const val SP_NAME = "CARD"
    }
    private var rate:Int = 0
    private var id:String? = null
    //存储卡相关数据的sp
    private lateinit var sp:SharedPreferences

    //当前卡的id
    private var currentId: String? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var mSensorControl: SensorControl
    private lateinit var mModulesControl: ModulesControl
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if(rate<0) rate = 0
            Log.d("wangyong", "handleMessage")
            val data = msg.data
            if (id != null && sp.getInt(id, -1) > 0) {
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
                rate = 0
                if(isLed1On) rate++
                if(isLed2On) rate++
                if(isLed3On) rate++
                if(isLed4On) rate++
                binding.speedValue.setText("x" + rate.toString())
                super.handleMessage(msg)
            }else{
                reset_led()
            }
        }
    }
    private val uiHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            Log.d("wangyong", "handle Message")
            val broadCastIntent = Intent(ACTION)
//            when (intent?.extras?.getString("type")) {
//                ChargeActivity.TYPE -> broadCastIntent.action = ChargeActivity.ACTION
//                MainActivity.TYPE -> broadCastIntent.action = MainActivity.ACTION
//                else -> return
//            }
            when (msg?.what) {
                Command.HF_TYPE.toInt() -> {
                    val data = msg.data
                    if (!data.getBoolean("result")) {
                        broadCastIntent.apply {
                            putExtra("what", 1)
                            putExtra(
                                "Result",
                                "设置工作模式失败"
                            )
                        }
                        sendBroadcast(broadCastIntent)
                    }
                }
                Command.HF_FREQ.toInt() -> {
                    val data = msg.data
                    if (!data.getBoolean("result")) {
                        broadCastIntent.apply {
                            putExtra("what", 1)
                            if (data.getBoolean("Result")) {
                                putExtra("Result", "打开射频失败")
                            } else {
                                putExtra("Result", "关闭射频失败")
                            }
                        }
                        sendBroadcast(broadCastIntent)
                    }
                }
                Command.HF_ACTIVE.toInt() -> {
                    val data = msg.data
                    if (!data.getBoolean("result")) {
                        broadCastIntent.apply {
                            putExtra("what", 2)
                        }
                        sendBroadcast(broadCastIntent)
                    }
                }
                Command.HF_ID.toInt() -> {
                    val data = msg.data
                    broadCastIntent.putExtra("what", 3)
                    if (data.getBoolean("result")) {
                        broadCastIntent.putExtra("Result", data.getString("cardNo"))
                        sendBroadcast(broadCastIntent)
                    } else {
                        // do nothing
                    }
                }
                else -> {
                    // do nothing
                }
            }
            super.handleMessage(msg)
        }
    }
    fun reset_led(){
        isLed1On=false
        isLed2On=false
        isLed3On=false
        isLed4On=false
        binding.led1.setImageResource(R.drawable.smarthome_led_off)
        binding.led2.setImageResource(R.drawable.smarthome_led_off)
        binding.led3.setImageResource(R.drawable.smarthome_led_off)
        binding.led4.setImageResource(R.drawable.smarthome_led_off)
        rate=0
        id=null
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val extra = intent?.extras ?: return
            when (extra.getInt("what")) {
                //初始化错误
                1 -> {
                    binding.balanceValue1.setText("未检测到卡片")
                    reset_led()
//                    Toast.makeText(this@MainActivity, "初始化错误", Toast.LENGTH_LONG).show()
                }
                2 -> {
                    binding.balanceValue1.setText("未检测到卡片")
                    reset_led()
//                    Toast.makeText(
//                        this@MainActivity, "未检测到卡", Toast.LENGTH_LONG
//                    ).show()
                }
                //成功获取卡号
                3 -> {
                    currentId = extra.getString("Result")
                    id = currentId
                    if (currentId == null) {
                        //do nothing
                    } else {
//                        Toast.makeText(this@MainActivity, "检测到卡片:"+currentId, Toast.LENGTH_SHORT).show()
                        val money = sp.getInt(currentId,-20)
                        if(money == -20){
                           binding.balanceValue1.setText("请开卡")
                        }else{
                            sp.edit().apply(){
                                putInt(currentId,money-rate)
                                apply()
                            }
                            binding.balanceValue1.setText(money.toString())
                        }
                    }
                }
            }
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
//            Log.d("wangyong", "LedListener")
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
        mSensorControl.actionControl(true)
        mModulesControl.actionControl(true)
        sp = getSharedPreferences(SP_NAME, MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        Log.d("wangyong", "onStart")
        val intentFilter = IntentFilter(ACTION)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        Log.d("wangyong", "onStop")
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(
            Intent(
                this, CardService::class.java
            )
        )
        mSensorControl.actionControl(false)
        mModulesControl.actionControl(false)
        mSensorControl.removeLedListener(ledListener)
        mSensorControl.closeSerialDevice()
        mModulesControl.closeSerialDevice()
    }

    private fun initEvent() {
        mModulesControl = ModulesControl(uiHandler)
        mSensorControl = SensorControl().apply {
            addLedListener(ledListener)
        }
//        binding.led1.setOnClickListener {
//            isLed1On = !isLed1On
//            if(isLed1On){
//                rate++
//                binding.led1.setImageResource(R.drawable.smarthome_led_on)
//            }else{
//                rate--
//                binding.led1.setImageResource(R.drawable.smarthome_led_off)
//            }
//            binding.speedValue.setText("x"+rate.toString())
//        }
//        binding.led2.setOnClickListener {
//            isLed2On=!isLed2On
//            if(isLed2On){
//                rate++
//                binding.led2.setImageResource(R.drawable.smarthome_led_on)
//            }else{
//                rate--
//                binding.led2.setImageResource(R.drawable.smarthome_led_off)
//            }
//            binding.speedValue.setText("x"+rate.toString())
//        }
//        binding.led3.setOnClickListener {
//            isLed3On=!isLed3On
//            if(isLed3On){
//                rate++
//                binding.led3.setImageResource(R.drawable.smarthome_led_on)
//            }else{
//                rate--
//                binding.led3.setImageResource(R.drawable.smarthome_led_off)
//            }
//            binding.speedValue.setText("x"+rate.toString())
//        }
//        binding.led4.setOnClickListener {
//            isLed4On=!isLed4On
//            if(isLed4On){
//                rate++
//                binding.led4.setImageResource(R.drawable.smarthome_led_on)
//            }else{
//                rate--
//                binding.led4.setImageResource(R.drawable.smarthome_led_off)
//            }
//            binding.speedValue.setText("x"+rate.toString())
//        }
        binding.led1.setOnClickListener{
            if (id != null && sp.getInt(id, -1) > 0) {
                if (isLed1On) {
                    mSensorControl.led1_Off(false)
                } else {
                    mSensorControl.led1_On(false)
                }
            }
        }
        binding.led2.setOnClickListener {
            if (id != null && sp.getInt(id, -1) > 0){
                if (isLed2On) {
                    mSensorControl.led2_Off(false)
                } else {
                    mSensorControl.led2_On(false)
                }
            }
        }
        binding.led3.setOnClickListener {
            if (id != null && sp.getInt(id, -1) > 0) {
                if (isLed3On) {
                    mSensorControl.led3_Off(false)
                } else {
                    mSensorControl.led3_On(false)
                }
            }
        }
        binding.led4.setOnClickListener {
            if (id != null && sp.getInt(id, -1) > 0) {
                if (isLed4On) {
                    mSensorControl.led4_Off(false)
                } else {
                    mSensorControl.led4_On(false)
                }
            }
        }
        binding.goToCharge.setOnClickListener {
            startActivity(
                Intent(this, ChargeActivity::class.java)
            )
        }

        startService(
            Intent(this, CardService::class.java).apply {
                putExtra("type", TYPE)
            }
        )
    }
}