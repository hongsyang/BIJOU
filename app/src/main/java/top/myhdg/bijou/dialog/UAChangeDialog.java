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
                uaEdit.setText("Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5 Quark/2.4.2.986");
            }
        });
        simpleUaButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                uaEdit.setText("Mozilla/5.0 (Linux; Android 8.0; MI 6 Build/OPR1.170623.027; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/48.0.2564.116 Mobile Safari/537.36 T7/10.3 SearchCraft/2.6.3 (Baidu; P1 8.0.0)");
            }
        });
        weUaButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                uaEdit.setText("Mozilla/5.0 (Linux; Android 6.0; NEM-AL10 Build/HONORNEM-AL10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/043906 Mobile Safari/537.36 MicroMessenger/6.6.1.1220(0x26060133) NetType/WIFI Language/zh_CN");
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
                uaEdit.setText("Mozilla/5.0 (Symbian/3; Series60/5.2 NokiaN8-00/012.002; Profile/MIDP-2.1 Configuration/CLDC-1.1 ) AppleWebKit/533.4 (KHTML, like Gecko) NokiaBrowser/7.3.0 Mobile Safari/533.4 3gpp-gba");
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
