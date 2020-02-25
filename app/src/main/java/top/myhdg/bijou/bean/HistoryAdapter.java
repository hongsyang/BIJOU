package top.myhdg.bijou.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.myhdg.bijou.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> historyItemList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView historyIdText;
        TextView historyTitleText;
        TextView historySiteText;
        TextView historyTimeText;
        LinearLayout historyItem;
        Button deleteHistoryButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            historyIdText = itemView.findViewById(R.id.history_id_text);
            historyTitleText = itemView.findViewById(R.id.history_title_text);
            historySiteText = itemView.findViewById(R.id.history_site_text);
            historyTimeText = itemView.findViewById(R.id.history_time_text);
            historyItem = itemView.findViewById(R.id.history_item);
            deleteHistoryButton = itemView.findViewById(R.id.history_delete_button);
        }
    }

    public HistoryAdapter(List<HistoryItem> historyItemList) {
        this.historyItemList = historyItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem historyItem = historyItemList.get(position);
        holder.historyIdText.setText(String.valueOf(position + 1));
        holder.historyTitleText.setText(historyItem.getTitle());
        holder.historySiteText.setText(historyItem.getWebsite());
        holder.historyTimeText.setText(historyItem.getTime());
        holder.deleteHistoryButton.setOnClickListener(historyItem.getDeleteClickListener());
        holder.historyItem.setOnClickListener(historyItem.getOpenClickListener());
    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

}
