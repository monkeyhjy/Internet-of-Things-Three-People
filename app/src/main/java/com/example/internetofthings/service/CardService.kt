package com.example.internetofthings.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import com.example.internetofthings.activities.ChargeActivity
import com.example.internetofthings.activities.MainActivity
import com.example.internetofthings.controlGroup.rfidcontrol.ModulesControl
import com.example.internetofthings.controlGroup.zigbeecontrol.Command

class CardService : Service() {
    private lateinit var mModuleController: ModulesControl

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uiHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message?) {
                val broadCastIntent = Intent()
                when (intent?.extras?.getString("type")) {
                    ChargeActivity.TYPE -> broadCastIntent.action = ChargeActivity.ACTION
                    MainActivity.TYPE -> broadCastIntent.action = MainActivity.ACTION
                    else -> return
                }
                when (msg?.what) {
                    Command.HF_TYPE.toInt() -> {
                        val data = msg.data
                        if (!data.getBoolean("result")) {
                            intent.apply {
                                putExtra("what", 1)
                                putExtra(
                                    "Result",
                                    "设置工作模式失败"
                                )
                            }
                            sendBroadcast(intent)
                        }
                    }
                    Command.HF_FREQ.toInt() -> {
                        val data = msg.data
                        if (!data.getBoolean("result")) {
                            intent.apply {
                                putExtra("what", 1)
                                if (data.getBoolean("Result")) {
                                    putExtra("Result", "打开射频失败")
                                } else {
                                    putExtra("Result", "关闭射频失败")
                                }
                            }
                            sendBroadcast(intent)
                        }
                    }
                    Command.HF_ACTIVE.toInt() -> {
                        val data = msg.data
                        if (!data.getBoolean("result")) {
                            intent.apply {
                                putExtra("what", 2)
                            }
                            sendBroadcast(intent)
                        }
                    }
                    Command.HF_ID.toInt() -> {
                        val data = msg.data
                        intent.putExtra("what", 3)
                        if (data.getBoolean("result")) {
                            intent.putExtra("Result", data.getString("cardNo"))
                            sendBroadcast(intent)
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
        mModuleController = ModulesControl(uiHandler)
        mModuleController.actionControl(true)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mModuleController.actionControl(false)
        mModuleController.closeSerialDevice()
    }
}