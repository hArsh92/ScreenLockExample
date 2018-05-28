package harsh.com.screenlocker.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import harsh.com.screenlocker.BuildConfig;
import harsh.com.screenlocker.R;
import harsh.com.screenlocker.listener.OnItemClickListener;
import harsh.com.screenlocker.listener.OnSwipeTouchListener;
import harsh.com.screenlocker.notifications.NotificationAdapter;
import harsh.com.screenlocker.notifications.NotificationItem;
import harsh.com.screenlocker.service.LockScreenService;

import static android.content.Context.WINDOW_SERVICE;

public class Utils {

    private static final long TIME_BACKGROUND_IMAGE_SHUFFLE = 30000L;

    /**
     * Get a notification for the running service.
     */
    public static Notification getNotification(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNM = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            if (mNM != null) {
                NotificationChannel channel = mNM.getNotificationChannel(context.getString(R.string.app_name));
                if (channel == null) {
                    channel = new NotificationChannel(
                            context.getString(R.string.app_name),
                            context.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_LOW
                    );
                    mNM.createNotificationChannel(channel);
                }
            }
        }

        String text = context.getString(R.string.app_name);
        return new NotificationCompat.Builder(context, text)
                .setOngoing(true)
                .setContentTitle(text)
                .setContentText(text)
                .setTicker(text)
                .setSmallIcon(android.R.drawable.ic_media_pause)
                .build();
    }

    public static List<NotificationItem> getNotificationItems(StatusBarNotification[] notifications) {
        List<NotificationItem> notificationItems = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (StatusBarNotification notification : notifications) {
                if (!notification.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
                    NotificationItem item = new NotificationItem();
                    item.id = notification.getId();
                    item.packageName = notification.getPackageName();
                    if (notification.getNotification() != null) {
                        item.icon = notification.getNotification().getLargeIcon();
                        item.title = notification.getNotification().extras.getString("android.title");
                        item.body = notification.getNotification().extras.getCharSequence("android.text");
                        item.extras = notification.getNotification().extras;
                        item.actions = notification.getNotification().actions;
                        item.category = notification.getNotification().category;
                        item.contentIntent = notification.getNotification().contentIntent;
                        item.deleteIntent = notification.getNotification().deleteIntent;
                        item.fullScreenIntent = notification.getNotification().fullScreenIntent;
                    }
                    notificationItems.add(item);
                }
            }
        }
        return notificationItems;
    }

    public static WindowManager.LayoutParams prepareLockScreenView(final FrameLayout layout, List<NotificationItem> notifications, final Context context) {

        int windowType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            windowType = WindowManager.LayoutParams.TYPE_TOAST |
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        } else {
            windowType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                windowType,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;

        LayoutInflater inflater = LayoutInflater.from(context);
        View lockScreenView = inflater.inflate(R.layout.lock_screen, layout);


        final ImageView backgroundView = lockScreenView.findViewById(R.id.background);
        final Random random = new Random();
        final Handler handler = new Handler();
        final int[] imageList = new int[]{
                R.drawable.ic_image_2,
                R.drawable.ic_image_1,
                R.drawable.ic_image_3
        };
        final Runnable runnable = new Runnable() {
            public void run() {
                int randomNum = random.nextInt(3);
                backgroundView.setImageDrawable(ContextCompat.getDrawable(context, imageList[randomNum]));
                handler.postDelayed(this, TIME_BACKGROUND_IMAGE_SHUFFLE);
            }
        };
        backgroundView.setImageDrawable(ContextCompat.getDrawable(context, imageList[random.nextInt(3)]));
        handler.postDelayed(runnable, TIME_BACKGROUND_IMAGE_SHUFFLE);

        final ImageView swipeView = lockScreenView.findViewById(R.id.swipe_view);
        swipeView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeTop() {
                WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
                if (windowManager != null) {
                    LockScreenService.removeLockScreenView(windowManager);
                    handler.removeCallbacks(runnable);
                }
            }
        });


        RecyclerView notificationList = lockScreenView.findViewById(R.id.notification_list);
        notificationList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        notificationList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        final NotificationAdapter adapter = new NotificationAdapter(notifications, new OnItemClickListener() {
            @Override
            public void onItemClicked(NotificationItem item) {
                WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
                if (windowManager != null) {
                    LockScreenService.removeLockScreenView(windowManager);
                    handler.removeCallbacks(runnable);
                }
            }
        });
        notificationList.setAdapter(adapter);

        return params;
    }
}
