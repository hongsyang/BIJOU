package top.myhdg.bijou.bean;

import android.view.View;

public class HomeQuickItem {

    private int id;
    private String title;
    private String website;
    private View.OnClickListener openClickListener;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public View.OnClickListener getOpenClickListener() {
        return openClickListener;
    }

    public void setOpenClickListener(View.OnClickListener openClickListener) {
        this.openClickListener = openClickListener;
    }

}
