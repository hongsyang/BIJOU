package top.myhdg.bijou.bean;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.myhdg.bijou.R;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {

    private List<DownloadItem> downloadItemList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView fileNameText;
        TextView speedText;
        ProgressBar progressBar;
        TextView fileSizeText;
        TextView stateText;
        Button pauseButton;
        Button cancelButton;
        LinearLayout downloadItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileNameText = itemView.findViewById(R.id.dl_file_name_text);
            speedText = itemView.findViewById(R.id.dl_speed_text);
            progressBar = itemView.findViewById(R.id.dl_progress_bar);
            fileSizeText = itemView.findViewById(R.id.dl_file_size_text);
            stateText = itemView.findViewById(R.id.dl_state_text);
            pauseButton = itemView.findViewById(R.id.pause_download_button);
            cancelButton = itemView.findViewById(R.id.cancel_download_button);
            downloadItem = itemView.findViewById(R.id.download_item);
        }

    }

    public DownloadAdapter(List<DownloadItem> downloadItemList) {
        this.downloadItemList = downloadItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownloadItem downloadItem = downloadItemList.get(position);
        holder.fileNameText.setText(downloadItem.getFileName());
        holder.speedText.setText(downloadItem.getSpeed());
        holder.progressBar.setProgress(downloadItem.getPercent());
        holder.fileSizeText.setText(downloadItem.getCurrentProgress() + "b / " + downloadItem.getFileSize());
        holder.stateText.setText(downloadItem.getPercent() + "%  " + getStateString(downloadItem.getState()));
        holder.downloadItem.setOnClickListener(downloadItem.getOpenClickListener());
        holder.pauseButton.setOnClickListener(downloadItem.getPauseClickListener());
        holder.pauseButton.setBackground(downloadItem.getPauseIcon());
        holder.cancelButton.setOnClickListener(downloadItem.getCancelClickListener());
    }

    @Override
    public int getItemCount() {
        return downloadItemList.size();
    }

    private String getStateString(int state) {
        String stateStr = "";
        switch (state) {
            case 0:
                stateStr = "失败";
                break;
            case 1:
                stateStr = "完成";
                break;
            case 2:
                stateStr = "停止";
                break;
            case 3:
                stateStr = "等待";
                break;
            case 4:
                stateStr = "正在下载";
                break;
            case 5:
                stateStr = "预处理";
                break;
            case 6:
                stateStr = "预处理完成";
                break;
            case 7:
                stateStr = "取消";
                break;
        }
        return stateStr;
    }

}
