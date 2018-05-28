package harsh.com.screenlocker.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class NotificationItem implements Serializable, Parcelable {

    public int id;
    public String packageName;
    public CharSequence title;
    public CharSequence body;
    public Icon icon;
    public Bundle extras;
    public PendingIntent contentIntent;
    public PendingIntent deleteIntent;
    public PendingIntent fullScreenIntent;
    public Notification.Action[] actions;
    public String category;

    public NotificationItem() { }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (id == ((NotificationItem) obj).id) {
            return true;
        }
        return super.equals(obj);
    }

    public NotificationItem(Parcel in) {
        id = in.readInt();
        packageName = in.readString();
        title = (CharSequence) in.readValue(CharSequence.class.getClassLoader());
        body = (CharSequence) in.readValue(CharSequence.class.getClassLoader());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            icon = (Icon) in.readValue(Icon.class.getClassLoader());
        }
        extras = in.readBundle();
        contentIntent = (PendingIntent) in.readValue(PendingIntent.class.getClassLoader());
        deleteIntent = (PendingIntent) in.readValue(PendingIntent.class.getClassLoader());
        fullScreenIntent = (PendingIntent) in.readValue(PendingIntent.class.getClassLoader());
        category = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(packageName);
        dest.writeValue(title);
        dest.writeValue(body);
        dest.writeValue(icon);
        dest.writeBundle(extras);
        dest.writeValue(contentIntent);
        dest.writeValue(deleteIntent);
        dest.writeValue(fullScreenIntent);
        dest.writeString(category);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NotificationItem> CREATOR = new Parcelable.Creator<NotificationItem>() {
        @Override
        public NotificationItem createFromParcel(Parcel in) {
            return new NotificationItem(in);
        }

        @Override
        public NotificationItem[] newArray(int size) {
            return new NotificationItem[size];
        }
    };
}
