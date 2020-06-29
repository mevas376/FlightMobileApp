package com.example.flightmobileapp
import Api
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flightmobileapp.MainActivity.Companion.bitmap
import com.google.gson.GsonBuilder
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.joystick.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Joystick : AppCompatActivity() {
    private var lastElevator: Double = 0.0
    private var lastAileron: Double = 0.0
    private var lastThrottle: Double = 0.0
    private var lastRudder: Double = 0.0
    private var changeImage = false
    private var url : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.joystick)
        url = intent.getStringExtra("url").toString()
       // val imageView = findViewById<ImageView>(R.id.server_screenshot)
      //  imageView.setImageBitmap(MainActivity.bitmap)
        setJoystick()
        setThrottle()
        setRudder()
        CoroutineScope(IO).launch { setCommand() }
        onStart()
    }


    private fun imageRequest() {
        changeImage = true
        CoroutineScope(IO).launch {
            while (changeImage) {
                getSimulatorScreen()
                delay(250)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!changeImage) {
            imageRequest()
        }
    }

    override fun onResume() {
        super.onResume()
        if(!changeImage) {
            imageRequest()
        }
    }

    override fun onPause() {
        super.onPause()
        if (changeImage) {
            changeImage = false
        }
    }

    override fun onStop() {
        super.onStop()
        changeImage = false
    }

    fun setCommand() {
        val json=
            "{\"aileron\": $lastAileron,\n \"rudder\": $lastRudder,\n \"elevator\": $lastElevator,\n \"throttle\": $lastThrottle\n}"
        val rb: RequestBody = RequestBody.create(MediaType.parse("application/json"), json)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(url.toString())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        println("URL:"+url)
        api.postControl(rb).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (!changeImage) {
                    return
                }
                Toast.makeText(applicationContext, "Failed to ",
                    Toast.LENGTH_SHORT).show()
                return
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!changeImage) {
                    return
                }
                if (response.code() != 200) {
                    val message = getResponseMessage(response)
                    if (message == "Failed to update command") {
                        Toast.makeText(applicationContext, message,
                            Toast.LENGTH_SHORT).show()
                    }
                }
                println("Response Code:"+response.code())
            }
        })
    }

    fun showMessage(message : String) {
        Toast.makeText(applicationContext, message + "\nyou can return the login page",
            Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private  fun  getSimulatorScreen() {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(this.url.toString())
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
        val api = retrofit.create(Api::class.java)
        api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (!changeImage) {
                    return
                }
                if (t.message?.contains("timeout") == true) {
                    Toast.makeText(applicationContext, "Timeout request",
                        Toast.LENGTH_SHORT).show()
                } else {
                    showMessage("Connection failed")
                }
                return
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!changeImage) {
                    return
                }
                val inputstream = response.body()?.byteStream()
                if (inputstream == null) {
                    val message = getResponseMessage(response)
                    showMessage(message)
                }
                val bitmap = BitmapFactory.decodeStream(inputstream)
                runOnUiThread {
                    val imageView = findViewById<ImageView>(R.id.server_screenshot)
                    imageView.setImageBitmap(bitmap)
                }
            }
        })

    }

    companion object {
        fun getResponseMessage(response: Response<ResponseBody>): String {
            val reader: BufferedReader?
            val sb = StringBuilder()
            try {
                reader =
                    BufferedReader(InputStreamReader(response.errorBody()!!.byteStream()))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val finallyError = sb.toString()
            return finallyError
        }
    }
    private fun setJoystick(){
        findViewById<JoystickView>(R.id.joystickView).setOnMoveListener { angle, strength ->
            val angleRad = Math.toRadians(angle.toDouble())
            val baseRadius: Double = ((240 * 80) / 200).toDouble()
            val joyRadius: Double = (strength * baseRadius) / 100
            //calculate
            val y: Double = joyRadius * sin(angleRad)
            val x: Double = joyRadius * cos(angleRad)
            //normalize the value between 0 to 1
            //elevator
            val normalizeY: Double = y / baseRadius
            //aileron
            val normalizeX: Double = x / baseRadius
            if ((abs(normalizeY - lastElevator) > 0.02) || (abs(normalizeX - lastAileron) > 0.02)) {
                lastElevator = normalizeY
                lastAileron = normalizeX
                CoroutineScope(IO).launch { setCommand() }            }
        }
    }

    private fun setThrottle(){
        val throttleSB = findViewById<SeekBar>(R.id.throttleSeekBar)
        throttleSB.max = 100
        val throttleText = findViewById<TextView>(R.id.thrText)
        throttleSB?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val num = (progress.toDouble() / 100)
                if (num == 0.0) {
                    throttleText.text = "0"
                } else {
                    throttleText.text = String.format("%.2f", num).toDouble().toString()
                }
                if (abs(lastThrottle - num) > 0.01) {
                    lastThrottle = num
                    CoroutineScope(IO).launch { setCommand() }                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
            }
        })
    }

    private fun  setRudder(){
        val rudderSB = findViewById<SeekBar>(R.id.rudderSeekBar)
        rudderSB.max = 200
        val rudderText = findViewById<TextView>(R.id.rudText)
        rudderSB?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val num = (progress.toDouble() / 100) - 1
                if (num == 0.0) {
                    rudderText.text = "0"
                } else {
                    rudderText.text = String.format("%.2f", num).toDouble().toString()
                }

                if (abs(lastRudder - num) > 0.02) {
                    lastRudder = num
                    CoroutineScope(IO).launch { setCommand() }                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
            }
        })

    }
}



