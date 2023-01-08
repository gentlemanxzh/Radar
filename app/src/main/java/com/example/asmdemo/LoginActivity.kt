package com.example.asmdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.addcn.monitor.login.LoginHook
import com.addcn.monitor.login.NeedLogin

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val switch = findViewById<SwitchCompat>(R.id.switch_login)
        switch.isChecked = LoginHook.isLogin()
        findViewById<SwitchCompat>(R.id.switch_login).setOnCheckedChangeListener { _, check ->
            if (check) {
                LoginHook.login()
            } else {
                LoginHook.logout()
            }
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            startHomePage()
        }
    }


    @NeedLogin
    private fun startHomePage() {
        Toast.makeText(this, "登录成功，前往首页", Toast.LENGTH_SHORT).show()
    }
}