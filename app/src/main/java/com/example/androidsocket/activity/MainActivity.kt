package com.example.androidsocket.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.androidsocket.R
import com.example.androidsocket.manager.SocketManager
import com.example.androidsocket.model.BitCoin
import okhttp3.*

class MainActivity : AppCompatActivity() {

    lateinit var mWebSocket: SocketManager
    lateinit var tv_socket: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        tv_socket = findViewById(R.id.tv_socket)
        mWebSocket = SocketManager()
        setUpUI()
    }

    private fun setUpUI() {
        mWebSocket.connectToSocket("live_trades_btcusd")
        mWebSocket.socketListener(object : SocketManager.SocketListener {
            override fun onSuccess(bitcoin: BitCoin) {
                runOnUiThread {
                    if (bitcoin.event == "bts:subscription_succeeded") {
                        Toast.makeText(this@MainActivity, "Successfully Connected, Wait a minute", Toast.LENGTH_SHORT ).show()
                    } else {
                       tv_socket.text = bitcoin.data.amount.toString()
                    }
                }
            }

            override fun onFailure(message: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Successfully Connected, Wait a minute", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }


}