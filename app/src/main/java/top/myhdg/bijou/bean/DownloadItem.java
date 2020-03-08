package top.myhdg.bijou.bean;

import android.graphics.drawable.Drawable;
import android.view.View;

public class DownloadItem {

    private long id;
    private String fileName;
    private String speed;
    private String fileSize;
    private long currentProgress;
    private int percent;
    private int state;
    private Drawable pauseIcon;
    private View.OnClickListener openClickListener;
    private View.OnClickListener pauseClickListener;
    private View.OnClickListener cancelClickListener;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public long getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(long currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Drawable getPauseIcon() {
        return pauseIcon;
    }

    public void setPauseIcon(Drawable pauseIcon) {
        this.pauseIcon = pauseIcon;
    }

    public View.OnClickListener getOpenClickListener() {
        return openClickListener;
    }

    public void setOpenClickListener(View.OnClickListener openClickListener) {
        this.openClickListener = openClickListener;
    }

    public View.OnClickListener getPauseClickListener() {
        return pauseClickListener;
    }

    public void setPauseClickListener(View.OnClickListener pauseClickListener) {
        this.pauseClickListener = pauseClickListener;
    }

    public View.OnClickListener getCancelClickListener() {
        return cancelClickListener;
    }

    public void setCancelClickListener(View.OnClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

}
