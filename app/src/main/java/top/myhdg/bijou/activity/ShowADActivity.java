package top.myhdg.bijou.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.appx.BDInterstitialAd;

import java.util.ArrayList;

import top.myhdg.bijou.R;

public class ShowADActivity extends BaseActivity {

    private BDInterstitialAd bdInterstitialAd;
    private BDInterstitialAd.InterstitialAdListener adListener;

    private static final String APP_KEY = "rGygF66DB7WucxyWzdLxWGDybRP2wmjM";
    private static final String AD_ID = "ntuHx5sTGGniFdR0eubEH76c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_ad);

        Button exitButton = findViewById(R.id.exit_ad_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowADActivity.this.finish();
            }
        });

        adListener = new BDInterstitialAd.InterstitialAdListener() {
            @Override
            public void onAdvertisementViewDidHide() {

            }

            @Override
            public void onAdvertisementDataDidLoadSuccess() {
                if (bdInterstitialAd.isLoaded()) {
                    bdInterstitialAd.showAd();
                }
            }

            @Override
            public void onAdvertisementDataDidLoadFailure() {
                Toast.makeText(ShowADActivity.this, "广告加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdvertisementViewDidShow() {

            }

            @Override
            public void onAdvertisementViewDidClick() {

            }

            @Override
            public void onAdvertisementViewWillStartNewIntent() {

            }
        };

        ArrayList<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(ShowADActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(ShowADActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(ShowADActivity.this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(ShowADActivity.this, permissions.toArray(new String[0]), 1);
        } else {
            bdInterstitialAd = new BDInterstitialAd(this, APP_KEY, AD_ID);
            bdInterstitialAd.setAdListener(adListener);
            if (!bdInterstitialAd.isLoaded()) {
                bdInterstitialAd.loadAd();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (bdInterstitialAd != null) {
            bdInterstitialAd.destroy();
            bdInterstitialAd = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bdInterstitialAd = new BDInterstitialAd(this, APP_KEY, AD_ID);
                bdInterstitialAd.setAdListener(adListener);
                if (!bdInterstitialAd.isLoaded()) {
                    bdInterstitialAd.loadAd();
                }
            } else {
                Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
