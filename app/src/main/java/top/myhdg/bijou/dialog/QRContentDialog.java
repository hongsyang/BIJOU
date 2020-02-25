package top.myhdg.bijou.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import top.myhdg.bijou.R;

public class QRContentDialog extends Dialog {

    public EditText qrContentEdit;
    private TextView copyButton;
    private TextView currentPageButton;
    private TextView newPageForeButton;
    private TextView newPageBackButton;
    private TextView dismissButton;

    public QRContentDialog(@NonNull Context context) {
        super(context, R.style.Dialog);

        setContentView(R.layout.dialog_show_qr_content);

        qrContentEdit = findViewById(R.id.qr_content_edit);
        copyButton = findViewById(R.id.copy_content_button);
        currentPageButton = findViewById(R.id.current_page_button);
        newPageForeButton = findViewById(R.id.new_page_fore_button);
        newPageBackButton = findViewById(R.id.new_page_back_button);
        dismissButton = findViewById(R.id.dismiss_qr_content_button);
    }

    public void setData(String qrContent, View.OnClickListener copyClickListener, View.OnClickListener currentClickListener,
                        View.OnClickListener newForeClickListener, View.OnClickListener newBackClickListener,
                        View.OnClickListener dismissClickListener) {
        qrContentEdit.setText(qrContent);
        qrContentEdit.setSelection(qrContent.length());
        copyButton.setOnClickListener(copyClickListener);
        currentPageButton.setOnClickListener(currentClickListener);
        newPageForeButton.setOnClickListener(newForeClickListener);
        newPageBackButton.setOnClickListener(newBackClickListener);
        dismissButton.setOnClickListener(dismissClickListener);
    }

}
