package top.myhdg.bijou.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import top.myhdg.bijou.R;

public class SupportActivity extends BaseActivity {

    private LinearLayout supportLayout;
    private LinearLayout qqButton;
    private LinearLayout qqGroupButton;
    private LinearLayout subscriptionButton;
    private LinearLayout alipayButton;

    private static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
    private static final String INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&" +
            "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s" +
            "%3Dweb-other&_t=1472443966571#Intent;" +
            "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_support);

        Button exitButton = findViewById(R.id.exit_support_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportActivity.this.finish();
            }
        });

        supportLayout = findViewById(R.id.support_layout);
        qqButton = findViewById(R.id.qq_button);
        qqGroupButton = findViewById(R.id.qq_group_button);
        subscriptionButton = findViewById(R.id.subscription_button);
        alipayButton = findViewById(R.id.alipay_button);

        qqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("url", "http://wpa.qq.com/msgrd?v=3&uin=1316740440&site=qq&menu=yes");
                setResult(Activity.RESULT_FIRST_USER, intent);
                SupportActivity.this.finish();
            }
        });

        qqGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar(supportLayout, "QQ群暂未创建");
            }
        });

        subscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar(supportLayout, "微信公众号暂未创建");
            }
        });

        alipayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasClient(SupportActivity.this, ALIPAY_PACKAGE_NAME)) {
                    try {
                        Intent intent = Intent.parseUri(
                                INTENT_URL_FORMAT.replace("{urlCode}", "fkx110726in1l4woe1t36c6"),
                                Intent.URI_INTENT_SCHEME
                        );
                        SupportActivity.this.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showSnackBar(supportLayout, "请先安装支付宝再支持作者吧");
                }
            }
        });
    }

    /**
     * 获取SnackBar背景颜色
     */
    private int getSnackBarBackgroundColor() {
        SharedPreferences prefs = getSharedPreferences("BIJOU", MODE_PRIVATE);
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
     * 检测APP是否安装
     */
    private boolean hasClient(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
