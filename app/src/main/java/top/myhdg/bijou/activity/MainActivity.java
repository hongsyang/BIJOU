package top.myhdg.bijou.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arialyy.aria.core.Aria;
import com.example.zhouwei.library.CustomPopWindow;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.gyf.immersionbar.ImmersionBar;
import com.kyleduo.switchbutton.SwitchButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.github.xudaojie.qrcodelib.CaptureActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import skin.support.SkinCompatManager;
import skin.support.design.widget.SkinMaterialCoordinatorLayout;
import skin.support.design.widget.SkinMaterialNavigationView;
import top.myhdg.bijou.R;
import top.myhdg.bijou.bean.Bookmark;
import top.myhdg.bijou.bean.History;
import top.myhdg.bijou.bean.HomeQuick;
import top.myhdg.bijou.bean.HomeQuickAdapter;
import top.myhdg.bijou.bean.HomeQuickItem;
import top.myhdg.bijou.bean.Suggest;
import top.myhdg.bijou.bean.SuggestAdapter;
import top.myhdg.bijou.bean.WebPage;
import top.myhdg.bijou.bean.WebPageAdapter;
import top.myhdg.bijou.dialog.QRContentDialog;
import top.myhdg.bijou.dialog.UAChangeDialog;
import top.myhdg.bijou.util.HttpUtil;
import top.myhdg.bijou.util.KeyboardUtil;
import top.myhdg.bijou.view.EnhancedToolbar;
import top.myhdg.bijou.view.EnhancedWebView;

public class MainActivity extends BaseActivity {

    private SharedPreferences prefs;

    private boolean darkMode;//深色模式
    private boolean noTrace;//无痕浏览
    private boolean uaPC;//桌面版UA
    private boolean fullScreen;//全屏模式

    private long lastPressedTime = 0;//上次按下返回键时间

    private RelativeLayout toolbarLayout;
    private EnhancedToolbar toolbar;//顶部工具栏
    private EditText toolbarEdit;//顶部地址栏
    private Button menuButton;//工具栏菜单按钮
    private Button goButton;//工具栏转到按钮
    private Button multipleButton;//工具栏多页面按钮
    private ProgressBar progressBar;//进度条

    private LinearLayout findLayout;
    private Button cancelFindButton;//取消查找按钮
    private EditText findEdit;//查找内容输入框
    private Button findBackButton;//查找上一个按钮
    private Button findNextButton;//查找下一个按钮

    private RecyclerView suggestRecyclerView;//搜索建议列表
    private ArrayList<Suggest> suggestList;//搜索建议列表
    private SuggestAdapter suggestAdapter;

    private static final String SUGGEST_API = "https://suggestion.baidu.com/su?p=3&cb=window.bdsug.sug&wd=";

    private NestedScrollView layoutHome;//主页
    private TextView startText;//主页搜索框
    private RecyclerView homeQuickRecylerView;//主页快捷方式
    public static List<HomeQuick> homeQuicks;
    private List<HomeQuickItem> homeQuickList;
    private HomeQuickAdapter homeQuickAdapter;
    private String homeQuickUrl;
    private long homeQuickLastTime = 0;//上次点击主页快捷方式时间
    private long homeQuickCurTime = 0;//当前点击主页快捷方式时间
    private CustomPopWindow editHomeQuickPopWindow;
    private HomeQuick editHomeQuick;
    @SuppressLint("HandlerLeak")
    private Handler handler;

    private DrawerLayout drawerLayout;

    private SkinMaterialNavigationView navMenu;
    private SwitchButton darkModeSwitch;//深色模式开关
    private SwitchButton noTraceSwitch;//无痕浏览开关
    private SwitchButton uaPcSwitch;//桌面版UA开关
    private SwitchButton fullScreenSwitch;//全屏模式开关
    private RelativeLayout bookmarkButton;//书签按钮
    private RelativeLayout historyButton;//历史记录按钮
    private RelativeLayout downloadButton;//下载按钮
    private RelativeLayout shareButton;//分享按钮
    private RelativeLayout exportButton;//导出按钮
    private RelativeLayout uaButton;//浏览器标识按钮
    private RelativeLayout sourceButton;//网页源码按钮
    private RelativeLayout settingsButton;//设置按钮
    private TextView oneText;//菜单底部一言

    private static final String ONE_TEXT_API = "https://v1.hitokoto.cn/";//一言API

    public static String htmlSource;//网页源码
    private String darkModeCSS;//深色模式CSS

    private FrameLayout fullVideoLayout;//全屏播放视频布局
    private WebChromeClient.CustomViewCallback videoCallback;

    private Button qrButton;//扫描二维码按钮
    private Button addButton;//添加新页面按钮
    private Button emptyButton;//清空页面按钮
    private RecyclerView webPageRecycler;//页面列表
    private ArrayList<EnhancedWebView> webViews;//WebView列表
    private int topPage;//最上层WebView
    private ArrayList<WebPage> webPageList;//多窗口列表
    private WebPageAdapter webPageAdapter;

    private static final int QR_REQUEST_CODE = 1;//扫描二维码请求码

    private SkinMaterialCoordinatorLayout coordinatorLayout;

    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton addBookmarkFab;//添加书签悬浮按钮
    private FloatingActionButton reloadFab;//重新加载悬浮按钮
    private FloatingActionButton goBackFab;//返回悬浮按钮
    private FloatingActionButton goForwardFab;//前进悬浮按钮
    private FloatingActionButton homeFab;//主页悬浮按钮
    private FloatingActionButton findFab;//查找悬浮按钮

    private CustomPopWindow addBookmarkPopWindow;//添加书签弹窗

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout webViewLayout;

    private boolean enabledJavaScript;//是否开启JavaScript
    private boolean enabledDropReload;//是否开启下拉刷新
    private boolean enabledKeyCtrl;//是否开启音量键翻页
    private boolean enabledRecovery;//是否开启启动时恢复页面
    private boolean clearCacheExit;//退出时清理缓存
    private boolean clearFormDataExit;//退出时清理表单数据
    private boolean clearHistoryExit;//退出时清理历史记录

    private int recoveryNum;//恢复页数量

    private int screenHeight;//屏幕长度

    private String homePage;//主页
    private String engine;//搜索引擎
    private int toolbarContent;//地址栏显示内容
    private static final int TITLE = 10;//网站标题
    private static final int WEBSITE = 11;//网址
    private static final int DOMAIN = 12;//域名

    private boolean customUA = false;//是否自定义UA
    private String customUserAgent;
    private static final String PC_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36 Edg/80.0.361.57";
    private static final String DEFAULT_UA = "";

    public CustomPopWindow longSrcAnchorPopWindow;//长按超链接弹窗
    public CustomPopWindow longImgPopWindow;//长按图片弹窗
    private String longClickUrl = "";//网页长按目标Url

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private static final int FILE_CHOOSER_RESULT_CODE = 10000;//文件上传请求码

    private static final int HISTORY_RESQUEST_CODE = 2;//历史记录请求码
    private static final int BOOKMARK_RESQUEST_CODE = 3;//书签请求码
    private static final int SETTINGS_RESQUEST_CODE = 4;//设置请求码

    //权限请求码
    private static final int ACCESS_COARSE_LOCATION = 0;
    private static final int ACCESS_FINE_LOCATION = 1;
    private static final int WRITE_EXTERNAL_STORAGE_EXPORT_IMG = 2;
    private static final int WRITE_EXTERNAL_STORAGE_SAVE_IMG = 3;
    private static final int WRITE_EXTERNAL_STORAGE_DOWNLOAD = 4;

    private String downloadFileName = "";
    private String downloadUrl = "";
    private CustomPopWindow addDownloadPopWindow;

    private static final String REMOVE_AD_JS = "javascript: $('.u-ad-wrap')" +
            ".remove();$('.home_packet').remove();$('.pbpb-item').remove();$('.m_pbpb_m0')" +
            ".remove();$('.experience').remove();$('#bo1').remove();";

    //深色模式例外网站
    private static final String[] DARKMODE_EXCEPT = {"music.163.com/m/song", "d1.music.126.net/dmusic"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("BIJOU", MODE_PRIVATE);
        darkMode = prefs.getBoolean("dark_mode", false);
        noTrace = prefs.getBoolean("no_trace", false);
        uaPC = prefs.getBoolean("ua_pc", false);
        fullScreen = prefs.getBoolean("full_screen", false);
        changeFullScreen();

        homePage = prefs.getString("home_page", "about:blank");
        engine = prefs.getString("engine", "https://www.baidu.com/s?wd=");
        toolbarContent = prefs.getInt("toolbar_content", TITLE);

        enabledJavaScript = prefs.getBoolean("java_script", true);
        enabledDropReload = prefs.getBoolean("drop_reload", true);
        enabledKeyCtrl = prefs.getBoolean("key_ctrl", false);
        enabledRecovery = prefs.getBoolean("recovery", false);
        recoveryNum = prefs.getInt("recovery_num", 0);
        customUA = prefs.getBoolean("custom_ua", false);
        customUserAgent = prefs.getString("custom_user_agent", "");
        clearCacheExit = prefs.getBoolean("clear_cache_exit", false);
        clearFormDataExit = prefs.getBoolean("clear_form_data_exit", false);
        clearHistoryExit = prefs.getBoolean("clear_history_exit", false);

        screenHeight = this.getResources().getDisplayMetrics().heightPixels;

        getDarkModeCSS();

        LitePal.getDatabase();

        WebView.enableSlowWholeDocumentDraw();

        initMultiple();

        initMenu();

        initToolbar();

        initFindBar();

        initFloatingButton();

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setDistanceToTriggerSync(600);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webViews.get(topPage).reload();
            }
        });
        webViewLayout = findViewById(R.id.web_view_layout);

        webViews = new ArrayList<>();
        webPageList = new ArrayList<>();
        webPageAdapter = new WebPageAdapter(webPageList);
        webPageRecycler.setAdapter(webPageAdapter);

        suggestList = new ArrayList<>();
        suggestAdapter = new SuggestAdapter(suggestList);
        suggestRecyclerView.setAdapter(suggestAdapter);

        if (enabledRecovery && recoveryNum > 0 && !prefs.getString("recovery_page_0", "about:blank").equals(homePage)) {
            openRecoveryPage();
        } else {
            addNewPageForeground(homePage);
        }

        String externalUrl = getIntent().getStringExtra("external_url");
        if (externalUrl != null) {
            openExternalUrl(getIntent().getBooleanExtra("is_file_url", false), externalUrl);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String externalUrl = intent.getStringExtra("external_url");
        if (externalUrl != null) {
            openExternalUrl(intent.getBooleanExtra("is_file_url", false), externalUrl);
        }
    }

    /**
     * 恢复页面
     */
    private void openRecoveryPage() {
        for (int i = 0; i < recoveryNum; i++) {
            String recoveryPage = "recovery_page_" + i;
            String recoveryUrl = prefs.getString(recoveryPage, "about:blank");
            if (!recoveryUrl.equals(homePage)) {
                addNewPageForeground(seniorUrl(recoveryUrl));
            }
            prefs.edit().remove(recoveryPage).apply();
        }
    }

    /**
     * 响应外部调起
     */
    private void openExternalUrl(boolean isFlie, String externalUrl) {
        if (externalUrl != null) {
            if (isFlie) {
                addNewPageForeground(externalUrl);
            } else {
                addNewPageForeground(seniorUrl(externalUrl));
            }
        }
    }

    /**
     * 初始化多页面切换
     */
    private void initMultiple() {
        qrButton = findViewById(R.id.qr_button);
        addButton = findViewById(R.id.add_button);
        emptyButton = findViewById(R.id.empty_button);
        webPageRecycler = findViewById(R.id.multiple_recycler_view);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, QR_REQUEST_CODE);
                drawerLayout.closeDrawers();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPageForeground(homePage);
                drawerLayout.closeDrawers();
            }
        });

        emptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionSnackBar(findViewById(R.id.nav_multiple), "确定清空所有窗口吗？(此操作不可逆)", "清空", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAllWebPage();
                        drawerLayout.closeDrawers();
                    }
                });
            }
        });

        webPageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        navMenu = findViewById(R.id.nav_menu);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        noTraceSwitch = findViewById(R.id.no_trace_switch);
        uaPcSwitch = findViewById(R.id.ua_pc_switch);
        fullScreenSwitch = findViewById(R.id.full_screen_switch);
        bookmarkButton = findViewById(R.id.bookmark_);
        historyButton = findViewById(R.id.history_);
        downloadButton = findViewById(R.id.download_);
        shareButton = findViewById(R.id.share_);
        exportButton = findViewById(R.id.export_);
        uaButton = findViewById(R.id.ua_);
        sourceButton = findViewById(R.id.source_);
        settingsButton = findViewById(R.id.settings_);
        oneText = findViewById(R.id.one_text);

        darkModeSwitch.setChecked(darkMode);
        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getBoolean("dark_mode_follow_system", true)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("dark_mode_follow_system", false);
                    editor.apply();
                    showSnackBar(navMenu, "深色模式跟随系统已失效(可在设置中重新开启)");
                }
            }
        });
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                darkMode = isChecked;
                changeDarkMode();
            }
        });

        noTraceSwitch.setChecked(noTrace);
        noTraceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                noTrace = isChecked;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("no_trace", noTrace);
                editor.apply();
                if (noTrace) {
                    showSnackBar(navMenu, "无痕浏览已开启");
                } else {
                    showSnackBar(navMenu, "无痕浏览已关闭");
                }
            }
        });

        uaPcSwitch.setChecked(uaPC);
        uaPcSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                uaPC = isChecked;
                customUA = false;
                customUserAgent = "";
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("ua_pc", uaPC);
                editor.putBoolean("custom_ua", false);
                editor.putString("custom_user_agent", customUserAgent);
                editor.apply();
                changeUA();
            }
        });

        fullScreenSwitch.setChecked(fullScreen);
        fullScreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fullScreen = isChecked;
                changeFullScreen();
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, BookmarkActivity.class), BOOKMARK_RESQUEST_CODE);
                drawerLayout.closeDrawers();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, HistoryActivity.class), HISTORY_RESQUEST_CODE);
                drawerLayout.closeDrawers();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DownloadActivity.class));
                drawerLayout.closeDrawers();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareTitle = webViews.get(topPage).getTitle() + "\n";
                String shareUrl = webViews.get(topPage).getUrl();
                if (shareUrl.equals("about:blank")) {
                    shareTitle = "BIJOU 2: 期待你的如约而至！\n";
                    shareUrl = "https://www.coolapk.com/apk/top.myhdg.bijou";
                }
                intent.putExtra(Intent.EXTRA_TEXT, "来自BIJOU 2的分享:\n" + shareTitle + shareUrl);
                MainActivity.this.startActivity(intent);
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutHome.getVisibility() == View.VISIBLE) {
                    showSnackBar(navMenu, "不支持主页导出");
                } else {
                    @SuppressLint("InflateParams") View exportView = LayoutInflater.from(MainActivity.this).inflate(R.layout.export_pop_window, null);
                    initExportPopWindow(exportView);
                    CustomPopWindow exportPopWindow = new CustomPopWindow.PopupWindowBuilder(MainActivity.this)
                            .setView(exportView)
                            .create()
                            .showAsDropDown(exportButton, 0, -94);
                }
            }
        });

        uaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UAChangeDialog dialog = new UAChangeDialog(MainActivity.this);
                dialog.setData(customUserAgent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uaPC = false;
                        uaPcSwitch.setChecked(false);
                        customUA = true;
                        customUserAgent = dialog.uaEdit.getText().toString();
                        KeyboardUtil.closeKeyboard(MainActivity.this, dialog.uaEdit);
                        dialog.dismiss();
                        changeUA();
                        showSnackBar(navMenu, "浏览器标识应用成功");

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("ua_pc", uaPC);
                        editor.putBoolean("custom_ua", customUA);
                        editor.putString("custom_user_agent", customUserAgent);
                        editor.apply();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uaPC = false;
                        uaPcSwitch.setChecked(false);
                        customUA = false;
                        customUserAgent = "";
                        KeyboardUtil.closeKeyboard(MainActivity.this, dialog.uaEdit);
                        dialog.dismiss();
                        changeUA();
                        showSnackBar(navMenu, "已恢复默认浏览器标识");

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("ua_pc", uaPC);
                        editor.putBoolean("custom_ua", customUA);
                        editor.putString("custom_user_agent", customUserAgent);
                        editor.apply();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KeyboardUtil.closeKeyboard(MainActivity.this, dialog.uaEdit);
                        dialog.dismiss();
                    }
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        sourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webViews.get(topPage).getUrl().startsWith("http://") || webViews.get(topPage).getUrl().startsWith("https://")) {
                    if (htmlSource != null) {
                        Intent intent = new Intent(MainActivity.this, SourceActivity.class);
                        startActivity(intent);
                    } else {
                        showSnackBar(navMenu, "暂未获取到网页源码");
                    }
                } else {
                    showSnackBar(navMenu, "暂时仅支持http网页及部分https网页查看源码");
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), SETTINGS_RESQUEST_CODE);
                drawerLayout.closeDrawers();
            }
        });

        setOneText();
        oneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOneText();
            }
        });
        oneText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyContent(oneText.getText().toString());
                return true;
            }
        });
    }

    /**
     * 初始化导出弹窗
     */
    private void initExportPopWindow(View exportView) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.picture_:
                        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_EXPORT_IMG)) {
                            exportPicture();
                        }
                        break;
                    case R.id.pdf_:
                        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                        String jobName = "BIJOU 导出网页：" + webViews.get(topPage).getTitle();
                        PrintDocumentAdapter printAdapter = webViews.get(topPage).createPrintDocumentAdapter(jobName);
                        assert printManager != null;
                        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                        break;
                }
            }
        };
        exportView.findViewById(R.id.picture_).setOnClickListener(clickListener);
        exportView.findViewById(R.id.pdf_).setOnClickListener(clickListener);
    }

    /**
     * 导出网页为图片
     */
    private void exportPicture() {
        showSnackBar(navMenu, "网页图片将在片刻后导入系统图库");
        final EnhancedWebView webView = webViews.get(topPage);
        final String title = "BIJOU 导出网页：" + webView.getTitle() + ".jpg";
        final File file = getExternalFilesDir(title);
        assert file != null;
        if (file.exists()) {
            file.delete();
        } else {
            file.mkdir();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(file.getAbsoluteFile());
                    Bitmap bitmap = Bitmap.createBitmap(webView.getPageWidth(), webView.getPageHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    webView.draw(canvas);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                        System.gc();
                    }
                    MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(),
                            file.getAbsolutePath(), title, null);
                    MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));// 通知图库更新
                    file.delete();
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSnackBar(navMenu, "网页图片导出失败，请检查存储权限");
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 初始化顶部工具栏
     */
    private void initToolbar() {
        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbar = findViewById(R.id.toolbar);
        toolbarEdit = findViewById(R.id.toolbar_edit);
        menuButton = toolbar.getMenuButton();
        goButton = toolbar.getGoButton();
        multipleButton = toolbar.getMultipleButton();
        progressBar = findViewById(R.id.progress_bar);
        suggestRecyclerView = findViewById(R.id.suggest_recycler_iew);
        layoutHome = findViewById(R.id.layout_home);
        startText = findViewById(R.id.start_text);
        fullVideoLayout = findViewById(R.id.full_video_layout);
        homeQuickRecylerView = findViewById(R.id.home_quick_recyler_view);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                syncWebPageList();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = toolbarEdit.getText().toString();
                if (content.length() > 0) {
                    webViews.get(topPage).loadUrl(seniorUrl(content));
                    closeSuggestList();
                }
            }
        });

        multipleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        toolbarEdit.setSelectAllOnFocus(true);
        toolbarEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String url = webViews.get(topPage).getUrl();
                    if (url != null && !url.equals("about:blank")) {
                        toolbarEdit.setText(url);
                        toolbarEdit.selectAll();
                    }
                    KeyboardUtil.openKeyboard(MainActivity.this, toolbarEdit);
                    goButton.setVisibility(View.VISIBLE);
                    openSuggestList();
                } else {
                    KeyboardUtil.closeKeyboard(MainActivity.this, toolbarEdit);
                    goButton.setVisibility(View.GONE);
                    closeSuggestList();
                }
            }
        });
        toolbarEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String content = toolbarEdit.getText().toString();
                    if (content.length() > 0) {
                        webViews.get(topPage).loadUrl(seniorUrl(content));
                        closeSuggestList();
                    }
                }
                return false;
            }
        });
        toolbarEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (toolbarEdit.getText().toString().length() > 0) {
                    setSuggestList(toolbarEdit.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (toolbarEdit.getText().toString().length() == 0) {
                    suggestList.clear();
                    suggestAdapter.notifyDataSetChanged();
                }
            }
        });

        suggestRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarEdit.requestFocus();
            }
        });

        homeQuickRecylerView.setLayoutManager(new GridLayoutManager(this, 5));
        homeQuickList = new ArrayList<>();
        homeQuicks = LitePal.findAll(HomeQuick.class);
        homeQuickAdapter = new HomeQuickAdapter(homeQuickList);
        homeQuickRecylerView.setAdapter(homeQuickAdapter);
        syncHomeQuickList();

        ItemTouchHelper helper = new ItemTouchHelper(new DragHomeQuickItemCallback(homeQuickAdapter));
        helper.attachToRecyclerView(homeQuickRecylerView);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1:
                        webViews.get(topPage).loadUrl(seniorUrl(homeQuickUrl));
                        break;
                    case 2:
                        @SuppressLint("InflateParams") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_bookmark_pop_window, null);
                        initEditHomeQuickPopWindow(view);
                        editHomeQuickPopWindow = new CustomPopWindow.PopupWindowBuilder(MainActivity.this)
                                .setView(view)
                                .enableBackgroundDark(true)
                                .create()
                                .showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 同步主页快捷方式列表
     */
    private void syncHomeQuickList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                homeQuickList.clear();
                for (final HomeQuick homeQuick : homeQuicks) {
                    HomeQuickItem homeQuickItem = new HomeQuickItem();
                    final String homeQuickUrl = homeQuick.getWebsite();
                    homeQuickItem.setId(homeQuick.getId());
                    homeQuickItem.setTitle(homeQuick.getTitle());
                    homeQuickItem.setWebsite(homeQuickUrl);
                    homeQuickItem.setOpenClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.this.homeQuickUrl = homeQuickUrl;
                            editHomeQuick = homeQuick;
                            homeQuickLastTime = homeQuickCurTime;
                            homeQuickCurTime = System.currentTimeMillis();
                            if (homeQuickCurTime - homeQuickLastTime < 300) {
                                homeQuickCurTime = 0;
                                homeQuickLastTime = 0;
                                handler.removeMessages(1);
                                handler.sendEmptyMessage(2);
                            } else {
                                handler.sendEmptyMessageDelayed(1, 310);
                            }
                        }
                    });
                    homeQuickList.add(homeQuickItem);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        homeQuickAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化编辑书签弹窗
     */
    private void initEditHomeQuickPopWindow(View view) {
        TextView textView = view.findViewById(R.id.add_bookmark_text);
        textView.setText("编辑");
        final EditText titleEdit = view.findViewById(R.id.title_edit);
        titleEdit.setText(editHomeQuick.getTitle());
        final EditText websiteEdit = view.findViewById(R.id.website_edit);
        websiteEdit.setText(editHomeQuick.getWebsite());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_bookmark_button:
                        if (!titleEdit.getText().toString().equals("") && !websiteEdit.getText().toString().equals("")) {
                            homeQuicks.get(homeQuicks.indexOf(editHomeQuick)).setTitle(titleEdit.getText().toString());
                            homeQuicks.get(homeQuicks.indexOf(editHomeQuick)).setWebsite(websiteEdit.getText().toString());
                            homeQuicks.get(homeQuicks.indexOf(editHomeQuick)).save();
                            syncHomeQuickList();
                            editHomeQuickPopWindow.dissmiss();
                            showSnackBar(coordinatorLayout, "已保存更改");
                        }
                        break;
                    case R.id.add_home_quick_button:
                        if (!titleEdit.getText().toString().equals("") && !websiteEdit.getText().toString().equals("")) {
                            Bookmark bookmark = new Bookmark();
                            bookmark.setTitle(titleEdit.getText().toString());
                            bookmark.setWebsite(websiteEdit.getText().toString());
                            bookmark.save();
                            editHomeQuickPopWindow.dissmiss();
                            showSnackBar(coordinatorLayout, "已添加至书签");
                        }
                        break;
                    case R.id.cancel_add_bookmark_button:
                        editHomeQuickPopWindow.dissmiss();
                        showActionSnackBar(coordinatorLayout, "确定删除快捷方式吗？(此操作不可逆)", "删除", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LitePal.delete(HomeQuick.class, editHomeQuick.getId());
                                homeQuicks.remove(editHomeQuick);
                                syncHomeQuickList();
                                showSnackBar(coordinatorLayout, "快捷方式已删除");
                            }
                        });
                        break;
                }
            }
        };

        TextView editButton = view.findViewById(R.id.add_bookmark_button);
        editButton.setText("更改");
        editButton.setOnClickListener(clickListener);
        TextView bookmarkButton = view.findViewById(R.id.add_home_quick_button);
        bookmarkButton.setText("书签");
        bookmarkButton.setOnClickListener(clickListener);
        TextView cancelButton = view.findViewById(R.id.cancel_add_bookmark_button);
        cancelButton.setText("删除");
        cancelButton.setOnClickListener(clickListener);
    }

    /**
     * 初始化查找栏
     */
    private void initFindBar() {
        findLayout = findViewById(R.id.find_layout);
        cancelFindButton = findViewById(R.id.cancel_find_button);
        findEdit = findViewById(R.id.find_edit);
        findBackButton = findViewById(R.id.find_back_button);
        findNextButton = findViewById(R.id.find_next_button);

        findEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                webViews.get(topPage).findAllAsync(findEdit.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    KeyboardUtil.closeKeyboard(MainActivity.this, findEdit);
                    webViews.get(topPage).findNext(true);
                }
                return false;
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancel_find_button:
                        closeFindLayout();
                        break;
                    case R.id.find_back_button:
                        KeyboardUtil.closeKeyboard(MainActivity.this, findEdit);
                        webViews.get(topPage).findNext(false);
                        break;
                    case R.id.find_next_button:
                        KeyboardUtil.closeKeyboard(MainActivity.this, findEdit);
                        webViews.get(topPage).findNext(true);
                        break;
                }
            }
        };

        cancelFindButton.setOnClickListener(clickListener);
        findBackButton.setOnClickListener(clickListener);
        findNextButton.setOnClickListener(clickListener);
    }

    /**
     * 打开查找布局
     */
    private void openFindLayout() {
        toolbarLayout.setVisibility(View.GONE);
        findLayout.setVisibility(View.VISIBLE);
        findEdit.requestFocus();
        KeyboardUtil.openKeyboard(this, findEdit);
    }

    /**
     * 关闭查找布局
     */
    private void closeFindLayout() {
        findLayout.setVisibility(View.GONE);
        toolbarLayout.setVisibility(View.VISIBLE);
        findEdit.setText("");
        findEdit.clearFocus();
        KeyboardUtil.closeKeyboard(this, findEdit);
        webViews.get(topPage).clearMatches();
    }

    /**
     * 初始化悬浮按钮
     */
    private void initFloatingButton() {
        floatingActionMenu = findViewById(R.id.floating_action_menu);
        addBookmarkFab = findViewById(R.id.fab_add_bookmark);
        reloadFab = findViewById(R.id.fab_reload);
        goBackFab = findViewById(R.id.fab_go_back);
        goForwardFab = findViewById(R.id.fab_go_forward);
        homeFab = findViewById(R.id.fab_home);
        findFab = findViewById(R.id.fab_find);

        addBookmarkFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!webViews.get(topPage).getUrl().equals("about:blank")) {
                    @SuppressLint("InflateParams") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_bookmark_pop_window, null);
                    initAddBookmarkPopWindow(view);
                    addBookmarkPopWindow = new CustomPopWindow.PopupWindowBuilder(MainActivity.this)
                            .setView(view)
                            .enableBackgroundDark(true)
                            .enableOutsideTouchableDissmiss(false)
                            .create()
                            .showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);
                } else {
                    showSnackBar(coordinatorLayout, "主页无需添加书签");
                }
            }
        });

        reloadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webViews.get(topPage).reload();
            }
        });

        goBackFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webViews.get(topPage).canGoBack()) {
                    webViews.get(topPage).goBack();
                }
            }
        });

        goForwardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webViews.get(topPage).canGoForward()) {
                    webViews.get(topPage).goForward();
                }
            }
        });

        homeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webViews.get(topPage).loadUrl(seniorUrl(homePage));
            }
        });
        homeFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                webViews.get(topPage).scrollTo(0, 0);
                return true;
            }
        });

        findFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webViews.get(topPage).getUrl().equals("about:blank")) {
                    showSnackBar(coordinatorLayout, "BIJOU默认主页不支持网页查找内容");
                } else {
                    if (findLayout.getVisibility() == View.VISIBLE) {
                        closeFindLayout();
                    } else {
                        openFindLayout();
                        floatingActionMenu.close(true);
                    }
                }
            }
        });
    }

    /**
     * 初始化添加书签弹窗
     */
    private void initAddBookmarkPopWindow(View view) {
        final EditText titleEdit = view.findViewById(R.id.title_edit);
        titleEdit.setText(webViews.get(topPage).getTitle());
        final EditText websiteEdit = view.findViewById(R.id.website_edit);
        websiteEdit.setText(webViews.get(topPage).getUrl());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_bookmark_button:
                        if (!titleEdit.getText().toString().equals("") && !websiteEdit.getText().toString().equals("")) {
                            Bookmark bookmark = new Bookmark();
                            bookmark.setTitle(titleEdit.getText().toString());
                            bookmark.setWebsite(websiteEdit.getText().toString());
                            bookmark.save();
                            addBookmarkPopWindow.dissmiss();
                            showSnackBar(coordinatorLayout, "书签添加成功");
                        }
                        break;
                    case R.id.add_home_quick_button:
                        if (!titleEdit.getText().toString().equals("") && !websiteEdit.getText().toString().equals("")) {
                            HomeQuick homeQuick = new HomeQuick();
                            homeQuick.setTitle(titleEdit.getText().toString());
                            homeQuick.setWebsite(websiteEdit.getText().toString());
                            homeQuick.save();
                            homeQuicks.add(homeQuick);
                            syncHomeQuickList();
                            addBookmarkPopWindow.dissmiss();
                            showSnackBar(coordinatorLayout, "已添加至主页");
                        }
                        break;
                    case R.id.cancel_add_bookmark_button:
                        addBookmarkPopWindow.dissmiss();
                        break;
                }
            }
        };

        TextView bookmarkButton = view.findViewById(R.id.add_bookmark_button);
        bookmarkButton.setOnClickListener(clickListener);
        TextView homeQuickButton = view.findViewById(R.id.add_home_quick_button);
        homeQuickButton.setOnClickListener(clickListener);
        TextView cancelButton = view.findViewById(R.id.cancel_add_bookmark_button);
        cancelButton.setOnClickListener(clickListener);
    }

    /**
     * 获取深色模式CSS
     */
    private void getDarkModeCSS() {
        InputStream inputStream = MainActivity.this.getResources().openRawResource(R.raw.dark_mode);
        byte[] buffer = new byte[0];
        try {
            buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        darkModeCSS = Base64.encodeToString(buffer, Base64.NO_WRAP);
    }

    /**
     * 初始化WebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(final EnhancedWebView webView, String url) {
        WebSettings settings = webView.getSettings();
        settings.setSupportMultipleWindows(true);//支持多窗口
        settings.setJavaScriptEnabled(enabledJavaScript);
        settings.setUseWideViewPort(true);//设置是否支持HTML的“viewport”标签或者使用wide viewport
        settings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        settings.setSupportZoom(true);//支持缩放
        settings.setBuiltInZoomControls(true);//设置内置的缩放控件
        settings.setDisplayZoomControls(false);//隐藏原生的缩放控件
        settings.setAllowFileAccess(true);//设置可以访问文件
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        settings.setDomStorageEnabled(true);// 开启 DOM storage API 功能
        settings.setDatabaseEnabled(true);//开启 database storage API 功能
        settings.setAppCacheEnabled(true);//开启 Application Caches 功能
        settings.setGeolocationEnabled(true);//开启定位功能
        settings.setDatabaseEnabled(true);//启用数据库
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持JS打开新窗口
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        if (isNetworkConnected(this)) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        File cacheDir = this.getCacheDir();
        if (cacheDir != null) {
            String appCachePath = cacheDir.getAbsolutePath();
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            settings.setAppCacheEnabled(true);
            settings.setAppCachePath(appCachePath);
        }
        if (uaPC) {
            settings.setUserAgentString(PC_UA);
            swipeRefreshLayout.setEnabled(false);
        } else {
            swipeRefreshLayout.setEnabled(enabledDropReload);
            settings.setUserAgentString(DEFAULT_UA);
            if (customUA) {
                settings.setUserAgentString(customUserAgent);
            }
        }

        webView.requestFocus();
        webView.requestFocusFromTouch();
        webView.requestFocusFromTouch();//支持获取手势焦点
        webView.setDrawingCacheEnabled(true);//开启绘制缓存
        webView.addJavascriptInterface(new JavascriptInterface(), "local_obj");
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);//开启硬件加速

        //添加下载监听
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                downloadUrl = url;
                getDownloadFileName();
                showActionSnackBar(coordinatorLayout, "是否希望创建下载任务:  " + url, "下载", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_DOWNLOAD)) {
                            @SuppressLint("InflateParams") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_download_pop_window, null);
                            initAddDownloadPopWindow(view);
                            addDownloadPopWindow = new CustomPopWindow.PopupWindowBuilder(MainActivity.this)
                                    .setView(view)
                                    .enableBackgroundDark(true)
                                    .enableOutsideTouchableDissmiss(false)
                                    .create()
                                    .showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);
                        }
                    }
                });
            }
        });
        //添加长按监听
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (result == null) {
                    return false;
                }
                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE) {
                    return false;
                }
                if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {
                    return false;
                }
                switch (type) {
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        longClickUrl = result.getExtra();
                        int clickAncX = (int) (webViews.get(topPage).getClickX() - 250);
                        int clickAncY = (int) (webViews.get(topPage).getClickY() - 480);
                        if (clickAncX < 0) {
                            clickAncX = 50;
                        }
                        if (clickAncY < 0) {
                            clickAncY = 50;
                        }
                        @SuppressLint("InflateParams") View longSrcAnchorView = LayoutInflater.from(MainActivity.this)
                                .inflate(R.layout.long_src_anchor_pop_window, null);
                        initLongSrcAnchorPopWindow(longSrcAnchorView);
                        longSrcAnchorPopWindow = new CustomPopWindow.PopupWindowBuilder(MainActivity.this)
                                .setView(longSrcAnchorView)
                                .enableBackgroundDark(true)
                                .create()
                                .showAsDropDown(webViews.get(topPage), clickAncX, clickAncY);
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE: // 图片
                        longClickUrl = result.getExtra();
                        int clickImgX = (int) (webViews.get(topPage).getClickX() - 250);
                        int clickImgY = (int) (webViews.get(topPage).getClickY() - 700);
                        if (clickImgX < 0) {
                            clickImgX = 50;
                        }
                        if (clickImgY < 0) {
                            clickImgY = 50;
                        }
                        @SuppressLint("InflateParams") View longImgView = LayoutInflater.from(MainActivity.this)
                                .inflate(R.layout.long_img_pop_window, null);
                        initLongImgPopWindow(longImgView);
                        longImgPopWindow = new CustomPopWindow.PopupWindowBuilder(MainActivity.this)
                                .setView(longImgView)
                                .enableBackgroundDark(true)
                                .create()
                                .showAsDropDown(webViews.get(topPage), clickImgX, clickImgY);
                        break;
                }
                return true;
            }
        });

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();//忽略证书错误
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                if (!url.startsWith("http")) {
                    showActionSnackBar(coordinatorLayout, "是否响应该网站的请求:  " + url, "响应", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                closeSuggestList();

                //注入深色模式CSS
                if (darkMode) {
                    if (isDarkModeExceptUrl(url)) {
                        view.loadUrl("javascript:(function() {"
                                + "var parent = document.getElementsByTagName('head').item(0);"
                                + "var style = document.createElement('style');"
                                + "style.type = 'text/css';"
                                + "style.innerHTML = window.atob('"
                                + darkModeCSS + "');"
                                + "parent.appendChild(style)"
                                + "})();");
                    }
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (darkMode) {
                    if (isDarkModeExceptUrl(url)) {
                        view.loadUrl("javascript:(function() {"
                                + "var parent = document.getElementsByTagName('head').item(0);"
                                + "var style = document.createElement('style');"
                                + "style.type = 'text/css';"
                                + "style.innerHTML = window.atob('"
                                + darkModeCSS + "');"
                                + "parent.appendChild(style)"
                                + "})();");
                    }
                }

                //获取html源码
                view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

                //全屏播放视频
                view.loadUrl(TagUtils.getJs(url));

                //移除广告
                view.loadUrl(REMOVE_AD_JS);

                //网页长度过小时，关闭下拉刷新
                if (webView.getPageHeight() - screenHeight < 300) {
                    swipeRefreshLayout.setEnabled(false);
                } else {
                    swipeRefreshLayout.setEnabled(enabledDropReload);
                    if (uaPC) {
                        swipeRefreshLayout.setEnabled(false);
                    }
                }

                if (!noTrace) {
                    if (!view.getUrl().equals(homePage)) {
                        History history = new History();
                        history.setTitle(view.getTitle());
                        history.setWebsite(view.getUrl());
                        history.setTime(getTime());
                        history.save();
                    }
                }

                if (enabledRecovery) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("recovery_num", webViews.size());
                    for (int i = 0; i < webViews.size(); i++) {
                        String recoveryPage = "recovery_page_" + i;
                        editor.putString(recoveryPage, webViews.get(i).getUrl());
                    }
                    editor.apply();
                }
            }
        };
        webView.setWebViewClient(webViewClient);

        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public boolean onCreateWindow(final WebView view, boolean isDialog, boolean isUserGesture, final Message resultMsg) {
                if (!isDialog) {
                    showActionSnackBar(coordinatorLayout, "该网站希望打开新的窗口", "打开", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final WebView webTemp = new WebView(MainActivity.this);
                            webTemp.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    addNewPageForeground(url);

                                    webTemp.clearCache(true);
                                    webTemp.clearHistory();
                                    webTemp.clearFormData();
                                    webTemp.destroy();
                                    return true;
                                }
                            });
                            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                            transport.setWebView(webTemp);
                            resultMsg.sendToTarget();
                        }
                    });
                }
                return true;
            }

            @Override
            public void onCloseWindow(WebView window) {
                showActionSnackBar(coordinatorLayout, "该网站希望关闭当前窗口", "关闭", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteWebPage(topPage);
                    }
                });
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setToolbarContentHint();
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (webViews.get(topPage) == view) {
                    if (newProgress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                        //注入深色模式CSS
                        if (newProgress > 20 && darkMode) {
                            if (isDarkModeExceptUrl(view.getUrl())) {
                                view.loadUrl("javascript:(function() {"
                                        + "var parent = document.getElementsByTagName('head').item(0);"
                                        + "var style = document.createElement('style');"
                                        + "style.type = 'text/css';"
                                        + "style.innerHTML = window.atob('"
                                        + darkModeCSS + "');"
                                        + "parent.appendChild(style)"
                                        + "})();");
                            }
                        }
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "选择文件"), FILE_CHOOSER_RESULT_CODE);
                return true;
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt();
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                showActionSnackBar(coordinatorLayout, "是否授权该网站获取位置", "授权", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, ACCESS_COARSE_LOCATION)
                                || checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION)) {
                            callback.invoke(origin, true, true);//第二个参数:是否同意网站获取权限  第三个参数：是否希望内核记住
                        }
                    }
                });

                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                fullVideoLayout.setVisibility(View.VISIBLE);
                fullVideoLayout.addView(view);
                hideStatus();
                videoCallback = callback;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                if (videoCallback != null) {
                    videoCallback.onCustomViewHidden();
                    videoCallback = null;
                }
                fullVideoLayout.removeAllViews();
                fullVideoLayout.setVisibility(View.GONE);
                closeSuggestList();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                changeFullScreen();
                super.onHideCustomView();
            }
        };
        webView.setWebChromeClient(webChromeClient);

        webView.loadUrl(seniorUrl(url));
    }

    /**
     * 判断是否为深色模式例外网站
     */
    private boolean isDarkModeExceptUrl(String url) {
        boolean result = false;
        for (String s : DARKMODE_EXCEPT) {
            result = url.contains(s);
            if (result) {
                break;
            }
        }
        return !result;
    }

    /**
     * 获取下载文件名
     */
    private void getDownloadFileName() {
        HttpUtil.sendOkHttpRequest(downloadUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String content = response.header("Content-Disposition");
                try {
                    if (content != null) {
                        if (content.split("\"").length > 1) {
                            downloadFileName = content.split("\"")[1];
                            downloadFileName = downloadFileName.replaceAll(getString(R.string.regex), "");
                        } else {
                            downloadFileName = content.split("=")[1];
                            downloadFileName = downloadFileName.replaceAll(getString(R.string.regex), "");
                        }
                    } else {
                        downloadFileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化下载弹窗
     */
    private void initAddDownloadPopWindow(View view) {
        final EditText fileNameEdit = view.findViewById(R.id.file_name_edit);
        fileNameEdit.setText(downloadFileName);
        final EditText downloadUrlEdit = view.findViewById(R.id.dl_url_edit);
        downloadUrlEdit.setText(downloadUrl);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_dl_button:
                        if (!fileNameEdit.getText().toString().equals("") && !downloadUrlEdit.getText().toString().equals("")) {
                            Aria.download(MainActivity.this)
                                    .load(downloadUrlEdit.getText().toString())
                                    .setFilePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                                            + "/BIJOU/" + fileNameEdit.getText().toString())
                                    .ignoreFilePathOccupy()
                                    .create();
                            KeyboardUtil.closeKeyboard(MainActivity.this, fileNameEdit);
                            KeyboardUtil.closeKeyboard(MainActivity.this, downloadUrlEdit);
                            downloadFileName = "";
                            downloadUrl = "";
                            addDownloadPopWindow.dissmiss();
                            showSnackBar(coordinatorLayout, "若无异常，下载任务将在片刻后创建");
                        }
                        break;
                    case R.id.cancel_dl_button:
                        addDownloadPopWindow.dissmiss();
                        break;
                }
            }
        };

        TextView downloadButton = view.findViewById(R.id.add_dl_button);
        downloadButton.setOnClickListener(clickListener);
        TextView cancelButton = view.findViewById(R.id.cancel_dl_button);
        cancelButton.setOnClickListener(clickListener);
    }

    /**
     * 获取系统时间
     */
    private String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * 初始化长按超链接弹窗
     */
    private void initLongSrcAnchorPopWindow(View longSrcAnchorView) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.copy_anc_button:
                        copyContent(longClickUrl);
                        longSrcAnchorPopWindow.dissmiss();
                        break;
                    case R.id.new_page_fore_open_button:
                        addNewPageForeground(seniorUrl(longClickUrl));
                        longSrcAnchorPopWindow.dissmiss();
                        break;
                    case R.id.new_page_back_open_button:
                        addNewPageBackstage(seniorUrl(longClickUrl));
                        longSrcAnchorPopWindow.dissmiss();
                        showSnackBar(coordinatorLayout, "已后台打开目标链接");
                        break;
                }
            }
        };
        longSrcAnchorView.findViewById(R.id.copy_anc_button).setOnClickListener(clickListener);
        longSrcAnchorView.findViewById(R.id.new_page_fore_open_button).setOnClickListener(clickListener);
        longSrcAnchorView.findViewById(R.id.new_page_back_open_button).setOnClickListener(clickListener);
    }

    /**
     * 初始化长按图片接弹窗
     */
    private void initLongImgPopWindow(View longImgView) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.show_img_button:
                        Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                        intent.putExtra("url", longClickUrl);
                        startActivity(intent);
                        longImgPopWindow.dissmiss();
                        break;
                    case R.id.save_img_button:
                        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_SAVE_IMG)) {
                            new SaveImage().execute();
                        }
                        longImgPopWindow.dissmiss();
                        break;
                    case R.id.copy_img_url_button:
                        copyContent(longClickUrl);
                        longImgPopWindow.dissmiss();
                        break;
                    case R.id.new_img_fore_open_button:
                        addNewPageForeground(seniorUrl(longClickUrl));
                        longImgPopWindow.dissmiss();
                        break;
                    case R.id.new_img_back_open_button:
                        addNewPageBackstage(seniorUrl(longClickUrl));
                        longImgPopWindow.dissmiss();
                        showSnackBar(coordinatorLayout, "已后台打开目标链接");
                        break;
                }
            }
        };
        longImgView.findViewById(R.id.show_img_button).setOnClickListener(clickListener);
        longImgView.findViewById(R.id.save_img_button).setOnClickListener(clickListener);
        longImgView.findViewById(R.id.copy_img_url_button).setOnClickListener(clickListener);
        longImgView.findViewById(R.id.new_img_fore_open_button).setOnClickListener(clickListener);
        longImgView.findViewById(R.id.new_img_back_open_button).setOnClickListener(clickListener);
    }

    /**
     * 复制内容
     */
    private void copyContent(String content) {
        ClipData mClipData = ClipData.newPlainText("Label", content);
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        cm.setPrimaryClip(mClipData);
        showSnackBar(navMenu, "内容已复制");
    }

    /**
     * 判断是否联网
     */
    private static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            assert mConnectivityManager != null;
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            return mNetworkInfo != null;
        }
        return false;
    }

    /**
     * 切换UA
     */
    private void changeUA() {
        for (EnhancedWebView webView : webViews) {
            if (uaPC) {
                webView.getSettings().setUserAgentString(PC_UA);
            } else {
                if (customUA) {
                    webView.getSettings().setUserAgentString(customUserAgent);
                } else {
                    webView.getSettings().setUserAgentString(DEFAULT_UA);
                }
            }
            webView.reload();
        }
    }

    /**
     * 同步多窗口列表
     */
    private void syncWebPageList() {
        webPageList.clear();
        for (int i = 0; i < webViews.size(); i++) {
            EnhancedWebView webView = webViews.get(i);
            final WebPage webPage = new WebPage();
            webPage.setWebPageId(i);
            webPage.setWebPageTitle(webView.getTitle());
            webPage.setWebPageSite(webView.getUrl());
            webPage.setDeleteClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWebPage(webPage.getWebPageId());
                }
            });
            webPage.setWebPageItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeWebPage(webPage.getWebPageId());
                    drawerLayout.closeDrawers();
                }
            });
            webPageList.add(webPage);
        }
        webPageAdapter.notifyDataSetChanged();
    }

    /**
     * 处理Url
     */
    private String seniorUrl(String originUrl) {
        try {
            if (originUrl.length() > 0) {
                if (originUrl.equals("about:blank")) {
                    return originUrl;
                }
                try {
                    URL url = new URL(originUrl);
                    return url.toString();
                } catch (Exception ew) {
                    try {
                        if (Patterns.WEB_URL.matcher("https://" + originUrl).matches()) {
                            return "https://" + originUrl;
                        } else {
                            if (Patterns.WEB_URL.matcher(engine + originUrl).matches()) {
                                return engine + originUrl;
                            } else {
                                return "https://www.baidu.com/s?wd=" + originUrl;
                            }
                        }
                    } catch (Exception eh) {
                        return "https://www.baidu.com/s?wd=" + originUrl;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置地址栏内容
     */
    private void setToolbarContentHint() {
        try {
            switch (toolbarContent) {
                case TITLE:
                    String title = webViews.get(topPage).getTitle();
                    toolbarEdit.setHint(title);
                    break;
                case WEBSITE:
                    toolbarEdit.setHint(webViews.get(topPage).getUrl());
                    break;
                case DOMAIN:
                    toolbarEdit.setHint(webViews.get(topPage).getOriginalUrl().split("/")[2]);
                    break;
            }
            if (webViews.get(topPage).getUrl().equals("about:blank")) {
                toolbarEdit.setHint("BIJOU主页");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取搜索建议列表
     */
    private void setSuggestList(String s) {
        HttpUtil.sendOkHttpRequest(SUGGEST_API + s, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showSnackBar(coordinatorLayout, "搜索建议获取失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String content = Objects.requireNonNull(response.body()).string();
                    String suggestContent = content.substring(17);
                    JSONObject contentObject = new JSONObject(suggestContent);
                    JSONArray suggestArray = contentObject.getJSONArray("s");
                    suggestList.clear();
                    for (int i = 0; i < suggestArray.length(); i++) {
                        final Suggest suggest = new Suggest();
                        suggest.setSuggestItemClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toolbarEdit.setText(suggest.getSuggestText());
                                        toolbarEdit.setSelection(suggest.getSuggestText().length());
                                    }
                                });
                            }
                        });
                        suggest.setSuggestText(suggestArray.get(i).toString());
                        suggestList.add(suggest);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            suggestAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 打开搜索建议列表
     */
    private void openSuggestList() {
        layoutHome.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.GONE);
        suggestRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭搜索建议列表
     */
    private void closeSuggestList() {
        toolbarEdit.setText("");
        toolbarEdit.clearFocus();
        String url = webViews.get(topPage).getUrl();
        if (url != null && url.equals("about:blank")) {
            openHome();
        } else {
            openWebPage();
        }
    }

    /**
     * 打开主页
     */
    private void openHome() {
        swipeRefreshLayout.setVisibility(View.GONE);
        suggestRecyclerView.setVisibility(View.GONE);
        layoutHome.setVisibility(View.VISIBLE);
    }

    /**
     * 打开网页
     */
    private void openWebPage() {
        suggestRecyclerView.setVisibility(View.GONE);
        layoutHome.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 前台添加新页面
     */
    private void addNewPageForeground(String url) {
        if (webViews.size() != 0) {
            webViews.get(topPage).setVisibility(View.GONE);
        }
        topPage = webViews.size();

        EnhancedWebView webView = new EnhancedWebView(this);
        initWebView(webView, url);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
        webView.setVisibility(View.VISIBLE);
        webViews.add(webView);
        webViewLayout.addView(webView);

        syncWebPageList();

        setToolbarContentHint();

        if (topPage == 10) {
            showSnackBar(coordinatorLayout, "新增过多页面可能会引起系统卡顿或其他未知问题,请谨慎添加");
        }

        closeFindLayout();
    }

    /**
     * 后台添加新页面
     */
    private void addNewPageBackstage(String url) {
        EnhancedWebView webView = new EnhancedWebView(this);
        initWebView(webView, url);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
        webView.setVisibility(View.GONE);
        webViews.add(webView);
        webViewLayout.addView(webView);

        syncWebPageList();

        if (webViews.size() == 10) {
            showSnackBar(coordinatorLayout, "新增过多页面可能会引起系统卡顿或其他未知问题,请谨慎添加");
        }
    }

    /**
     * 更改显示的页面
     */
    private void changeWebPage(int i) {
        webViews.get(topPage).setVisibility(View.GONE);
        topPage = i;
        webViews.get(topPage).setVisibility(View.VISIBLE);
        setToolbarContentHint();
        closeSuggestList();

        //网页长度过小时，关闭下拉刷新
        if (webViews.get(topPage).getPageHeight() - screenHeight < 100) {
            swipeRefreshLayout.setEnabled(false);
        } else {
            swipeRefreshLayout.setEnabled(enabledDropReload);
            if (uaPC) {
                swipeRefreshLayout.setEnabled(false);
            }
        }

        closeFindLayout();
    }

    /**
     * 删除指定页面
     */
    private void deleteWebPage(int i) {
        EnhancedWebView webView = webViews.get(i);
        webViewLayout.removeView(webView);
        webView.loadUrl("about:blank");
        webView.stopLoading();
        webView.clearHistory();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.destroy();
        webViews.remove(webView);

        if (i == topPage) {
            if (webViews.size() == 0) {
                addNewPageForeground(homePage);
            } else {
                topPage--;
                if (topPage < 0) {
                    topPage = 0;
                }
                changeWebPage(topPage);
            }
        }
        if (i < topPage) {
            topPage--;
        }

        syncWebPageList();
    }

    /**
     * 删除所有页面
     */
    private void deleteAllWebPage() {
        for (EnhancedWebView webView : webViews) {
            webViewLayout.removeView(webView);
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.clearHistory();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
        }
        webViews.clear();
        addNewPageForeground(homePage);
        syncWebPageList();
    }

    /**
     * 销毁所有页面
     */
    private void destoryAllWebPage() {
        for (EnhancedWebView webView : webViews) {
            webViewLayout.removeView(webView);
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.clearHistory();
            if (clearCacheExit) {
                webView.clearCache(true);
            }
            if (clearFormDataExit) {
                webView.clearFormData();
            }
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
        }
        webViews.clear();
        webPageList.clear();
        if (clearHistoryExit) {
            LitePal.deleteAll(History.class);
        }
    }

    /**
     * 获取网页源码
     */
    private static class JavascriptInterface {
        JavascriptInterface() {
        }

        @android.webkit.JavascriptInterface
        public void showSource(String html) {
            htmlSource = html;
        }
    }

    /**
     * 设置菜单栏底部一言
     */
    private void setOneText() {
        HttpUtil.sendOkHttpRequest(ONE_TEXT_API, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSnackBar(navMenu, "一言获取失败");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String oneString = "";
                try {
                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                    oneString = jsonObject.getString("hitokoto");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String finalOneString = oneString;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        oneText.setText(finalOneString);
                    }
                });
            }
        });
    }

    /**
     * 获取SnackBar背景颜色
     */
    private int getSnackBarBackgroundColor() {
        if (darkMode) {
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
        if (darkMode) {
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
        darkModeSwitch.setChecked(darkMode);
        if (darkMode) {
            SkinCompatManager.getInstance().loadSkin("dark", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
        } else {
            SkinCompatManager.getInstance().restoreDefaultTheme();
        }
        changeStatusColor();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dark_mode", darkMode);
        editor.apply();
        for (EnhancedWebView webView : webViews) {
            webView.reload();
        }
    }

    /**
     * 隐藏状态栏
     */
    private void hideStatus() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    /**
     * 显示状态栏
     */
    private void showStatus() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        changeStatusColor();
    }

    /**
     * 切换全屏模式
     */
    private void changeFullScreen() {
        if (fullScreen) {
            hideStatus();
        } else {
            showStatus();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("full_screen", fullScreen);
        editor.apply();
    }

    /**
     * 检查权限
     */
    private boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_COARSE_LOCATION:
            case ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showSnackBar(coordinatorLayout, "定位权限获取失败");
                }
                break;
            case WRITE_EXTERNAL_STORAGE_EXPORT_IMG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportPicture();
                } else {
                    showSnackBar(coordinatorLayout, "存储权限获取失败");
                }
                break;
            case WRITE_EXTERNAL_STORAGE_SAVE_IMG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new SaveImage().execute();
                } else {
                    showSnackBar(coordinatorLayout, "存储权限获取失败");
                }
                break;
            case WRITE_EXTERNAL_STORAGE_DOWNLOAD:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    @SuppressLint("InflateParams") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_download_pop_window, null);
                    initAddDownloadPopWindow(view);
                    addDownloadPopWindow = new CustomPopWindow.PopupWindowBuilder(MainActivity.this)
                            .setView(view)
                            .enableBackgroundDark(true)
                            .enableOutsideTouchableDissmiss(false)
                            .create()
                            .showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);
                } else {
                    showSnackBar(coordinatorLayout, "存储权限获取失败");
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);

        if (videoCallback == null) {
            boolean darkModeFollowSystem = prefs.getBoolean("dark_mode_follow_system", true);
            if (darkModeFollowSystem) {
                darkMode = isSystemDarkMode();
                changeDarkMode();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START) || drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawers();
        } else {
            if (fullVideoLayout.getVisibility() != View.VISIBLE) {
                if (suggestRecyclerView.getVisibility() == View.VISIBLE) {
                    toolbarEdit.clearFocus();
                    closeSuggestList();
                } else {
                    if (toolbarEdit.hasFocus()) {
                        toolbarEdit.clearFocus();
                    } else {
                        if (webViews.get(topPage).canGoBack()) {
                            webViews.get(topPage).goBack();
                        } else {
                            if (topPage >= 1) {
                                showActionSnackBar(coordinatorLayout, "是否关闭当前窗口", "关闭", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteWebPage(topPage);
                                    }
                                });
                            } else {
                                long nowTime = System.currentTimeMillis();
                                if ((nowTime - lastPressedTime) > 1800) {
                                    showSnackBar(coordinatorLayout, "重复操作将退出BIJOU");
                                    lastPressedTime = nowTime;
                                } else {
                                    ActivityCollector.finishAll();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 文件上传
     */
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case FILE_CHOOSER_RESULT_CODE:
                if (resultCode == Activity.RESULT_OK && uploadMessageAboveL != null) {
                    Uri[] results = null;
                    if (intent != null) {
                        String dataString = intent.getDataString();
                        ClipData clipData = intent.getClipData();
                        if (clipData != null) {
                            results = new Uri[clipData.getItemCount()];
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                results[i] = item.getUri();
                            }
                        }
                        if (dataString != null)
                            results = new Uri[]{Uri.parse(dataString)};
                    }
                    uploadMessageAboveL.onReceiveValue(results);
                    uploadMessageAboveL = null;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FILE_CHOOSER_RESULT_CODE:
                if (uploadMessage == null && uploadMessageAboveL == null) return;
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                if (uploadMessageAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data);
                } else if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(result);
                    uploadMessage = null;
                }
                break;
            case QR_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String qrResult = data.getStringExtra("result");
                    final QRContentDialog dialog = new QRContentDialog(this);
                    dialog.setData(qrResult, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            copyContent(dialog.qrContentEdit.getText().toString());
                            KeyboardUtil.closeKeyboard(MainActivity.this, dialog.qrContentEdit);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            webViews.get(topPage).loadUrl(seniorUrl(dialog.qrContentEdit.getText().toString()));
                            KeyboardUtil.closeKeyboard(MainActivity.this, dialog.qrContentEdit);
                            dialog.dismiss();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addNewPageForeground(seniorUrl(dialog.qrContentEdit.getText().toString()));
                            KeyboardUtil.closeKeyboard(MainActivity.this, dialog.qrContentEdit);
                            dialog.dismiss();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addNewPageBackstage(seniorUrl(dialog.qrContentEdit.getText().toString()));
                            KeyboardUtil.closeKeyboard(MainActivity.this, dialog.qrContentEdit);
                            dialog.dismiss();
                            showSnackBar(coordinatorLayout, "已后台打开");
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            KeyboardUtil.closeKeyboard(MainActivity.this, dialog.qrContentEdit);
                            dialog.dismiss();
                        }
                    });
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                } else {
                    showSnackBar(coordinatorLayout, "未识别到内容");
                }
                break;
            case BOOKMARK_RESQUEST_CODE:
                if (resultCode == Activity.RESULT_FIRST_USER && data != null && data.getBooleanExtra("add_home_quick", false)) {
                    homeQuicks = LitePal.findAll(HomeQuick.class);
                    syncHomeQuickList();
                }
            case HISTORY_RESQUEST_CODE:
                if (resultCode == Activity.RESULT_FIRST_USER && data != null && data.getStringExtra("url") != null) {
                    addNewPageForeground(seniorUrl(data.getStringExtra("url")));
                }
                break;
            case SETTINGS_RESQUEST_CODE:
                if (resultCode == Activity.RESULT_FIRST_USER) {
                    applySettings(data);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 应用设置
     */
    private void applySettings(Intent data) {
        if (prefs.getBoolean("java_script", true) != enabledJavaScript) {
            enabledJavaScript = prefs.getBoolean("java_script", true);
            changeJavaScriptStatus();
        }

        if (prefs.getBoolean("drop_reload", true) != enabledDropReload) {
            enabledDropReload = prefs.getBoolean("drop_reload", true);
            if (uaPC) {
                swipeRefreshLayout.setEnabled(false);
            } else {
                swipeRefreshLayout.setEnabled(enabledDropReload);
            }
        }

        enabledKeyCtrl = prefs.getBoolean("key_ctrl", false);

        if (prefs.getBoolean("recovery", false) != enabledRecovery) {
            enabledRecovery = prefs.getBoolean("recovery", false);
        }

        clearCacheExit = prefs.getBoolean("clear_cache_exit", false);
        clearFormDataExit = prefs.getBoolean("clear_form_data_exit", false);
        clearHistoryExit = prefs.getBoolean("clear_history_exit", false);

        try {
            if (data.getBooleanExtra("clear_cache", false)) {
                for (EnhancedWebView webView : webViews) {
                    webView.clearCache(true);
                }
            }

            if (data.getBooleanExtra("clear_form_data", false)) {
                for (EnhancedWebView webView : webViews) {
                    webView.clearFormData();
                }
            }

            if (data.getStringExtra("url") != null) {
                addNewPageForeground(seniorUrl(data.getStringExtra("url")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!prefs.getString("home_page", "about:blank").equals(homePage)) {
            if (webViews.get(topPage).getUrl().equals(homePage)) {
                webViews.get(topPage).loadUrl(seniorUrl(prefs.getString("home_page", "about:blank")));
            }
            homePage = prefs.getString("home_page", "about:blank");
        }

        engine = prefs.getString("engine", "https://www.baidu.com/s?wd=");

        if (prefs.getInt("toolbar_content", TITLE) != toolbarContent) {
            toolbarContent = prefs.getInt("toolbar_content", TITLE);
            setToolbarContentHint();
        }

        if (prefs.getBoolean("dark_mode", false) != darkMode) {
            darkMode = prefs.getBoolean("dark_mode", false);
            changeDarkMode();
        }
    }

    /**
     * 更改JavaScript状态
     */
    private void changeJavaScriptStatus() {
        for (EnhancedWebView webView : webViews) {
            webView.getSettings().setJavaScriptEnabled(enabledJavaScript);
            webView.reload();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (enabledKeyCtrl) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                webViews.get(topPage).pageUp(false);
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                webViews.get(topPage).pageDown(false);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        super.onPause();
        for (EnhancedWebView webView : webViews) {
            webView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (EnhancedWebView webView : webViews) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        destoryAllWebPage();
        super.onDestroy();
    }

    /**
     * 保存网页图片
     */
    @SuppressLint("StaticFieldLeak")
    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result;
            try {
                int idx = longClickUrl.lastIndexOf(".");
                String ext = longClickUrl.substring(idx);
                final String image = new Date().getTime() + ext + ".jpg";
                final File file = getExternalFilesDir(image);
                assert file != null;
                if (file.exists()) {
                    file.delete();
                } else {
                    file.mkdir();
                }
                InputStream inputStream = null;
                URL url = new URL(longClickUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len;
                FileOutputStream outStream = new FileOutputStream(file);
                assert inputStream != null;
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(),
                        file.getAbsolutePath(), image, null);
                MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                file.delete();
                outStream.flush();
                outStream.close();
                result = "图片将在片刻后保存至系统图库";
            } catch (Exception e) {
                result = "保存失败" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showSnackBar(coordinatorLayout, result);
                }
            });
        }
    }

    private static class TagUtils {

        private static String getTagByUrl(String url) {
            if (url.contains("qq")) {
                return "tvp_fullscreen_button";
            } else if (url.contains("youku")) {
                return "x-zoomin";
            } else if (url.contains("bilibili")) {
                return "icon-widescreen";
            } else if (url.contains("acfun")) {
                return "controller-btn-fullscreen";
            } else if (url.contains("le")) {
                return "hv_ico_screen";
            }
            return "";
        }

        static String getJs(String url) {
            String tag = getTagByUrl(url);
            if (TextUtils.isEmpty(tag)) {
                return "javascript:";
            } else {
                return "javascript:document.getElementsByClassName('"
                        + tag + "')[frontPage].addEventListener('click',function(){onClick.fullVedio();return false;});";
            }
        }
    }

    static class DragHomeQuickItemCallback extends ItemTouchHelper.Callback {

        private HomeQuickAdapter adapter;

        public DragHomeQuickItemCallback(HomeQuickAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlag;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                        | ItemTouchHelper.START | ItemTouchHelper.END;
            } else {
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlag, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            HomeQuick homeQuick1;
            HomeQuick homeQuick2;
            HomeQuick homeQuickTemp = new HomeQuick();

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    homeQuick1 = MainActivity.homeQuicks.get(i);
                    homeQuick2 = MainActivity.homeQuicks.get(i + 1);
                    homeQuickTemp.setTitle(homeQuick1.getTitle());
                    homeQuickTemp.setWebsite(homeQuick1.getWebsite());
                    homeQuick1.setTitle(homeQuick2.getTitle());
                    homeQuick1.setWebsite(homeQuick2.getWebsite());
                    homeQuick1.save();
                    homeQuick2.setTitle(homeQuickTemp.getTitle());
                    homeQuick2.setWebsite(homeQuickTemp.getWebsite());
                    homeQuick2.save();
                    Collections.swap(adapter.getHomeQuickList(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    homeQuick1 = MainActivity.homeQuicks.get(i);
                    homeQuick2 = MainActivity.homeQuicks.get(i - 1);
                    homeQuickTemp.setTitle(homeQuick1.getTitle());
                    homeQuickTemp.setWebsite(homeQuick1.getWebsite());
                    homeQuick1.setTitle(homeQuick2.getTitle());
                    homeQuick1.setWebsite(homeQuick2.getWebsite());
                    homeQuick1.save();
                    homeQuick2.setTitle(homeQuickTemp.getTitle());
                    homeQuick2.setWebsite(homeQuickTemp.getWebsite());
                    homeQuick2.save();
                    Collections.swap(adapter.getHomeQuickList(), i, i - 1);
                }
            }

            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder.itemView.setAlpha(0.5f);
            }
        }

        @Override
        public void clearView(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(1);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

    }

}
