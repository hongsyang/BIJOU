package top.myhdg.bijou.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import top.myhdg.bijou.EnhancedApplication;
import top.myhdg.bijou.R;

public class HomeQuickAdapter extends RecyclerView.Adapter<HomeQuickAdapter.ViewHolder> {

    private List<HomeQuickItem> homeQuickList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout homeQuickItem;
        CircleImageView homeQuickIcon;
        TextView homeQuickTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            homeQuickItem = itemView.findViewById(R.id.home_quick_item);
            homeQuickIcon = itemView.findViewById(R.id.home_quick_icon);
            homeQuickTitle = itemView.findViewById(R.id.home_quick_title);
        }

    }

    public HomeQuickAdapter(List<HomeQuickItem> homeQuickList) {
        this.homeQuickList = homeQuickList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_quick_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            HomeQuickItem homeQuickItem = homeQuickList.get(position);
            holder.homeQuickTitle.setText(homeQuickItem.getTitle().split(" ")[0]);
            holder.homeQuickItem.setOnClickListener(homeQuickItem.getOpenClickListener());
            String website = homeQuickItem.getWebsite();
            Glide.with(EnhancedApplication.getContext()).load(R.drawable.planet).centerInside().into(holder.homeQuickIcon);
            String iconUrl = website.split("/")[0] + "/" + website.split("/")[1] + "/" + website.split("/")[2] + "/favicon.ico";
            Glide.with(EnhancedApplication.getContext()).load(iconUrl).centerInside().into(holder.homeQuickIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return homeQuickList.size();
    }

    public List<HomeQuickItem> getHomeQuickList() {
        return homeQuickList;
    }

}
