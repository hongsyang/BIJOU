package top.myhdg.bijou.bean;

import android.view.View;

public class WebPage {

    private int webPageId;
    private String webPageTitle;
    private String webPageSite;
    private View.OnClickListener deleteClickListener;
    private View.OnClickListener webPageItemClickListener;

    public int getWebPageId() {
        return webPageId;
    }

    public void setWebPageId(int webPageId) {
        this.webPageId = webPageId;
    }

    public String getWebPageTitle() {
        return webPageTitle;
    }

    public void setWebPageTitle(String webPageTitle) {
        this.webPageTitle = webPageTitle;
    }

    public String getWebPageSite() {
        return webPageSite;
    }

    public void setWebPageSite(String webPageSite) {
        this.webPageSite = webPageSite;
    }

    public View.OnClickListener getDeleteClickListener() {
        return deleteClickListener;
    }

    public void setDeleteClickListener(View.OnClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public View.OnClickListener getWebPageItemClickListener() {
        return webPageItemClickListener;
    }

    public void setWebPageItemClickListener(View.OnClickListener webPageItemClickListener) {
        this.webPageItemClickListener = webPageItemClickListener;
    }

}
