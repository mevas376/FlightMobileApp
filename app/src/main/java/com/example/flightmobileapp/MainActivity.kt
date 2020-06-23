package com.example.flightmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.room.Room
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.joystick.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        var db:AppDB=AppDB.getInstance(this)
//        var lastURL:String = db.urlDAO().getLastUrl()

        Thread {
            var db:AppDB=AppDB.getInstance(this)
            var lastURL:String = db.urlDAO().getLastUrl()
            var url = UrlEntity()
            url.url_name = "mev"
            url.URL_Date = System.currentTimeMillis()
            db.urlDAO().saveUrl(url)
            db.urlDAO().getLastUrl()

//            Log.i("blabla",db.urlDAO().getLastUrl() )
//            Log.d("MYTAG", "current position is: " )
        }.start()

        }

    //function listener to the  5 BUTTONS of URLS
    fun urlClicked(){

    }

    //function to connect to server with URL
    fun connectClick(view:View){
        val intent = Intent(this, Joystick::class.java)
        startActivity(intent)

    }



}






