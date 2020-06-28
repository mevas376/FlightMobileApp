package com.example.flightmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import Api
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
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
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
var isLocal= false
//private val uiScope = CoroutineScope(Dispatchers.Main)

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var bitmap: Bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //function for the connect button:
        findViewById<Button>(R.id.connect_button).setOnClickListener(){
            //CoroutineScope(Dispatchers.IO).launch {
            var urlText = typeUrl.text.toString()
            var db:AppDB=AppDB.getInstance(this)

                //if text box is empty: user needs to insert something
                if(urlText.equals("")){
                    Toast.makeText(
                        this@MainActivity,
                        "please type url, or click one of the local hosts buttons.", Toast.LENGTH_SHORT
                    ).show()
                } else {//otherwise textbox is not empty:

                    if (isLocal) { //if local host is pressed
                        CoroutineScope(Dispatchers.IO).launch {
                            db.urlDAO().updateUrl(urlText, System.currentTimeMillis())
                        }
                        //connect to server
                        tryConnectToServer(urlText)

                    } else { //if typed new url
                        var uUrl = typeUrl.text.toString()
                        var url1 = UrlEntity()
                        url1.url_name = uUrl
                        url1.URL_Date = System.currentTimeMillis()

                        CoroutineScope(Dispatchers.IO).launch {
                            db.urlDAO().saveUrl(url1)
                        }
                        //connect to server
                        tryConnectToServer(uUrl)
                    }
                //}
            }
        }

        //function LISTENER for the button URL1:
        findViewById<Button>(R.id.url1).setOnClickListener(){

            var db: AppDB = AppDB.getInstance(this)

            CoroutineScope(Dispatchers.IO).launch {
                val listUrls = db.urlDAO().getRecentUrl()

                //check if there is no url in the database
                if (listUrls.isEmpty()) {
                    var a = 12
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "No url in local host, please try again", Toast.LENGTH_SHORT
                        ).show()
                    }


                } else { //if there is url in the database at this place:
                    val url1string = listUrls[0]

                    //displaying the url in the edit text:
                    val editText = findViewById<EditText>(R.id.typeUrl)
                    editText.text = Editable.Factory.getInstance().newEditable(url1string)

                    //updating the url date in the database:
                        db.urlDAO().updateUrl(url1string, System.currentTimeMillis())

                }
            }
        }

        //function LISTENER for the button URL2:
        findViewById<Button>(R.id.url2).setOnClickListener(){

            var db: AppDB = AppDB.getInstance(this)

            CoroutineScope(Dispatchers.IO).launch {
                val listUrls = db.urlDAO().getRecentUrl()

                //check if there is no url in the database at this place:
                if (listUrls.size < 2) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "No url in local host, please try again", Toast.LENGTH_SHORT
                        ).show()
                    }

                }else{ //if there url in the database at thid place:
                    val url1string = listUrls[1]
                    //displaying the url in the edit text:
                    val editText = findViewById<EditText>(R.id.typeUrl)
                    editText.text = Editable.Factory.getInstance().newEditable(url1string)

                    //updating the url date in the database:
                        db.urlDAO().updateUrl(url1string, System.currentTimeMillis())
                }
            }
        }

        //function LISTENER for the button URL3:
        findViewById<Button>(R.id.url3).setOnClickListener(){

            var db: AppDB = AppDB.getInstance(this)

            CoroutineScope(Dispatchers.IO).launch {
                val listUrls = db.urlDAO().getRecentUrl()

                //val url1string = db.urlDAO().getRecentUrl()[2]
                //check if there is no url in the database
                if (listUrls.size < 3) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "No url in local host, please try again", Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{ //if there url in the database at third place:
                    val url1string = listUrls[2]

                    //displaying the url in the edit text:
                    val editText = findViewById<EditText>(R.id.typeUrl)
                    editText.text = Editable.Factory.getInstance().newEditable(url1string)

                    //updating the url date in the database:
                        db.urlDAO().updateUrl(url1string, System.currentTimeMillis())
                }
            }
        }

        //function LISTENER for the button URL4:
        findViewById<Button>(R.id.url4).setOnClickListener(){

            var db: AppDB = AppDB.getInstance(this)

            CoroutineScope(Dispatchers.IO).launch {
                val listUrls = db.urlDAO().getRecentUrl()

                //val url1string = db.urlDAO().getRecentUrl()[3]
                //check if there is no url in the database
                if (listUrls.size < 4) {

                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "No url in local host, please try again", Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{ //if there url in the database at third place:
                    val url1string = listUrls[3]

                    //displaying the url in the edit text:
                    val editText = findViewById<EditText>(R.id.typeUrl)
                    editText.text = Editable.Factory.getInstance().newEditable(url1string)

                    //updating the url date in the database:
                        db.urlDAO().updateUrl(url1string, System.currentTimeMillis())

                }
            }
        }

        //function LISTENER for the button URL5:
        findViewById<Button>(R.id.url5).setOnClickListener(){

            var db: AppDB = AppDB.getInstance(this)

            CoroutineScope(Dispatchers.IO).launch {
                val listUrls = db.urlDAO().getRecentUrl()

                //val url1string = db.urlDAO().getRecentUrl()[4]
                //check if there is no url in the database
                if (listUrls.size < 5) {

                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "No url in local host, please try again", Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{ //if there url in the database at third place:
                    val url1string = listUrls[4]

                    //displaying the url in the edit text:
                    val editText = findViewById<EditText>(R.id.typeUrl)
                    editText.text = Editable.Factory.getInstance().newEditable(url1string)

                    //updating the url date in the database:
                        db.urlDAO().updateUrl(url1string, System.currentTimeMillis())

                }
            }
        }




        Thread {
            var db:AppDB=AppDB.getInstance(this)
            db.urlDAO().deleteAll()
//            var lastURL:String = db.urlDAO().getLastUrl()
////            var url1 = UrlEntity()
////            url1.url_name = "1"
////            url1.URL_Date = System.currentTimeMillis()
////            db.urlDAO().saveUrl(url1)
//
////            var url2 = UrlEntity()
////            url2.url_name = "2"
////            url2.URL_Date = System.currentTimeMillis()
////            db.urlDAO().saveUrl(url2)
////            db.urlDAO().getLastUrl()
       }.start()

        }


    private fun tryConnectToServer(url : String) {
        try {
            Toast.makeText(
                applicationContext,
                "Logging...",
                Toast.LENGTH_LONG
            ).show()
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()
            val api = retrofit.create(Api::class.java)
            api.getImg().enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "Can't Connect, try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val inputStream = response.body()?.byteStream()


                    if (inputStream != null) {
                        bitmap = BitmapFactory.decodeStream(inputStream)
                        nextActivity(url)
                    }else{
                        bitmap = BitmapFactory.decodeStream(inputStream)
                        Toast.makeText(
                            applicationContext,
                            "Can't get an image from the flight gear",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } catch (e : Exception) {
            Toast.makeText(
                applicationContext,
                "Can't Connect, try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun nextActivity(url : String) {
        var line: String?
        val sb = StringBuilder()
        // create the second screen
        val intent = Intent(this, Joystick::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }



}






