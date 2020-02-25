package top.myhdg.bijou.bean;

import org.litepal.crud.LitePalSupport;

public class Bookmark extends LitePalSupport {

    private int id;
    private String title;
    private String website;

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

}
