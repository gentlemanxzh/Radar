package com.addcn.monitor.login

import android.util.Log
import android.widget.Toast

/**
 *
 * @author Gentleman
 * @time 2022/12/9 14:51
 * @desc
 */
object LoginHook {

    private var isLogin = false

    @JvmStatic
    fun isLogin(): Boolean {
        return isLogin;
    }

    fun login(){
        isLogin = true;
    }

    fun logout(){
        isLogin = false;
    }

    @JvmStatic
    fun starLoginPage() {
       println("跳转到登录页面")
    }
}