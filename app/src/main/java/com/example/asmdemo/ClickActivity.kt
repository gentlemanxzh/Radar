package com.example.asmdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.addcn.monitor.click.UncheckViewOnClick

class ClickActivity : AppCompatActivity() {

    private var clickIndex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_click)

        findViewById<Button>(R.id.btn_un_check_click).setOnClickListener(object : View.OnClickListener {
            @UncheckViewOnClick
            override fun onClick(p0: View?) {
                onClickView()
            }
        })

        findViewById<Button>(R.id.btn_click).setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                onClickView()
            }
        })

        findViewById<Button>(R.id.btn_lambda).setOnClickListener {
            onClickView()
        }


    }

    private fun onClickView() {
        findViewById<TextView>(R.id.tv_index).text = (clickIndex++).toString()
    }
}