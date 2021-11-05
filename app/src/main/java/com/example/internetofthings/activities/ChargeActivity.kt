package com.example.internetofthings.activities

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.example.internetofthings.controlGroup.rfidcontrol.ModulesControl
import com.example.internetofthings.controlGroup.zigbeecontrol.Command
import com.example.internetofthings.databinding.ActivityChargeBinding
import com.example.internetofthings.service.CardService

class ChargeActivity : AppCompatActivity() {
    companion object {
        const val TYPE = "CHARGE"
        const val ACTION = "CHARGE_ACTION"
    }
    private lateinit var mModulesControl: ModulesControl
    private lateinit var binding: ActivityChargeBinding

    //存储卡相关数据的sp
    private lateinit var sp:SharedPreferences
    private var id:String? = null
    //当前卡的id
    private var currentId: String? = null
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val extra = intent?.extras ?: return

            when (extra.getInt("what")) {
                //初始化错误
                1 -> {
                    id = null
                    binding.balanceValue2.setText("未检测到卡片")
//                    Toast.makeText(this@ChargeActivity, "初始化错误", Toast.LENGTH_LONG).show()
                }
                2 -> {
                    id = null
                    binding.balanceValue2.setText("未检测到卡片")
//                    Toast.makeText(
//                        this@ChargeActivity, "未检测到卡", Toast.LENGTH_LONG
//                    ).show()
                }
                //成功获取卡号
                3 -> {
                    currentId = extra.getString("Result")
                    if (currentId == null) {

                        //do nothing
                    } else {
                        id = currentId
                        val money = sp.getInt(id,-1)
                        if(money==-1){
                            binding.balanceValue2.setText("请开卡")
                        }else {
                            binding.balanceValue2.setText(money.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEvent()
        sp = getSharedPreferences(MainActivity.SP_NAME, MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        mModulesControl.actionControl(true)
        val intentFilter = IntentFilter(MainActivity.ACTION)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        mModulesControl.actionControl(false)
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        mModulesControl.closeSerialDevice()
//        stopService(
//            Intent(
//                this, CardService::class.java
//            )
//        )
    }

    private val uiHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            Log.d("wangyong", "handle Message")
            val broadCastIntent = Intent(MainActivity.ACTION)
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

    private fun initEvent() {
        mModulesControl = ModulesControl(uiHandler)
        binding.back.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
//        startService(
//            Intent(this, CardService::class.java).apply {
//                putExtra("type", TYPE)
//            }
//        )
        binding.applyCard.setOnClickListener {
            if(id!=null&&sp.getInt(id,-1)==-1) {
                sp.edit().apply {
                    putInt(id, 0)
                    apply()
                }
                Toast.makeText(this@ChargeActivity, "开卡成功", Toast.LENGTH_LONG).show()
            }
        }
        binding.destroyCard.setOnClickListener {
            if(sp.getInt(id,-1)!=-1) {
                sp.edit().apply {
                    remove(id)
                    apply()
                }
                Toast.makeText(this@ChargeActivity, "销卡成功", Toast.LENGTH_LONG).show()
            }
        }
        binding.pay.setOnClickListener {
            if(sp.getInt(id,-1)!=-1) {
                val old_money: Int = sp.getInt(id, -1)
                sp.edit().apply() {
                    putInt(id, old_money + Integer.parseInt(binding.money.text.toString()))
                    apply()
                }
                binding.money.setText("")
                Toast.makeText(this@ChargeActivity, "充值成功", Toast.LENGTH_LONG).show()
            }
        }
    }
}