package top.myhdg.bijou.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import top.myhdg.bijou.R;

public class ImageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);

        Button exitButton = findViewById(R.id.exit_image_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageActivity.this.finish();
            }
        });

        PhotoView photoView = findViewById(R.id.photo_view);
        photoView.enable();
        Glide.with(this).load(getIntent().getStringExtra("url")).into(photoView);
    }

}
