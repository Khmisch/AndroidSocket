package com.example.androidsocket.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.androidsocket.R
import com.example.androidsocket.model.BitCoin
import com.example.androidsocket.model.SocketListener
import com.example.androidsocket.model.WebSocketBtc
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

//    lateinit var mWebSocket: SocketManager
    lateinit var mWebSocket: WebSocketBtc
    lateinit var lineChart: LineChart
    var lineValues = ArrayList<Entry>()
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        mWebSocket = WebSocketBtc()
        lineChart = findViewById(R.id.line_chart)
        configureLineChart()
        setUpUI()
    }

    private fun setUpUI() {
        mWebSocket.connectToSocket("live_trades_btcusd")
        mWebSocket.socketListener(object : SocketListener {
            override fun onSuccess(bitcoin: BitCoin) {
                count++
                runOnUiThread {
                    if (bitcoin.event == "bts:subscription_succeeded") {
                        Toast.makeText(this@MainActivity, "Successfully Connected, Wait a minute", Toast.LENGTH_SHORT ).show()
                    } else {
                        lineValues.add(Entry(count.toFloat(),bitcoin.data.price.toFloat()))
                        setLineChartData(lineValues)
                    }
                }
            }

            override fun onFailure(message: String) {
                Log.d("@@@onFailureMain","error")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Successfully Connected, Wait a minute", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun setLineChartData(pricesHigh: ArrayList<Entry>) {
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        val highLineDataSet = LineDataSet(pricesHigh, "Live")
        highLineDataSet.setDrawCircles(true)
        highLineDataSet.circleRadius = 4f
        highLineDataSet.setDrawValues(false)
        highLineDataSet.lineWidth = 3f
        highLineDataSet.color = Color.GREEN
        highLineDataSet.setCircleColor(Color.GREEN)
        dataSets.add(highLineDataSet)

        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun configureLineChart() {
        val desc = Description()
        desc.text = "BTC-USD"
        desc.textSize = 20F
        lineChart.description = desc
        val xAxis: XAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            @SuppressLint("ConstantLocale")
            @RequiresApi(Build.VERSION_CODES.N)
            val mFormat: SimpleDateFormat = SimpleDateFormat("HH mm", Locale.getDefault())
            @RequiresApi(Build.VERSION_CODES.N)
            override fun getFormattedValue(value: Float): String {
                return mFormat.format(Date(System.currentTimeMillis()))
            }
        }
    }


}