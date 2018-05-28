package harsh.com.screenlocker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

    private static final String PREF_FILE = "settings_pref";

    public static void saveToPref(Context context, boolean isEnabled) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("is_enabled", isEnabled);
        editor.apply();
    }

    public static boolean isLockScreenEnabled(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getBoolean("is_enabled", false);
    }
}
