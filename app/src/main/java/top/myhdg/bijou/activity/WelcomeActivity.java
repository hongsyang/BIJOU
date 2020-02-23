package top.myhdg.bijou.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import com.gyf.immersionbar.ImmersionBar;

import skin.support.SkinCompatManager;
import top.myhdg.bijou.R;

public class WelcomeActivity extends BaseActivity {

    private boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        SharedPreferences prefs = getSharedPreferences("BIJOU", MODE_PRIVATE);
        boolean darkModeFollowSystem = prefs.getBoolean("dark_mode_follow_system", true);
        if (darkModeFollowSystem) {
            if (isSystemDarkMode()) {
                SkinCompatManager.getInstance().loadSkin("dark", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                darkMode = true;
            } else {
                SkinCompatManager.getInstance().restoreDefaultTheme();
                darkMode = false;
            }
        } else {
            darkMode = prefs.getBoolean("dark_mode", false);
            if (darkMode) {
                SkinCompatManager.getInstance().loadSkin("dark", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
            } else {
                SkinCompatManager.getInstance().restoreDefaultTheme();
            }
        }
        changeStatusColor();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dark_mode", darkMode);
        editor.apply();

        int time = 1000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                WelcomeActivity.this.finish();
            }
        }, time);
    }

    /**
     * 获取系统当前是否为深色模式
     */
    private boolean isSystemDarkMode() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * 根据深色模式调整状态栏颜色
     */
    private void changeStatusColor() {
        if (darkMode) {
            ImmersionBar.with(this)
                    .keyboardEnable(true)
                    .statusBarColor(R.color.colorPrimaryDark_dark)
                    .navigationBarColor(R.color.colorPrimaryDark_dark)
                    .autoDarkModeEnable(true)
                    .init();
        } else {
            ImmersionBar.with(this)
                    .keyboardEnable(true)
                    .statusBarColor(R.color.colorPrimaryDark)
                    .navigationBarColor(R.color.colorPrimaryDark)
                    .autoDarkModeEnable(true)
                    .init();
        }
    }

}
