package com.dim.proxy;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.net.InetSocketAddress;
import java.net.Proxy;


/**
 * Created by dim on 16/10/19.
 */
@Aspect
public class HookOkHttpClient {

    private static final String TAG = "HookOkHttpClient";
    private static HookOkHttpClient sHookOkHttpClient = new HookOkHttpClient();
    private static boolean sHookEnable = false;
    private static Proxy sProxy;

    public static HookOkHttpClient aspectOf() {

        return sHookOkHttpClient;
    }

    public static boolean isHook() {
        return sHookEnable;
    }

    public static void setHookEnable(boolean enable) {
        sHookEnable = enable;
    }

    public static void setHookProxy(Proxy proxy) {
        sProxy = proxy;
    }

    public static Proxy getHookProxy() {
        return sProxy;
    }

    @Pointcut("within(com.squareup.okhttp.OkHttpClient+)")
    void okhttp2() {
    }

    @Pointcut("within(okhttp3.OkHttpClient+)")
    void okhttp3() {
    }

    @Pointcut("execution(public java.net.Proxy getProxy())&& okhttp2()")
    void getProxy() {
    }

    @Pointcut("execution(public java.net.Proxy proxy()) && okhttp3()")
    void proxy() {
    }

    @Around("getProxy()||proxy()")
    public Object getProxyImp(ProceedingJoinPoint proceedingJoinPoint) {
        Log.d(TAG, "hook OkHttp : " + proceedingJoinPoint.getSignature().getName());
        if (!sHookEnable && sProxy != null) {
            try {
                return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        Log.d(TAG, "return  : hook " + sProxy);

        return sProxy;
    }

    public static boolean setProxySetting(ProxySettingService.ProxySetting proxySetting) {
        setHookEnable(proxySetting.enable);
        if (proxySetting.hostName != null && proxySetting.hostName.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            setHookProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxySetting.hostName, proxySetting.port)));
            return true;
        }
        return true;
    }

    public static boolean setProxySetting(boolean enable, String hostName, String port) {
        Log.d(TAG, "setProxy : enable " + enable + " hostName " + hostName + " port " + port);
        ProxySettingService.ProxySetting proxySetting = new ProxySettingService.ProxySetting();
        proxySetting.enable = enable;
        if (enable) {
            if (hostName.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}") && port.matches("\\d+")) {
                proxySetting.hostName = hostName;
                proxySetting.port = Integer.parseInt(port);
                return HookOkHttpClient.setProxySetting(proxySetting);
            }
            return false;
        }
        return true;
    }
}
