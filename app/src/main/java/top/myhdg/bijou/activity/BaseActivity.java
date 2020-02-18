package top.myhdg.bijou.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.SkinAppCompatDelegateImpl;

import com.gyf.immersionbar.ImmersionBar;

import skin.support.SkinCompatManager;
import top.myhdg.bijou.R;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    private boolean darkMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCollector.addActivity(this);

        prefs = getSharedPreferences("BIJOU", MODE_PRIVATE);

        darkMode = prefs.getBoolean("dark_mode", false);

        changeStatusColor();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);

        boolean darkModeFollowSystem = prefs.getBoolean("dark_mode_follow_system", true);
        if (darkModeFollowSystem) {
            if (isSystemDarkMode()) {
                SkinCompatManager.getInstance().loadSkin("dark", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                darkMode = true;
            } else {
                SkinCompatManager.getInstance().restoreDefaultTheme();
                darkMode = false;
            }
            changeStatusColor();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", darkMode);
            editor.apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ActivityCollector.removeActivity(this);
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

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return SkinAppCompatDelegateImpl.get(this, this);
    }

}
