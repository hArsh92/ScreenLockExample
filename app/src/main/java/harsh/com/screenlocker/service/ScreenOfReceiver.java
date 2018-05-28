package harsh.com.screenlocker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOfReceiver extends BroadcastReceiver {

    private OnScreenOfListener listener;

    public ScreenOfReceiver(OnScreenOfListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onScreenOf();
    }


    public interface OnScreenOfListener {
        void onScreenOf();
    }
}
