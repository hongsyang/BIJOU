package top.myhdg.bijou.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.zhouwei.library.CustomPopWindow;
import com.google.android.material.snackbar.Snackbar;
import com.gyf.immersionbar.ImmersionBar;
import com.kyleduo.switchbutton.SwitchButton;

import org.litepal.LitePal;

import skin.support.SkinCompatManager;
import top.myhdg.bijou.R;
import top.myhdg.bijou.bean.History;
import top.myhdg.bijou.util.KeyboardUtil;

public class SettingsActivity extends BaseActivity {

    SharedPreferences prefs;

    private TextView homePageButton;
    private TextView engineButton;
    private TextView toobarContentButton;
    private TextView clearDataButtin;
    private SwitchButton darkModeFollowSystemSwitch;
    private SwitchButton dropReloadSwitch;
    private SwitchButton keyCtrlSwitch;
    private SwitchButton javascriptSwitch;
    private SwitchButton recoverySwitch;
    private RelativeLayout aboutButton;
    private RelativeLayout supportButton;

    private int toobarContent;
    private static final int TITLE = 10;
    private static final int WEBSITE = 11;
    private static final int DOMAIN = 12;
    private String engine;
    private String homePage;
    private boolean clearCacheExit;
    private boolean clearFormDataExit;
    private boolean clearHistoryExit;

    private SwitchButton defaultHomeSwitch;
    private SwitchButton bingHomeSwitch;
    private SwitchButton googleHomeSwitch;
    private SwitchButton customHomeSwitch;
    private EditText inputHomeEdit;

    private SwitchButton baiduEngineSwitch;
    private SwitchButton bingEngineSwitch;
    private SwitchButton smEngineSwitch;
    private SwitchButton googleEngineSwitch;
    private SwitchButton customEngineSwitch;
    private EditText inputEngineEdit;

    private SwitchButton titleSwitch;
    private SwitchButton websiteSwitch;
    private SwitchButton domainSwitch;

    private CustomPopWindow clearDataPopWindow;
    private TextView clearCacheButton;
    private TextView clearFormButton;
    private TextView clearHistoryButton;
    private SwitchButton clearCacheSwitch;
    private SwitchButton clearFormSwitch;
    private SwitchButton clearHistorySwitch;
    private Intent resultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Button exitButton = findViewById(R.id.exit_settings_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });

        prefs = getSharedPreferences("BIJOU", MODE_PRIVATE);

        findViewAndInit();

        setClickListener();
    }

    private void findViewAndInit() {
        homePageButton = findViewById(R.id.home_page_button);
        engineButton = findViewById(R.id.engine_button);
        toobarContentButton = findViewById(R.id.toolbar_content_button);
        clearDataButtin = findViewById(R.id.clear_data_button);
        darkModeFollowSystemSwitch = findViewById(R.id.dark_mode_follow_system_switch);
        darkModeFollowSystemSwitch.setChecked(prefs.getBoolean("dark_mode_follow_system", true));
        dropReloadSwitch = findViewById(R.id.drop_reload_switch);
        dropReloadSwitch.setChecked(prefs.getBoolean("drop_reload", true));
        keyCtrlSwitch = findViewById(R.id.key_ctrl_switch);
        keyCtrlSwitch.setChecked(prefs.getBoolean("key_ctrl", false));
        javascriptSwitch = findViewById(R.id.javascript_switch);
        javascriptSwitch.setChecked(prefs.getBoolean("java_script", true));
        recoverySwitch = findViewById(R.id.recovery_switch);
        recoverySwitch.setChecked(prefs.getBoolean("recovery", false));
        aboutButton = findViewById(R.id.about_button);
        supportButton = findViewById(R.id.support_button);

        homePage = prefs.getString("home_page", "about:blank");
        engine = prefs.getString("engine", "https://www.baidu.com/s?wd=");
        toobarContent = prefs.getInt("toolbar_content", TITLE);
        clearCacheExit = prefs.getBoolean("clear_cache_exit", false);
        clearFormDataExit = prefs.getBoolean("clear_form_data_exit", false);
        clearHistoryExit = prefs.getBoolean("clear_history_exit", false);

        resultData = new Intent();
    }

    private void setClickListener() {
        homePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("InflateParams") View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.home_page_pop_window, null);
                initHomePagePopWindow(view);
                CustomPopWindow homePagePopWindow = new CustomPopWindow.PopupWindowBuilder(SettingsActivity.this)
                        .setView(view)
                        .enableBackgroundDark(true)
                        .create()
                        .showAsDropDown(homePageButton, 220, -150);
            }
        });

        engineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("InflateParams") View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.engine_pop_window, null);
                initEnginePopWindow(view);
                CustomPopWindow enginePopWindow = new CustomPopWindow.PopupWindowBuilder(SettingsActivity.this)
                        .setView(view)
                        .enableBackgroundDark(true)
                        .create()
                        .showAsDropDown(engineButton, 220, -150);
            }
        });

        toobarContentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("InflateParams") View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.toolbar_content_pop_window, null);
                initToolbarContentPopWindow(view);
                CustomPopWindow toolbarContentPopWindow = new CustomPopWindow.PopupWindowBuilder(SettingsActivity.this)
                        .setView(view)
                        .enableBackgroundDark(true)
                        .create()
                        .showAsDropDown(toobarContentButton, 220, -150);
            }
        });

        clearDataButtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("InflateParams") View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.clear_data_pop_window, null);
                initClearDataPopWindow(view);
                clearDataPopWindow = new CustomPopWindow.PopupWindowBuilder(SettingsActivity.this)
                        .setView(view)
                        .enableBackgroundDark(true)
                        .create()
                        .showAsDropDown(clearDataButtin, 220, -150);
            }
        });

        darkModeFollowSystemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("dark_mode_follow_system", isChecked);
                editor.apply();
                setResult(Activity.RESULT_FIRST_USER);
                changeDarkMode();
            }
        });

        dropReloadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("drop_reload", isChecked);
                editor.apply();
                setResult(Activity.RESULT_FIRST_USER);
            }
        });

        keyCtrlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("key_ctrl", isChecked);
                editor.apply();
                setResult(Activity.RESULT_FIRST_USER);
            }
        });

        javascriptSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("java_script", isChecked);
                editor.apply();
                setResult(Activity.RESULT_FIRST_USER);
            }
        });

        recoverySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("recovery", isChecked);
                editor.apply();
                setResult(Activity.RESULT_FIRST_USER);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SettingsActivity.this, AboutActivity.class), 1);
            }
        });

        supportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, RewardVideoActivity.class));
            }
        });
    }

    /**
     * 初始化主页弹窗
     */
    private void initHomePagePopWindow(View view) {
        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                switch (buttonView.getId()) {
                    case R.id.default_home_switch:
                        if (isChecked) {
                            homePage = "about:blank";
                            editor.putString("home_page", homePage);
                            bingHomeSwitch.setChecked(false);
                            googleHomeSwitch.setChecked(false);
                            customHomeSwitch.setChecked(false);
                            inputHomeEdit.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.bing_home_switch:
                        if (isChecked) {
                            homePage = "https://cn.bing.com/";
                            editor.putString("home_page", homePage);
                            defaultHomeSwitch.setChecked(false);
                            googleHomeSwitch.setChecked(false);
                            customHomeSwitch.setChecked(false);
                            inputHomeEdit.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.google_home_switch:
                        if (isChecked) {
                            homePage = "https://www.google.com/";
                            editor.putString("home_page", homePage);
                            defaultHomeSwitch.setChecked(false);
                            bingHomeSwitch.setChecked(false);
                            customHomeSwitch.setChecked(false);
                            inputHomeEdit.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.custom_home_switch:
                        if (isChecked) {
                            defaultHomeSwitch.setChecked(false);
                            bingHomeSwitch.setChecked(false);
                            googleHomeSwitch.setChecked(false);
                            inputHomeEdit.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                editor.apply();
                setResult(Activity.RESULT_FIRST_USER);
            }
        };

        defaultHomeSwitch = view.findViewById(R.id.default_home_switch);
        defaultHomeSwitch.setOnCheckedChangeListener(changeListener);
        bingHomeSwitch = view.findViewById(R.id.bing_home_switch);
        bingHomeSwitch.setOnCheckedChangeListener(changeListener);
        googleHomeSwitch = view.findViewById(R.id.google_home_switch);
        googleHomeSwitch.setOnCheckedChangeListener(changeListener);
        customHomeSwitch = view.findViewById(R.id.custom_home_switch);
        customHomeSwitch.setOnCheckedChangeListener(changeListener);
        inputHomeEdit = view.findViewById(R.id.input_home_edit);
        inputHomeEdit.setSelectAllOnFocus(true);
        inputHomeEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    homePage = inputHomeEdit.getText().toString();
                    if (!homePage.equals("")) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("home_page", homePage);
                        editor.apply();
                        KeyboardUtil.closeKeyboard(SettingsActivity.this, inputHomeEdit);
                        showSnackBar(findViewById(R.id.settings_layout), "自定义主页应用成功");
                    }
                }
                return false;
            }
        });

        switch (homePage) {
            case "about:blank":
                defaultHomeSwitch.setChecked(true);
                bingHomeSwitch.setChecked(false);
                googleHomeSwitch.setChecked(false);
                customHomeSwitch.setChecked(false);
                inputHomeEdit.setVisibility(View.GONE);
                break;
            case "https://cn.bing.com/":
                defaultHomeSwitch.setChecked(false);
                bingHomeSwitch.setChecked(true);
                googleHomeSwitch.setChecked(false);
                customHomeSwitch.setChecked(false);
                inputHomeEdit.setVisibility(View.GONE);
                break;
            case "https://www.google.com/":
                defaultHomeSwitch.setChecked(false);
                bingHomeSwitch.setChecked(false);
                googleHomeSwitch.setChecked(true);
                customHomeSwitch.setChecked(false);
                inputHomeEdit.setVisibility(View.GONE);
                break;
            default:
                defaultHomeSwitch.setChecked(false);
                bingHomeSwitch.setChecked(false);
                googleHomeSwitch.setChecked(false);
                customHomeSwitch.setChecked(true);
                inputHomeEdit.setVisibility(View.VISIBLE);
                inputHomeEdit.setText(homePage);
                break;
        }
    }

    /**
     * 初始化搜索引擎弹窗
     */
    private void initEnginePopWindow(View view) {
        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                switch (buttonView.getId()) {
                    case R.id.baidu_engine_switch:
                        if (isChecked) {
                            engine = "https://www.baidu.com/s?wd=";
                            editor.putString("engine", engine);
                            bingEngineSwitch.setChecked(false);
                            smEngineSwitch.setChecked(false);
                            googleEngineSwitch.setChecked(false);
                            customEngineSwitch.setChecked(false);
                            inputEngineEdit.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.bing_engine_switch:
                        if (isChecked) {
                            engine = "https://cn.bing.com/search?q=";
                            editor.putString("engine", engine);
                            baiduEngineSwitch.setChecked(false);
                            smEngineSwitch.setChecked(false);
                            googleEngineSwitch.setChecked(false);
                            customEngineSwitch.setChecked(false);
                            inputEngineEdit.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.sm_engine_switch:
                        if (isChecked) {
                            engine = "https://m.sm.cn/s?q=";
                            editor.putString("engine", engine);
                            baiduEngineSwitch.setChecked(false);
                            bingEngineSwitch.setChecked(false);
                            googleEngineSwitch.setChecked(false);
                            customEngineSwitch.setChecked(false);
                            inputEngineEdit.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.google_engine_switch:
                        if (isChecked) {
                            engine = "https://www.google.com/search?q=";
                            editor.putString("engine", engine);
                            baiduEngineSwitch.setChecked(false);
                            bingEngineSwitch.setChecked(false);
                            smEngineSwitch.setChecked(false);
                            customEngineSwitch.setChecked(false);
                            inputEngineEdit.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.custom_engine_switch:
                        if (isChecked) {
                            baiduEngineSwitch.setChecked(false);
                            bingEngineSwitch.setChecked(false);
                            smEngineSwitch.setChecked(false);
                            googleEngineSwitch.setChecked(false);
                            inputEngineEdit.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                editor.apply();
                setResult(Activity.RESULT_FIRST_USER);
            }
        };

        baiduEngineSwitch = view.findViewById(R.id.baidu_engine_switch);
        baiduEngineSwitch.setOnCheckedChangeListener(changeListener);
        bingEngineSwitch = view.findViewById(R.id.bing_engine_switch);
        bingEngineSwitch.setOnCheckedChangeListener(changeListener);
        smEngineSwitch = view.findViewById(R.id.sm_engine_switch);
        smEngineSwitch.setOnCheckedChangeListener(changeListener);
        googleEngineSwitch = view.findViewById(R.id.google_engine_switch);
        googleEngineSwitch.setOnCheckedChangeListener(changeListener);
        customEngineSwitch = view.findViewById(R.id.custom_engine_switch);
        customEngineSwitch.setOnCheckedChangeListener(changeListener);
        inputEngineEdit = view.findViewById(R.id.input_engine_edit);
        inputEngineEdit.setSelectAllOnFocus(true);
        inputEngineEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    engine = inputEngineEdit.getText().toString();
                    if (!engine.equals("")) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("engine", engine);
                        editor.apply();
                        KeyboardUtil.closeKeyboard(SettingsActivity.this, inputEngineEdit);
                        showSnackBar(findViewById(R.id.settings_layout), "自定义搜索引擎应用成功");
                    }
                }
                return false;
            }
        });

        switch (engine) {
            case "https://www.baidu.com/s?wd=":
                baiduEngineSwitch.setChecked(true);
                bingEngineSwitch.setChecked(false);
                smEngineSwitch.setChecked(false);
                googleEngineSwitch.setChecked(false);
                customEngineSwitch.setChecked(false);
                inputEngineEdit.setVisibility(View.GONE);
                break;
            case "https://cn.bing.com/search?q=":
                baiduEngineSwitch.setChecked(false);
                bingEngineSwitch.setChecked(true);
                smEngineSwitch.setChecked(false);
                googleEngineSwitch.setChecked(false);
                customEngineSwitch.setChecked(false);
                inputEngineEdit.setVisibility(View.GONE);
                break;
            case "https://m.sm.cn/s?q=":
                baiduEngineSwitch.setChecked(false);
                bingEngineSwitch.setChecked(false);
                smEngineSwitch.setChecked(true);
                googleEngineSwitch.setChecked(false);
                customEngineSwitch.setChecked(false);
                inputEngineEdit.setVisibility(View.GONE);
                break;
            case "https://www.google.com/search?q=":
                baiduEngineSwitch.setChecked(false);
                bingEngineSwitch.setChecked(false);
                smEngineSwitch.setChecked(false);
                googleEngineSwitch.setChecked(true);
                customEngineSwitch.setChecked(false);
                inputEngineEdit.setVisibility(View.GONE);
                break;
            default:
                baiduEngineSwitch.setChecked(false);
                bingEngineSwitch.setChecked(false);
                smEngineSwitch.setChecked(false);
                googleEngineSwitch.setChecked(false);
                customEngineSwitch.setChecked(true);
                inputEngineEdit.setVisibility(View.VISIBLE);
                inputEngineEdit.setText(engine);
                break;
        }
    }

    /**
     * 初始化顶栏内容弹窗
     */
    private void initToolbarContentPopWindow(View view) {
        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                switch (buttonView.getId()) {
                    case R.id.title_switch:
                        if (isChecked) {
                            toobarContent = TITLE;
                            editor.putInt("toolbar_content", toobarContent);
                            websiteSwitch.setChecked(false);
                            domainSwitch.setChecked(false);
                        }
                        break;
                    case R.id.website_switch:
                        if (isChecked) {
                            toobarContent = WEBSITE;
                            editor.putInt("toolbar_content", toobarContent);
                            titleSwitch.setChecked(false);
                            domainSwitch.setChecked(false);
                        }
                        break;
                    case R.id.domain_switch:
                        if (isChecked) {
                            toobarContent = DOMAIN;
                            editor.putInt("toolbar_content", toobarContent);
                            titleSwitch.setChecked(false);
                            websiteSwitch.setChecked(false);
                        }
                        break;
                }
                editor.apply();
                setResult(Activity.RESULT_FIRST_USER);
            }
        };

        titleSwitch = view.findViewById(R.id.title_switch);
        titleSwitch.setOnCheckedChangeListener(changeListener);
        websiteSwitch = view.findViewById(R.id.website_switch);
        websiteSwitch.setOnCheckedChangeListener(changeListener);
        domainSwitch = view.findViewById(R.id.domain_switch);
        domainSwitch.setOnCheckedChangeListener(changeListener);
        switch (toobarContent) {
            case TITLE:
                titleSwitch.setChecked(true);
                websiteSwitch.setChecked(false);
                domainSwitch.setChecked(false);
                break;
            case WEBSITE:
                titleSwitch.setChecked(false);
                websiteSwitch.setChecked(true);
                domainSwitch.setChecked(false);
                break;
            case DOMAIN:
                titleSwitch.setChecked(false);
                websiteSwitch.setChecked(false);
                domainSwitch.setChecked(true);
                break;
        }
    }

    /**
     * 初始化清理数据弹窗
     */
    private void initClearDataPopWindow(View view) {
        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = prefs.edit();
                switch (buttonView.getId()) {
                    case R.id.clear_cache_switch:
                        clearCacheExit = isChecked;
                        editor.putBoolean("clear_cache_exit", clearCacheExit);
                        break;
                    case R.id.clear_form_switch:
                        clearFormDataExit = isChecked;
                        editor.putBoolean("clear_form_data_exit", clearFormDataExit);
                        break;
                    case R.id.clear_history_switch:
                        clearHistoryExit = isChecked;
                        editor.putBoolean("clear_history_exit", clearHistoryExit);
                        break;
                }
                editor.apply();
                setResult(Activity.RESULT_FIRST_USER);
            }
        };

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.clear_cache_button:
                        resultData.putExtra("clear_cache", true);
                        setResult(Activity.RESULT_FIRST_USER, resultData);
                        showSnackBar(findViewById(R.id.settings_layout), "缓存清理完成");
                        break;
                    case R.id.clear_form_button:
                        resultData.putExtra("clear_form_data", true);
                        setResult(Activity.RESULT_FIRST_USER, resultData);
                        showSnackBar(findViewById(R.id.settings_layout), "表单清理完成");
                        break;
                    case R.id.clear_history_button:
                        clearDataPopWindow.dissmiss();
                        showActionSnackBar(findViewById(R.id.settings_layout), "确定清空历史记录吗？(此操作不可逆)", "清空", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LitePal.deleteAll(History.class);
                                showSnackBar(findViewById(R.id.settings_layout), "历史记录清空完成");
                            }
                        });
                        break;
                }
            }
        };

        clearCacheButton = view.findViewById(R.id.clear_cache_button);
        clearCacheButton.setOnClickListener(clickListener);
        clearCacheSwitch = view.findViewById(R.id.clear_cache_switch);
        clearCacheSwitch.setChecked(clearCacheExit);
        clearCacheSwitch.setOnCheckedChangeListener(changeListener);
        clearFormButton = view.findViewById(R.id.clear_form_button);
        clearFormButton.setOnClickListener(clickListener);
        clearFormSwitch = view.findViewById(R.id.clear_form_switch);
        clearFormSwitch.setChecked(clearFormDataExit);
        clearFormSwitch.setOnCheckedChangeListener(changeListener);
        clearHistoryButton = view.findViewById(R.id.clear_history_button);
        clearHistoryButton.setOnClickListener(clickListener);
        clearHistorySwitch = view.findViewById(R.id.clear_history_switch);
        clearHistorySwitch.setChecked(clearHistoryExit);
        clearHistorySwitch.setOnCheckedChangeListener(changeListener);
    }

    /**
     * 获取SnackBar背景颜色
     */
    private int getSnackBarBackgroundColor() {
        if (prefs.getBoolean("dark_mode", false)) {
            return ContextCompat.getColor(this, R.color.colorAccent_dark);
        } else {
            return ContextCompat.getColor(this, R.color.colorAccent);
        }
    }

    /**
     * 显示SnackBar
     */
    private void showSnackBar(View view, String content) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getSnackBarBackgroundColor())
                .setTextColor(Color.parseColor("#ffffff"))
                .show();
    }

    /**
     * 显示带Action的SnackBar
     */
    private void showActionSnackBar(View view, String content, String action, View.OnClickListener clickListener) {
        Snackbar.make(view, content, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getSnackBarBackgroundColor())
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setAction(action, clickListener)
                .setActionTextColor(Color.parseColor("#ffffff"))
                .show();
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
        if (isSystemDarkMode()) {
            ImmersionBar.with(this)
                    .statusBarColor(R.color.colorPrimaryDark_dark)
                    .navigationBarColor(R.color.colorPrimaryDark_dark)
                    .autoDarkModeEnable(true)
                    .init();
        } else {
            ImmersionBar.with(this)
                    .statusBarColor(R.color.colorPrimaryDark)
                    .navigationBarColor(R.color.colorPrimaryDark)
                    .autoDarkModeEnable(true)
                    .init();
        }
    }

    /**
     * 切换深色模式
     */
    private void changeDarkMode() {
        if (isSystemDarkMode()) {
            SkinCompatManager.getInstance().loadSkin("dark", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
        } else {
            SkinCompatManager.getInstance().restoreDefaultTheme();
        }
        changeStatusColor();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dark_mode", isSystemDarkMode());
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_FIRST_USER) {
                if (data != null) {
                    resultData.putExtra("url", data.getStringExtra("url"));
                    setResult(Activity.RESULT_FIRST_USER, resultData);
                    this.finish();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
