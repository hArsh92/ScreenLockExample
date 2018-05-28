package harsh.com.screenlocker.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import harsh.com.screenlocker.BuildConfig;
import harsh.com.screenlocker.notifications.NotificationItem;
import harsh.com.screenlocker.utils.Utils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceNotificationListener extends NotificationListenerService {

    public static final String ACTION_NOTIFICATION_REFRESH = BuildConfig.APPLICATION_ID + ".notification_refresh";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        refreshList();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        refreshList();
    }

    private void refreshList() {
        List<NotificationItem> notifications = Utils.getNotificationItems(getActiveNotifications());
        Intent intent = new Intent(ACTION_NOTIFICATION_REFRESH);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", new ArrayList<>(notifications));
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }
}