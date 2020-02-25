package top.myhdg.bijou.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import top.myhdg.bijou.R;
import top.myhdg.bijou.bean.History;
import top.myhdg.bijou.bean.HistoryAdapter;
import top.myhdg.bijou.bean.HistoryItem;

public class HistoryActivity extends BaseActivity {

    private RecyclerView historyRecyclerView;
    private List<History> histories;
    private List<HistoryItem> historyList;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        Button exitButton = findViewById(R.id.exit_history_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryActivity.this.finish();
            }
        });

        historyRecyclerView = findViewById(R.id.history_recycler_view);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        histories = LitePal.findAll(History.class);
        adapter = new HistoryAdapter(historyList);
        historyRecyclerView.setAdapter(adapter);
        syncHistoryList();

        FloatingActionButton fabEmptyHistory = findViewById(R.id.fab_empty);
        fabEmptyHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionSnackBar(historyRecyclerView, "确认清空历史记录？(此操作不可逆)", "清空", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LitePal.deleteAll(History.class);
                        histories.clear();
                        syncHistoryList();
                        showSnackBar(historyRecyclerView, "历史记录已清空");
                    }
                });
            }
        });
    }

    /**
     * 同步历史记录列表
     */
    private void syncHistoryList() {
        historyList.clear();
        for (final History history : histories) {
            HistoryItem historyItem = new HistoryItem();
            final long id = history.getId();
            historyItem.setId(history.getId());
            historyItem.setTitle(history.getTitle());
            historyItem.setWebsite(history.getWebsite());
            historyItem.setTime(history.getTime());
            historyItem.setDeleteClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LitePal.delete(History.class, id);
                    histories.remove(history);
                    syncHistoryList();
                }
            });
            historyItem.setOpenClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent();
                    data.putExtra("url", history.getWebsite());
                    setResult(Activity.RESULT_FIRST_USER, data);
                    HistoryActivity.this.finish();
                }
            });
            historyList.add(historyItem);
        }
        adapter.notifyDataSetChanged();
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
     * 显示带Action的SnackBar
     */
    private void showActionSnackBar(View view, String content, String action, View.OnClickListener clickListener) {
        Snackbar.make(view, content, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getSnackBarBackgroundColor())
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setAction(action, clickListener)
                .setActionTextColor(Color.parseColor("#ffffff"))
                .show();
    }

}
