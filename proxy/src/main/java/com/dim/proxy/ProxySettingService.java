package com.dim.proxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ProxySettingService extends Service {

    private static final String TAG = "ProxySettingService";
    public static final String ACTION_PROXY = "com.dim.plugin.action.proxy.setting";
    public static final String KEY_PROXY_HOST_NAME = "key_proxy_host_name";
    public static final String KEY_PROXY_PORT = "key_proxy_port";
    public static final String KEY_PROXY_ENABLE = "key_proxy_enable";

    public ProxySettingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_PROXY:
                    ProxySetting proxySetting = getArgFromIntent(intent);
                    if (proxySetting != null) {
                        HookOkHttpClient.setProxySetting(proxySetting);
                        ProxyNotificationUtils.postNotification(this, proxySetting);
                    }
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private ProxySetting getArgFromIntent(Intent intent) {
        ProxySetting proxySetting = new ProxySetting();
        String proxyHostName = intent.getStringExtra(KEY_PROXY_HOST_NAME);
        Integer proxyPort = intent.getIntExtra(KEY_PROXY_PORT, -1);
        boolean enable = intent.getBooleanExtra(KEY_PROXY_ENABLE, true);
        proxySetting.enable = enable;
        if (proxyHostName != null && proxyPort != -1) {
            proxySetting.port = proxyPort;
            proxySetting.hostName = proxyHostName;
            return proxySetting;
        }
        if(!enable){
            return proxySetting;
        }
        return null;
    }

    public static class ProxySetting {
        public String hostName;
        public Integer port;
        public boolean enable;

        @Override
        public String toString() {
            return "ProxySetting{" +
                    "hostName='" + hostName + '\'' +
                    ", port=" + port +
                    ", enable=" + enable +
                    '}';
        }
    }
}
