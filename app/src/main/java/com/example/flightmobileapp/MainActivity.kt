package com.example.flightmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.room.Room
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.joystick.*

var isLocal= false

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        var db:AppDB=AppDB.getInstance(this)
//        var lastURL:String = db.urlDAO().getLastUrl()

        Thread {
            var db:AppDB=AppDB.getInstance(this)
            db.urlDAO().deleteAll()
            var lastURL:String = db.urlDAO().getLastUrl()
            var url1 = UrlEntity()
            url1.url_name = "1"
            url1.URL_Date = System.currentTimeMillis()
            db.urlDAO().saveUrl(url1)

            var url2 = UrlEntity()
            url2.url_name = "2"
            url2.URL_Date = System.currentTimeMillis()
            db.urlDAO().saveUrl(url2)
            db.urlDAO().getLastUrl()
        }.start()

        }

    // set on-click listener:url1
    fun url1Listener(view: View) {
        Thread {
            var db:AppDB=AppDB.getInstance(this)

            //check if there is no url in the database
            val url1string = db.urlDAO().getLastUrl()
//            if (url1string == null){
//
//                Toast.makeText(this@MainActivity,
//                    "No url in local host please try again", Toast.LENGTH_SHORT).show()
//            }
            //displaying the url in the edit text:
            val editText = findViewById<EditText>(R.id.typeUrl)
            editText.text = Editable.Factory.getInstance().newEditable(url1string)

            //updating the url date in the database:
            db.urlDAO().updateUrl(url1string, System.currentTimeMillis())
            isLocal = true
        }.start()

    }


    //function to connect to server with URL
    fun connectClick(view:View){
        val intent = Intent(this, Joystick::class.java)
        startActivity(intent)

    }



}






