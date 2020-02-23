package top.myhdg.bijou.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.annotation.Nullable;

import top.myhdg.bijou.R;

public class EnhancedToolbar extends skin.support.widget.SkinCompatToolbar {

    private Button menuButton;
    private Button goButton;
    private Button multipleButton;

    public EnhancedToolbar(Context context) {
        super(context);
    }

    public EnhancedToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EnhancedToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        menuButton = findViewById(R.id.menu_button);
        goButton = findViewById(R.id.go_button);
        multipleButton = findViewById(R.id.multiple_button);
    }

    public Button getMenuButton() {
        return menuButton;
    }

    public Button getGoButton() {
        return goButton;
    }

    public Button getMultipleButton() {
        return multipleButton;
    }

}
