package com.example.restaurantapp.Base;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.Entity.InspectionBean;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *  Activity  Base class
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        unbinder = ButterKnife.bind(this);
        init();
        Log.i("BaseActivity", "a");
        hideStatusBar();
    }

    /**
     *load layout
     */
    protected abstract int setLayoutId();

    /**
     * initialize
     */
    protected abstract void init();

    /**
     * set System Bar Color
     */
    protected void setSystemBarColor(int color) {
        ImmersionBar.with(this).statusBarColor(color);
}

    /**
     * hide Status Bar
     */
    protected void hideStatusBar() {
        ImmersionBar.with(this).init();
    }


    protected void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Activity:Exit Animation
     */
    protected void setExitAnimation(int animId) {
        overridePendingTransition(0, animId);
    }

    /**
     * Full Screen
     */
    protected void setFullScreen() {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_STATUS_BAR).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
