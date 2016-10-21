package com.dim.proxy;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;

public class WifiProxySettingActivity extends AppCompatActivity {

    private EditText hostNameET;
    private EditText proxyPortET;
    private Switch proxySwitch;
    private GetInfoAsyncTask getInfoAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_proxy_setting);
        hostNameET = (EditText) findViewById(R.id.hostNameET);
        proxyPortET = (EditText) findViewById(R.id.proxyPortET);
        proxySwitch = (Switch) findViewById(R.id.proxySwitch);
        boolean hook = HookOkHttpClient.isHook();
        proxySwitch.setChecked(hook);
        hostNameET.setEnabled(!hook);
        proxyPortET.setEnabled(!hook);
        Proxy hookProxy = HookOkHttpClient.getHookProxy();
        if (hookProxy != null && hookProxy.address() instanceof InetSocketAddress) {
            getInfoAsyncTask = new GetInfoAsyncTask((InetSocketAddress) hookProxy.address());
            getInfoAsyncTask.execute();
        }
        proxySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setProxyEnable(isChecked, hostNameET.getText().toString(), proxyPortET.getText().toString());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getInfoAsyncTask != null) {
            getInfoAsyncTask.cancel(true);
        }
    }

    private void setProxyEnable(boolean isChecked, String hostName, String port) {

        boolean enableProxy = HookOkHttpClient.isHook();
        hostNameET.setEnabled(!isChecked);
        proxyPortET.setEnabled(!isChecked);
        HookOkHttpClient.setHookEnable(isChecked);
        if (enableProxy == isChecked) {
            return;
        }
        if (isChecked) {
            if (HookOkHttpClient.setProxySetting(isChecked, hostName, port)) {

                ProxyNotificationUtils.postEnableProxyNotification(this, hostName, port);
            } else {
                proxySwitch.setChecked(false);
                Toast.makeText(this, R.string.setup_on_failure, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (HookOkHttpClient.setProxySetting(isChecked, hostName, port)) {
                ProxyNotificationUtils.postDisableProxyNotification(this);
            }
        }
    }

    class GetInfoAsyncTask extends AsyncTask<Void, Void, String[]> {

        private InetSocketAddress hookProxy;


        public GetInfoAsyncTask(InetSocketAddress hookProxy) {
            this.hookProxy = hookProxy;
        }

        @Override
        protected String[] doInBackground(Void... params) {
            String[] result = new String[2];
            result[0] = hookProxy.getHostName();
            result[1] = hookProxy.getPort() + "";
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            proxySwitch.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if (!WifiProxySettingActivity.this.isFinishing()) {
                hostNameET.setText(result[0]);
                proxyPortET.setText(result[1]);
                proxySwitch.setEnabled(true);
            }

        }
    }

}
