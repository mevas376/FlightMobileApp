package com.example.flightmobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

var isLocal= false
//private val uiScope = CoroutineScope(Dispatchers.Main)


class MainActivity : AppCompatActivity() {
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
                    } else { //if typed new url
                        var uUrl = typeUrl.text.toString()
                        var url1 = UrlEntity()
                        url1.url_name = uUrl
                        url1.URL_Date = System.currentTimeMillis()

                        CoroutineScope(Dispatchers.IO).launch {
                            db.urlDAO().saveUrl(url1)
                        }
                    }
                //}
            }
        }

        //function LISTENER for the button URL1:
        findViewById<Button>(R.id.url1).setOnClickListener(){

            var db: AppDB = AppDB.getInstance(this)

            CoroutineScope(Dispatchers.IO).launch {
                val url1string = db.urlDAO().getLastUrl()
                //check if there is no url in the database
                if (url1string == null) {
                    var a= 12
//                    Toast.makeText(
//                            this@MainActivity,
//                    "No url in local host, please try again", Toast.LENGTH_SHORT
//                    ).show()
                }else{ //if there url in the database at thid place:
                    //displaying the url in the edit text:
                    val editText = findViewById<EditText>(R.id.typeUrl)
                    editText.text = Editable.Factory.getInstance().newEditable(url1string)

                    //updating the url date in the database:
                    CoroutineScope(Dispatchers.IO).launch {
                        db.urlDAO().updateUrl(url1string, System.currentTimeMillis())
                    }
                }
            }
        }

        //function LISTENER for the button URL2:
        findViewById<Button>(R.id.url2).setOnClickListener(){

            var db: AppDB = AppDB.getInstance(this)

            CoroutineScope(Dispatchers.IO).launch {

                val url1string = db.urlDAO().getRecentUrl()[1]
                //check if there is no url in the database
                if (url1string == null) {
                    Toast.makeText(
                        this@MainActivity,
                        "No url in local host, please try again", Toast.LENGTH_SHORT
                    ).show()
                }else{ //if there url in the database at thid place:
                    //displaying the url in the edit text:
                    val editText = findViewById<EditText>(R.id.typeUrl)
                    editText.text = Editable.Factory.getInstance().newEditable(url1string)

                    //updating the url date in the database:
                    CoroutineScope(Dispatchers.IO).launch {
                        db.urlDAO().updateUrl(url1string, System.currentTimeMillis())
                    }
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
}






