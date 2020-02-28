package top.myhdg.bijou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import top.myhdg.bijou.R;

public class AboutActivity extends BaseActivity {

    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        resultIntent = new Intent();

        Button exitButton = findViewById(R.id.exit_about_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });

        TextView checkUpdateButton = findViewById(R.id.check_update_button);
        checkUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultIntent.putExtra("url", "https://www.coolapk.com/apk/top.myhdg.bijou");
                setResult(Activity.RESULT_FIRST_USER, resultIntent);
                AboutActivity.this.finish();
            }
        });

        LinearLayout bijou2OpenButton = findViewById(R.id.bijou_2_open_button);
        bijou2OpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultIntent.putExtra("url", "https://github.com/MyHdg0601/BIJOU");
                setResult(Activity.RESULT_FIRST_USER, resultIntent);
                AboutActivity.this.finish();
            }
        });

        LinearLayout bijou1OpenButton = findViewById(R.id.bijou_1_open_button);
        bijou1OpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultIntent.putExtra("url", "https://gitee.com/MyHdg/browser");
                setResult(Activity.RESULT_FIRST_USER, resultIntent);
                AboutActivity.this.finish();
            }
        });
    }

}
