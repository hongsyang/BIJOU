package top.myhdg.bijou.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import top.myhdg.bijou.R;

public class UAChangeDialog extends Dialog {

    public EditText uaEdit;
    private TextView applyButton;
    private TextView resetButton;
    private TextView cancelButton;

    private TextView kuaUaButton;
    private TextView simpleUaButton;
    private TextView weUaButton;
    private TextView ieUaButton;
    private TextView symbainUaButton;

    public UAChangeDialog(@NonNull Context context) {
        super(context, R.style.Dialog);

        setContentView(R.layout.dialog_change_ua);

        uaEdit = findViewById(R.id.ua_edit);
        applyButton = findViewById(R.id.apply_ua_button);
        resetButton = findViewById(R.id.reset_ua_button);
        cancelButton = findViewById(R.id.dismiss_change_ua_button);
        kuaUaButton = findViewById(R.id.kua_ua_button);
        simpleUaButton = findViewById(R.id.simple_ua_button);
        weUaButton = findViewById(R.id.we_ua_button);
        ieUaButton = findViewById(R.id.ie_ua_button);
        symbainUaButton = findViewById(R.id.symbian_ua_button);

        kuaUaButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                uaEdit.setText("Mozilla/5.0 (Linux; U; Android 10; zh-CN;) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.108 Quark/3.8.4.128 Mobile Safari/537.36");
            }
        });
        simpleUaButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                uaEdit.setText("Mozilla/5.0 (Linux; Android 10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/80.0.3987.87 Mobile Safari/537.36 SearchCraft/3.6.4 (Baidu; P1 10)");
            }
        });
        weUaButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                uaEdit.setText("Mozilla/5.0 (Linux; Android 10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045118 Mobile Safari/537.36 MMWEBID/5670 MicroMessenger/7.0.11.1600(0x27000B32) Process/tools  Language/zh_CN ABI/arm64");
            }
        });
        ieUaButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                uaEdit.setText("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko");
            }
        });
        symbainUaButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                uaEdit.setText("Nokia5230/04.13 (SymbianOS/9.3; U; Series60/3.2 Mozilla/5.0; Profile/MIDP-2.1 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413");
            }
        });
    }

    public void setData(String customUa, View.OnClickListener applyClickListener,
                        View.OnClickListener resetClickListener, View.OnClickListener cancelClickListener) {
        uaEdit.setText(customUa);
        uaEdit.setSelectAllOnFocus(true);
        applyButton.setOnClickListener(applyClickListener);
        resetButton.setOnClickListener(resetClickListener);
        cancelButton.setOnClickListener(cancelClickListener);
    }

}
