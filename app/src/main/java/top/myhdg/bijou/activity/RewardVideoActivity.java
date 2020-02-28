package top.myhdg.bijou.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;

import java.util.Map;

import top.myhdg.bijou.R;

public class RewardVideoActivity extends BaseActivity implements RewardVideoADListener {

    private static final String TAG = RewardVideoActivity.class.getSimpleName();
    private RewardVideoAD rewardVideoAD;
    private boolean adLoaded;//广告加载成功标志
    private boolean videoCached;//视频素材文件下载完成标志

    private static final String APP_ID = "1110206395";
    private static final String POS_ID = "1051103282632371";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reward_video);

        Button exitButton = findViewById(R.id.exit_ad_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewardVideoActivity.this.finish();
            }
        });

        initAD();
    }

    private void initAD() {
        // 1. 初始化激励视频广告
        rewardVideoAD = new RewardVideoAD(this, APP_ID, POS_ID, this, false);
        adLoaded = false;
        videoCached = false;
        // 2. 加载激励视频广告
        rewardVideoAD.loadAD();
    }

    private void showAD() {
        // 3. 展示激励视频广告
        if (adLoaded && rewardVideoAD != null) {
            //广告展示检查1：广告成功加载，此处也可以使用videoCached来实现视频预加载完成后再展示激励视频广告的逻辑
            if (!rewardVideoAD.hasShown()) {
                //广告展示检查2：当前广告数据还没有展示过
                long delta = 1000;//建议给广告过期时间加个buffer，单位ms，这里demo采用1000ms的buffer
                //广告展示检查3：展示广告前判断广告数据未过期
                if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
                    rewardVideoAD.showAD(RewardVideoActivity.this);
                }
            }
        }
    }

    /**
     * 广告加载成功，可在此回调后进行广告展示
     **/
    @Override
    public void onADLoad() {
        adLoaded = true;

        Log.d(TAG, "eCPM = " + rewardVideoAD.getECPM() + " , eCPMLevel = " + rewardVideoAD.getECPMLevel());

        showAD();
    }

    /**
     * 视频素材缓存成功，可在此回调后进行广告展示
     */
    @Override
    public void onVideoCached() {
        videoCached = true;

        Log.i(TAG, "视频素材缓存成功");
    }

    /**
     * 激励视频广告页面展示
     */
    @Override
    public void onADShow() {
        Log.i(TAG, "激励视频广告页面展示");
    }

    /**
     * 激励视频广告曝光
     */
    @Override
    public void onADExpose() {
        Log.i(TAG, "激励视频广告曝光");
    }

    /**
     * 激励视频触发激励（观看视频大于一定时长或者视频播放完毕）
     */
    @Override
    public void onReward() {
        Log.i(TAG, "激励视频触发激励");

        Toast.makeText(this, "广告观看完毕，感谢您的支持", Toast.LENGTH_LONG).show();
        RewardVideoActivity.this.finish();
    }

    /**
     * 激励视频广告被点击
     */
    @Override
    public void onADClick() {
        Map<String, String> map = rewardVideoAD.getExts();
        String clickUrl = map.get("clickUrl");
        Log.i(TAG, "激励视频广告被点击 clickUrl: " + clickUrl);

        RewardVideoActivity.this.finish();
    }

    /**
     * 激励视频播放完毕
     */
    @Override
    public void onVideoComplete() {
        Log.i(TAG, "激励视频播放完毕");

        RewardVideoActivity.this.finish();
    }

    /**
     * 激励视频广告被关闭
     */
    @Override
    public void onADClose() {
        Log.i(TAG, "激励视频广告被关闭");

        RewardVideoActivity.this.finish();
    }

    /**
     * 广告流程出错
     */
    @Override
    public void onError(AdError adError) {
        Toast.makeText(this, "广告加载出错：" + adError.getErrorCode() + "\n" + adError.getErrorMsg(), Toast.LENGTH_LONG).show();

        RewardVideoActivity.this.finish();
    }

}
