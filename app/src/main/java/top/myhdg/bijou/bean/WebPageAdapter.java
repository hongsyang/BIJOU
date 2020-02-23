package top.myhdg.bijou.bean;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.myhdg.bijou.R;

public class WebPageAdapter extends RecyclerView.Adapter<WebPageAdapter.ViewHolder> {

    private List<WebPage> webPageList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView webPageIdText;
        TextView webPageTitleText;
        TextView webPageSiteText;
        Button deleteWebPageButton;
        RelativeLayout webPageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            webPageIdText = itemView.findViewById(R.id.web_page_id_text);
            webPageTitleText = itemView.findViewById(R.id.web_page_title_text);
            webPageSiteText = itemView.findViewById(R.id.web_page_site_text);
            deleteWebPageButton = itemView.findViewById(R.id.web_page_delete_button);
            webPageItem = itemView.findViewById(R.id.web_page_item);
        }

    }

    public WebPageAdapter(List<WebPage> webPageList) {
        this.webPageList = webPageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.web_page_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WebPage webPage = webPageList.get(position);
        try {
            holder.webPageIdText.setText(String.valueOf(webPage.getWebPageId() + 1));
            String webTitle = webPage.getWebPageTitle();
            if (webTitle.equals("about:blank")) {
                holder.webPageTitleText.setText("BIJOU主页");
                holder.webPageSiteText.setText("myhdg.top.bijou");
            } else {
                holder.webPageTitleText.setText(webTitle);
                holder.webPageSiteText.setText(webPage.getWebPageSite().split("/")[2]);
            }
            holder.deleteWebPageButton.setOnClickListener(webPage.getDeleteClickListener());
            holder.webPageItem.setOnClickListener(webPage.getWebPageItemClickListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return webPageList.size();
    }

}
