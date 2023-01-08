package com.addcn.monitor.network.okhttp.interceptor;

import okhttp3.Interceptor;

/**
 * @author Gentlman
 * @time 2022/12/6 09:22
 * @desc
 */
public abstract  class AbsInterceptor implements Interceptor {
   public String TAG = this.getClass().getSimpleName();
}
