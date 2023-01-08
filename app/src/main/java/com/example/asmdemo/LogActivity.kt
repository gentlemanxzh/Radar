package com.example.asmdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        findViewById<Button>(R.id.btn_print).setOnClickListener {
            Log.d("LogActivity","Log d print")
            Log.v("LogActivity","Log v print")
            Log.i("LogActivity","Log i print")
            Log.e("LogActivity","Log e print")
        }
    }
}