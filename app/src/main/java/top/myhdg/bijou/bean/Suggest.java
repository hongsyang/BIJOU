package top.myhdg.bijou.bean;

import android.view.View;

public class Suggest {

    private String suggestText;
    private View.OnClickListener suggestItemClickListener;

    public String getSuggestText() {
        return suggestText;
    }

    public void setSuggestText(String suggestText) {
        this.suggestText = suggestText;
    }

    public View.OnClickListener getSuggestItemClickListener() {
        return suggestItemClickListener;
    }

    public void setSuggestItemClickListener(View.OnClickListener suggestItemClickListener) {
        this.suggestItemClickListener = suggestItemClickListener;
    }

}
