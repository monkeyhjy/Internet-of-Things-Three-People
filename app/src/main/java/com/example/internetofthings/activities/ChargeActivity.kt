package com.example.internetofthings.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.internetofthings.databinding.ActivityChargeBinding
import com.example.internetofthings.service.CardService

class ChargeActivity : AppCompatActivity() {
    companion object {
        const val TYPE = "CHARGE"
        const val ACTION = "CHARGE_ACTION"
    }

    private lateinit var binding: ActivityChargeBinding

    //存储卡相关数据的sp
    private val sp = this.getSharedPreferences(MainActivity.SP_NAME, MODE_PRIVATE)

    //当前卡的id
    private var currentId: String? = null
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val extra = intent?.extras ?: return
            when (extra.getInt("what")) {
                //初始化错误
                1 -> {
                    Toast.makeText(this@ChargeActivity, "初始化错误", Toast.LENGTH_LONG).show()
                }
                2 -> {
                    Toast.makeText(
                        this@ChargeActivity, "未检测到卡", Toast.LENGTH_LONG
                    ).show()
                }
                //成功获取卡号
                3 -> {
                    currentId = extra.getString("Result")
                    if (currentId == null) {
                        //do nothing
                    } else {
                        Toast.makeText(this@ChargeActivity, "检测到卡片", Toast.LENGTH_LONG).show()
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
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(ACTION)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(
            Intent(
                this, CardService::class.java
            )
        )
    }

    private fun initEvent() {
        binding.back.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
        startService(
            Intent(this, CardService::class.java).apply {
                putExtra("type", TYPE)
            }
        )
    }

    private fun queryCard(cardId: String) {
        if (sp.getInt(currentId, -1) == -1) {
            //没有记录，请先开卡授权
            Toast.makeText(this, "没有记录,请先开卡授权", Toast.LENGTH_LONG).show()
        } else {

        }
    }
}