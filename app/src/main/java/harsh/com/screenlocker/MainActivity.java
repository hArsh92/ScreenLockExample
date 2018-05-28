package harsh.com.screenlocker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import harsh.com.screenlocker.service.LockScreenService;
import harsh.com.screenlocker.utils.PermissionUtils;
import harsh.com.screenlocker.utils.PrefUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SwitchCompat toggleLockEnable = findViewById(R.id.switch_lock);
        toggleLockEnable.setChecked(PrefUtils.isLockScreenEnabled(this));

        toggleLockEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (PermissionUtils.canDrawOver(MainActivity.this)) {
                    if (PermissionUtils.canControlNotification(MainActivity.this)) {
                        if (isChecked) {
                            doBindService();
                        } else {
                            doUnbindService();
                        }
                    } else {
                        PermissionUtils.requestNotificationAccessibilityPermission(MainActivity.this);
                        toggleLockEnable.setChecked(false);
                    }
                } else {
                    PermissionUtils.requestOverlayPermission(MainActivity.this);
                    toggleLockEnable.setChecked(false);
                }
            }
        });
    }

    void doBindService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, LockScreenService.class));
        } else {
            startService(new Intent(this, LockScreenService.class));
        }
    }

    void doUnbindService() {
        stopService(new Intent(this, LockScreenService.class));
    }
}
