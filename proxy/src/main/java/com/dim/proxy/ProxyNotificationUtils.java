package com.dim.proxy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by dim on 16/10/20.
 */

public class ProxyNotificationUtils {

    public static final int NOTIFY_CODE = 33;

    public static void postNotification(Context context, ProxySettingService.ProxySetting proxySetting) {
        if (proxySetting.enable) {
            postEnableProxyNotification(context,proxySetting.hostName,proxySetting.port+"");
        } else {
            postDisableProxyNotification(context);
        }
    }

    public static void postEnableProxyNotification(Context context, String hostName, String port) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent startSetting = new Intent(context.getApplicationContext(), WifiProxySettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), NOTIFY_CODE, startSetting, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context).setContentTitle(context.getString(R.string.set_proxy_title)).setContentText(context.getString(R.string.proxy_host_name) + hostName + " : " + port)
                .setShowWhen(true).setTicker(context.getString(R.string.set_proxy_success)).setContentIntent(pendingIntent).setSmallIcon(R.drawable.proxy_notify_enable_icon).setAutoCancel(true).build();
        notificationManager.notify(NOTIFY_CODE, notification);
        Toast.makeText(context, context.getString(R.string.set_proxy_success), Toast.LENGTH_SHORT).show();

    }

    public static void postDisableProxyNotification(Context context) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent startSetting = new Intent(context.getApplicationContext(), WifiProxySettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), NOTIFY_CODE, startSetting, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.proxy_notify_disable_icon).setContentTitle(context.getString(R.string.set_proxy_title)).setContentText(context.getString(R.string.close_proxy)).setAutoCancel(true)
                .setShowWhen(true).setTicker(context.getString(R.string.close_proxy)).setContentIntent(pendingIntent).build();
        notificationManager.notify(NOTIFY_CODE, notification);
        Toast.makeText(context, context.getString(R.string.close_proxy), Toast.LENGTH_SHORT).show();

    }

}
