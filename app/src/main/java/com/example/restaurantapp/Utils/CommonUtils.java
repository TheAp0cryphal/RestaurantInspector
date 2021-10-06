package com.example.restaurantapp.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.utils.CommonUtils.ClearInfo;
import static com.example.utils.CommonUtils.DialogShow;
import static com.example.utils.CommonUtils.IntentToPage;

public class CommonUtils {

    /**
     * Clear all SharedPreferences
     */
    public static void ClearAllInfo(Context context) {
        ClearInfo(context, "GetName");
        ClearInfo(context, "GetId");
        ClearInfo(context, "GetMname");
    }


    /**
     * Hide status bar, navigation bar, title bar
     */
    public static void HideBarAll(Activity activity) {
        ImmersionBar.with(activity).hideBar(BarHide.FLAG_HIDE_BAR);
    }

    /**
     * Save Long type
     */
    public static void SaveLong(Context context, String Mode,String key,long text){
        SharedPreferences preferences = context.getSharedPreferences(Mode, Context.MODE_PRIVATE);
        preferences.edit().putLong(key, text).apply();
    }

    /**
     * Get Long type
     */
    public static Long SharedLong(Context context,String Mode, String key){
        SharedPreferences preferences = context.getSharedPreferences(Mode, Context.MODE_PRIVATE);
        long text = preferences.getLong(key,0);
        return text;
    }


}
