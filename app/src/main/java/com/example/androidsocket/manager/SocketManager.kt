package com.example.androidsocket.manager

import android.util.Log
import com.example.androidsocket.model.BitCoin
import com.example.androidsocket.model.Currency
import com.example.androidsocket.model.DataSend
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString

class SocketManager {

    lateinit var mWebSocket: WebSocket
    lateinit var socketListener: SocketListener
    var gson = Gson()

    fun connectToSocket(currency: String) {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url("wss://ws.bitstamp.net").build()
        client.newWebSocket(request, object:WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                mWebSocket = webSocket
                webSocket.send(gson.toJson(Currency("bts:subscribing", DataSend(currency))))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("@@@onMessage", "Receiving : $text")
                val bitCoin = gson.fromJson(text, BitCoin::class.java)
                socketListener.onSuccess(bitCoin)

            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("@@@onMessage", "Receiving bytes: $bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("@@@onClosing", "Closing : $code / $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("@@@onFailure", "${t.localizedMessage} and $response" )
                socketListener.onFailure(t.localizedMessage)
            }
        })
        client.dispatcher().executorService().shutdown()
    }

    fun socketListener(socketListener: SocketListener) {
        this.socketListener = socketListener
    }

    interface SocketListener {
        fun onSuccess(bitcoin: BitCoin)
        fun onFailure(message: String)
    }
}