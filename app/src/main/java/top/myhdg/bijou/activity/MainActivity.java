package top.myhdg.bijou.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Patterns;
import android.view.KeyEvent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gyf.immersionbar.ImmersionBar;
import com.kyleduo.switchbutton.SwitchButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import skin.support.SkinCompatManager;
import skin.support.design.widget.SkinMaterialCoordinatorLayout;
import top.myhdg.bijou.R;
import top.myhdg.bijou.bean.Suggest;
import top.myhdg.bijou.bean.SuggestAdapter;
import top.myhdg.bijou.bean.WebPage;
import top.myhdg.bijou.bean.WebPageAdapter;
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

    private EnhancedToolbar toolbar;//顶部工具栏
    private EditText toolbarEdit;//顶部地址栏
    private Button menuButton;//工具栏菜单按钮
    private Button goButton;//工具栏转到按钮
    private Button multipleButton;//工具栏多页面按钮
    private ProgressBar progressBar;//进度条

    private RecyclerView suggestRecyclerView;//搜索建议列表
    private ArrayList<Suggest> suggestList;//搜索建议列表
    private SuggestAdapter suggestAdapter;

    private static final String SUGGEST_API = "https://suggestion.baidu.com/su?p=3&cb=window.bdsug.sug&wd=";

    private NestedScrollView layoutHome;//主页
    private TextView startText;//主页搜索框

    private DrawerLayout drawerLayout;

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

    private Button qrButton;//扫描二维码按钮
    private Button addButton;//添加新页面按钮
    private Button emptyButton;//清空页面按钮
    private RecyclerView webPageRecycler;//页面列表
    private ArrayList<EnhancedWebView> webViews;//WebView列表
    private int topPage;//最上层WebView
    private ArrayList<WebPage> webPageList;//多窗口列表
    private WebPageAdapter webPageAdapter;

    private SkinMaterialCoordinatorLayout coordinatorLayout;

    private FloatingActionButton addBookmarkFab;//添加书签悬浮按钮
    private FloatingActionButton reloadFab;//重新加载悬浮按钮
    private FloatingActionButton goBackFab;//返回悬浮按钮
    private FloatingActionButton goForwardFab;//前进悬浮按钮
    private FloatingActionButton homeFab;//主页悬浮按钮
    private FloatingActionButton findFab;//查找悬浮按钮

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout webViewLayout;

    private boolean enabledJavaScript = true;//JavaScript是否开启
    private boolean enabledGeo = true;//定位权限是否开启

    private String homePage;//主页
    private String engine;//搜索引擎
    private int toolbarContent;//地址栏显示内容
    private static final int TITLE = 10;//网站标题
    private static final int WEBSITE = 11;//网址
    private static final int DOMAIN = 12;//域名

    private boolean customUA = false;//是否自定义UA
    private String customUserAgent;
    private static final String PC_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36 Edg/80.0.361.57";

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private static final int FILE_CHOOSER_RESULT_CODE = 10000;//文件上传请求码

    //权限请求码
    private static final int ACCESS_COARSE_LOCATION = 0;
    private static final int ACCESS_FINE_LOCATION = 1;

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

        customUA = prefs.getBoolean("custom_ua", false);
        customUserAgent = prefs.getString("custom_user_agent", "");

        getDarkModeCSS();

        initMultiple();

        initMenu();

        initToolbar();

        initFloatingButton();

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
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

        addNewPageForeground(homePage);
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
                deleteAllWebPage();
                drawerLayout.closeDrawers();
            }
        });

        webPageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
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
            }
        });

        uaPcSwitch.setChecked(uaPC);
        uaPcSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                uaPC = isChecked;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("ua_pc", uaPC);
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

            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                MainActivity.this.startActivity(intent);
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
                    showSnackBar(findViewById(R.id.nav_menu), "不支持主页导出");
                } else {
                    PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                    String jobName = getString(R.string.app_name) + " 导出网页：" + webViews.get(topPage).getTitle();
                    PrintDocumentAdapter printAdapter = webViews.get(topPage).createPrintDocumentAdapter(jobName);
                    assert printManager != null;
                    printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                }
            }
        });

        uaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        showSnackBar(findViewById(R.id.nav_menu), "暂未获取到网页源码");
                    }
                } else {
                    showSnackBar(findViewById(R.id.nav_menu), "暂时仅支持http网页及部分https网页查看源码");
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                ClipData mClipData = ClipData.newPlainText("Label", oneText.getText());
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                assert cm != null;
                cm.setPrimaryClip(mClipData);
                showSnackBar(findViewById(R.id.nav_menu), "一言已复制");
                return true;
            }
        });
    }

    /**
     * 初始化顶部工具栏
     */
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbarEdit = findViewById(R.id.toolbar_edit);
        menuButton = toolbar.getMenuButton();
        goButton = toolbar.getGoButton();
        multipleButton = toolbar.getMultipleButton();
        progressBar = findViewById(R.id.progress_bar);
        suggestRecyclerView = findViewById(R.id.suggest_recycler_iew);
        layoutHome = findViewById(R.id.layout_home);
        startText = findViewById(R.id.start_text);

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

        toolbarEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
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
                setSuggestList(toolbarEdit.getText().toString());
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
    }

    /**
     * 初始化悬浮按钮
     */
    private void initFloatingButton() {
        addBookmarkFab = findViewById(R.id.fab_add_bookmark);
        reloadFab = findViewById(R.id.fab_reload);
        goBackFab = findViewById(R.id.fab_go_back);
        goForwardFab = findViewById(R.id.fab_go_forward);
        homeFab = findViewById(R.id.fab_home);
        findFab = findViewById(R.id.fab_find);

        addBookmarkFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                webViews.get(topPage).loadUrl(homePage);
            }
        });

        findFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
    private void initWebView(EnhancedWebView webView, String url) {
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
            swipeRefreshLayout.setEnabled(true);
            if (customUA) {
                settings.setUserAgentString(customUserAgent);
            }
        }

        webView.requestFocusFromTouch();//支持获取手势焦点
        webView.setDrawingCacheEnabled(true);//开启绘制缓存
        webView.addJavascriptInterface(new JavascriptInterface(), "local_obj");

        //添加下载监听
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                showActionSnackBar(coordinatorLayout, "是否希望创建下载任务:  " + url, "下载", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
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

                        break;
                    case WebView.HitTestResult.IMAGE_TYPE: // 图片

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

                //注入深色模式CSS
                if (darkMode) {
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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //注入深色模式CSS
                if (darkMode) {
                    view.loadUrl("javascript:(function() {"
                            + "var parent = document.getElementsByTagName('head').item(0);"
                            + "var style = document.createElement('style');"
                            + "style.type = 'text/css';"
                            + "style.innerHTML = window.atob('"
                            + darkModeCSS + "');"
                            + "parent.appendChild(style)"
                            + "})();");
                }

                //获取html源码
                view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        };
        webView.setWebViewClient(webViewClient);

        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public boolean onCreateWindow(final WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                if (!isDialog) {
                    showActionSnackBar(coordinatorLayout, "是否同意该网站打开新的窗口", "同意", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WebView.HitTestResult result = view.getHitTestResult();
                            addNewPageForeground(result.getExtra());
                        }
                    });
                }
                return false;
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
                closeSuggestList();

                if (webViews.get(topPage) == view) {
                    if (newProgress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                        //注入深色模式CSS
                        if (darkMode) {
                            view.loadUrl("javascript:(function() {"
                                    + "var parent = document.getElementsByTagName('head').item(0);"
                                    + "var style = document.createElement('style');"
                                    + "style.type = 'text/css';"
                                    + "style.innerHTML = window.atob('"
                                    + darkModeCSS + "');"
                                    + "parent.appendChild(style)"
                                    + "})();");
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
                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, ACCESS_COARSE_LOCATION);
                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);
                callback.invoke(origin, enabledGeo, false);//第二个参数:是否同意网站获取权限  第三个参数：是否希望内核记住
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        };
        webView.setWebChromeClient(webChromeClient);

        webView.loadUrl(seniorUrl(url));
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
                swipeRefreshLayout.setEnabled(false);
            } else {
                swipeRefreshLayout.setEnabled(true);
                if (customUA) {
                    webView.getSettings().setUserAgentString(customUserAgent);
                } else {
                    webView.getSettings().setUserAgentString("");
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
                            return engine + originUrl;
                        }
                    } catch (Exception eh) {
                        return engine + originUrl;
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
                    toolbarEdit.setHint(webViews.get(topPage).getTitle());
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
        if (webViews.get(topPage).getUrl().equals("about:blank")) {
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

        if (topPage >= 9) {
            showSnackBar(coordinatorLayout, "新增过多页面可能会引起系统卡顿或其他未知问题,请谨慎添加");
        }
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

        if (webViews.size() >= 9) {
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
    }

    /**
     * 删除指定页面
     */
    private void deleteWebPage(int i) {
        EnhancedWebView webView = webViews.get(i);
        webViewLayout.removeView(webView);
        webView.loadUrl("about:blank");
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.destroy();
        webViews.remove(webView);

        if (i == topPage) {
            if (webViews.size() == 0) {
                addNewPageForeground(homePage);
            } else {
                topPage--;
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
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
        }
        webViews.clear();
        addNewPageForeground(homePage);
        syncWebPageList();
    }

    /**
     * 获取网页源码
     */
    private class JavascriptInterface {
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
                        showSnackBar(findViewById(R.id.nav_menu), "一言获取失败");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                            oneText.setText(jsonObject.getString("hitokoto"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
     * 切换全屏模式
     */
    private void changeFullScreen() {
        if (fullScreen) {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
            changeStatusColor();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("full_screen", fullScreen);
        editor.apply();
    }

    /**
     * 检查权限
     */
    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
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
        }

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);

        boolean darkModeFollowSystem = prefs.getBoolean("dark_mode_follow_system", true);
        if (darkModeFollowSystem) {
            darkMode = isSystemDarkMode();
            changeDarkMode();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START) || drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawers();
        } else {
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

    /**
     * 文件上传
     */
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
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
        }

        uploadMessageAboveL.onReceiveValue(results);

        uploadMessageAboveL = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (uploadMessage == null && uploadMessageAboveL == null) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
