package top.myhdg.bijou.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import top.myhdg.bijou.R;

import static top.myhdg.bijou.activity.MainActivity.htmlSource;

public class SourceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_source);

        Button exitButton = findViewById(R.id.exit_source_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourceActivity.this.finish();
            }
        });

        EditText sourceEdit = findViewById(R.id.source_edit);

        sourceEdit.setText(htmlSource);
    }

}
