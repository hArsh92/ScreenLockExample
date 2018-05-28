package harsh.com.screenlocker.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import harsh.com.screenlocker.R;
import harsh.com.screenlocker.notifications.NotificationItem;
import harsh.com.screenlocker.utils.PrefUtils;
import harsh.com.screenlocker.utils.Utils;

public class LockScreenService extends Service {
    public static FrameLayout mLayout = null;
    private ScreenOfReceiver receiver;
    private BroadcastReceiver notificationRefreshedListener;
    private List<NotificationItem> notificationItems;

    @Override
    public void onCreate() {
        notificationItems = new ArrayList<>();
        final NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        receiver = new ScreenOfReceiver(new ScreenOfReceiver.OnScreenOfListener() {
            @Override
            public void onScreenOf() {
                WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
                removeLockScreenView(windowManager);
                mLayout = new FrameLayout(LockScreenService.this);

                notificationItems.clear();
                if (mNM != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        notificationItems.addAll(Utils.getNotificationItems(mNM.getActiveNotifications()));
                    }
                }
                if (windowManager != null) {
                    WindowManager.LayoutParams params = Utils.prepareLockScreenView(mLayout,
                            notificationItems, LockScreenService.this);

                    windowManager.addView(mLayout, params);
                }
            }
        });

        notificationRefreshedListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<NotificationItem> items = intent.getParcelableArrayListExtra("data");
                if (items != null) {
                    notificationItems.clear();
                    notificationItems.addAll(items);
                    if (mLayout != null) {
                        ((RecyclerView) mLayout.findViewById(R.id.notification_list)).getAdapter().notifyDataSetChanged();
                    }
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(notificationRefreshedListener,
                        new IntentFilter(DeviceNotificationListener.ACTION_NOTIFICATION_REFRESH));
        PrefUtils.saveToPref(this, true);
        if (mNM != null) {
            mNM.notify(R.string.app_name, Utils.getNotification(this));
        }
        startForeground(R.string.app_name, Utils.getNotification(this));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(notificationRefreshedListener);
        PrefUtils.saveToPref(this, false);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void removeLockScreenView(WindowManager manager) {
        if (mLayout != null) {
            if (manager != null && mLayout.getWindowToken() != null) {
                try {
                    manager.removeView(mLayout);
                } catch (IllegalArgumentException ignore) { }
            }
            mLayout = null;
        }
    }
}
