package top.myhdg.bijou.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.task.DownloadTask;
import com.example.zhouwei.library.CustomPopWindow;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.myhdg.bijou.R;
import top.myhdg.bijou.bean.DownloadAdapter;
import top.myhdg.bijou.bean.DownloadItem;
import top.myhdg.bijou.util.KeyboardUtil;

public class DownloadActivity extends BaseActivity {

    private RecyclerView downloadRecyclerView;
    private List<DownloadEntity> downloads;
    private List<DownloadItem> downloadItemList;
    private DownloadAdapter adapter;
    private CustomPopWindow addDownloadPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download);

        Button exitButton = findViewById(R.id.exit_download_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadActivity.this.finish();
            }
        });

        Aria.download(this).register();

        downloadRecyclerView = findViewById(R.id.download_recycler_view);
        downloadRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        downloadItemList = new ArrayList<>();
        adapter = new DownloadAdapter(downloadItemList);
        downloadRecyclerView.setAdapter(adapter);
        syncDownloadList();

        FloatingActionButton fabAddDownload = findViewById(R.id.fab_add_download);
        fabAddDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DownloadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DownloadActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    @SuppressLint("InflateParams") View view = LayoutInflater.from(DownloadActivity.this).inflate(R.layout.add_download_pop_window, null);
                    initAddDownloadPopWindow(view);
                    addDownloadPopWindow = new CustomPopWindow.PopupWindowBuilder(DownloadActivity.this)
                            .setView(view)
                            .enableBackgroundDark(true)
                            .enableOutsideTouchableDissmiss(false)
                            .create()
                            .showAtLocation(findViewById(R.id.download_layout), Gravity.CENTER, 0, 0);
                }
            }
        });
    }

    /**
     * 初始化下载弹窗
     */
    private void initAddDownloadPopWindow(View view) {
        final EditText fileNameEdit = view.findViewById(R.id.file_name_edit);
        final EditText downloadUrlEdit = view.findViewById(R.id.dl_url_edit);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_dl_button:
                        if (!fileNameEdit.getText().toString().equals("") && !downloadUrlEdit.getText().toString().equals("")) {
                            Aria.download(DownloadActivity.this)
                                    .load(downloadUrlEdit.getText().toString())
                                    .setFilePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                                            + "/BIJOU/" + fileNameEdit.getText().toString())
                                    .ignoreFilePathOccupy()
                                    .create();
                            KeyboardUtil.closeKeyboard(DownloadActivity.this, fileNameEdit);
                            KeyboardUtil.closeKeyboard(DownloadActivity.this, downloadUrlEdit);
                            syncDownloadList();
                            addDownloadPopWindow.dissmiss();
                            showSnackBar(downloadRecyclerView, "若无异常，下载任务将在片刻后创建");
                        }
                        break;
                    case R.id.cancel_dl_button:
                        addDownloadPopWindow.dissmiss();
                        break;
                }
            }
        };

        view.findViewById(R.id.file_name_notice_text).setVisibility(View.GONE);
        TextView downloadButton = view.findViewById(R.id.add_dl_button);
        downloadButton.setOnClickListener(clickListener);
        TextView cancelButton = view.findViewById(R.id.cancel_dl_button);
        cancelButton.setOnClickListener(clickListener);
    }

    /**
     * 同步下载列表
     */
    private void syncDownloadList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloads = Aria.download(this).getTaskList();
                downloadItemList.clear();
                if (downloads != null) {
                    for (DownloadEntity downloadEntity : downloads) {
                        DownloadItem downloadItem = new DownloadItem();
                        final long taskId = downloadEntity.getId();
                        downloadItem.setId(taskId);
                        downloadItem.setFileName(downloadEntity.getFileName());
                        if (downloadEntity.getState() == 4) {
                            downloadItem.setSpeed(downloadEntity.getConvertSpeed());
                        } else {
                            downloadItem.setSpeed("");
                        }
                        downloadItem.setFileSize(downloadEntity.getConvertFileSize());
                        downloadItem.setCurrentProgress(downloadEntity.getCurrentProgress());
                        downloadItem.setPercent(downloadEntity.getPercent());
                        downloadItem.setState(downloadEntity.getState());
                        downloadItem.setPauseIcon(getPauseOrStartButtonIcon(taskId));
                        downloadItem.setOpenClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openFile(taskId);
                            }
                        });
                        downloadItem.setPauseClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pauseOrStartDownload(taskId);
                            }
                        });
                        downloadItem.setCancelClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDownload(taskId);
                            }
                        });
                        downloadItemList.add(downloadItem);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    /**
     * 根据类型打开文件
     */
    private void openFile(long taskId) {
        DownloadEntity downloadEntity = Aria.download(DownloadActivity.this).load(taskId).getEntity();
        if (downloadEntity.getState() == 1) {
            String path = downloadEntity.getFilePath();
            String ext = path.substring(path.lastIndexOf(".") + 1);
            switch (ext) {
                case "apk":
                    openFileByOther(path, "application/vnd.android.package-archive");
                    break;
                case "mp4":
                case "wmv":
                case "3gp":
                case "avi":
                case "flv":
                case "mkv":
                case "mov":
                case "mpg":
                case "rmvb":
                case "swf":
                case "vob":
                case "rtsp":
                    openFileByOther(path, "video/*");
                    break;
                case "mp3":
                case "aac":
                case "m4a":
                case "amr":
                case "wav":
                case "flac":
                case "gsm":
                case "ogg":
                case "webm":
                case "qmcflac":
                case "qmctomp3":
                case "ape":
                    openFileByOther(path, "audio/*");
                    break;
                case "txt":
                case "pdf":
                case "html":
                case "htm":
                case "epub":
                case "lrc":
                case "qrc":
                    openFileByOther(path, "text/*");
                    break;
                case "jpg":
                case "jpeg":
                case "png":
                case "bmp":
                case "tif":
                case "gif":
                case "wepb":
                case "heif":
                    Intent intent = new Intent(DownloadActivity.this, ImageActivity.class);
                    intent.putExtra("url", path);
                    startActivity(intent);
                    break;
            }
        }
    }

    /**
     * 通过其他应用打开文件
     */
    private void openFileByOther(String filePath, String type) {
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(DownloadActivity.this, "top.myhdg.bijou.FileProvider", apkfile);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), type);
        }
        startActivity(intent);
    }

    /**
     * 暂停或开始下载
     */
    private void pauseOrStartDownload(long taskId) {
        DownloadEntity downloadEntity = Aria.download(DownloadActivity.this).load(taskId).getEntity();
        switch (downloadEntity.getState()) {
            case -1:
            case 0:
            case 2:
                Aria.download(DownloadActivity.this).load(taskId).resume();
                break;
            case 3:
            case 4:
            case 5:
            case 6:
                Aria.download(DownloadActivity.this).load(taskId).stop();
                break;
        }
    }

    /**
     * 取消下载
     */
    private void cancelDownload(final long taskId) {
        showActionSnackBar(downloadRecyclerView, "确认删除下载任务吗？(同时会删除源文件)", "删除", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Aria.download(DownloadActivity.this).load(taskId).stop();
                Aria.download(DownloadActivity.this).load(taskId).cancel(true);
                for (DownloadItem downloadItem : downloadItemList) {
                    if (downloadItem.getId() == taskId) {
                        downloadItemList.remove(downloadItem);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
    }

    @Download.onPre
    void onPre(DownloadTask task) {
        if (task != null && task.getEntity() != null) {
            updateDownloadInfo(task.getEntity());
        }
    }

    @Download.onTaskStart
    void taskStart(DownloadTask task) {
        if (task != null && task.getEntity() != null) {
            updateDownloadInfo(task.getEntity());
        }
    }

    @Download.onTaskResume
    void taskResume(DownloadTask task) {
        if (task != null && task.getEntity() != null) {
            updateDownloadInfo(task.getEntity());
        }
    }

    @Download.onTaskRunning
    void running(DownloadTask task) {
        if (task != null && task.getEntity() != null) {
            updateDownloadInfo(task.getEntity());
        }
    }

    @Download.onWait
    void onWait(DownloadTask task) {
        if (task != null && task.getEntity() != null) {
            updateDownloadInfo(task.getEntity());
        }
    }

    @Download.onTaskStop
    void taskStop(DownloadTask task) {
        if (task != null && task.getEntity() != null) {
            updateDownloadInfo(task.getEntity());
        }
    }

    @Download.onTaskCancel
    void taskCancel(DownloadTask task) {
        if (task != null && task.getEntity() != null) {
            updateDownloadInfo(task.getEntity());
        }
    }

    @Download.onTaskFail
    void taskFail(DownloadTask task) {
        if (task != null && task.getEntity() != null) {
            updateDownloadInfo(task.getEntity());
        }
    }

    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        if (task != null && task.getEntity() != null) {
            updateDownloadInfo(task.getEntity());
        }
    }

    /**
     * 更新下载信息
     */
    private void updateDownloadInfo(DownloadEntity downloadEntity) {
        for (DownloadItem downloadItem : downloadItemList) {
            if (downloadItem.getId() == downloadEntity.getId()) {
                downloadItem.setFileName(downloadEntity.getFileName());
                if (downloadEntity.getState() == 4) {
                    downloadItem.setSpeed(downloadEntity.getConvertSpeed());
                } else {
                    downloadItem.setSpeed("");
                }
                downloadItem.setFileSize(downloadEntity.getConvertFileSize());
                downloadItem.setCurrentProgress(downloadEntity.getCurrentProgress());
                downloadItem.setPercent(downloadEntity.getPercent());
                downloadItem.setState(downloadEntity.getState());
                downloadItem.setPauseIcon(getPauseOrStartButtonIcon(downloadEntity.getId()));
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 获取按钮暂停或继续图标
     */
    private Drawable getPauseOrStartButtonIcon(long taskId) {
        SharedPreferences prefs = getSharedPreferences("BIJOU", MODE_PRIVATE);
        if (prefs.getBoolean("dark_mode", false)) {
            if (Aria.download(DownloadActivity.this).load(taskId).isRunning()) {
                return ResourcesCompat.getDrawable(getResources(), R.drawable.pause_dark, null);
            } else {
                return ResourcesCompat.getDrawable(getResources(), R.drawable.start_dark, null);
            }
        } else {
            if (Aria.download(DownloadActivity.this).load(taskId).isRunning()) {
                return ResourcesCompat.getDrawable(getResources(), R.drawable.pause, null);
            } else {
                return ResourcesCompat.getDrawable(getResources(), R.drawable.start, null);
            }
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                @SuppressLint("InflateParams") View view = LayoutInflater.from(DownloadActivity.this).inflate(R.layout.add_download_pop_window, null);
                initAddDownloadPopWindow(view);
                addDownloadPopWindow = new CustomPopWindow.PopupWindowBuilder(DownloadActivity.this)
                        .setView(view)
                        .enableBackgroundDark(true)
                        .enableOutsideTouchableDissmiss(false)
                        .create()
                        .showAtLocation(findViewById(R.id.download_layout), Gravity.CENTER, 0, 0);
            } else {
                showSnackBar(downloadRecyclerView, "存储权限获取失败");
            }
        }
    }

}
