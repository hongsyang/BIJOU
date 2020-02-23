package top.myhdg.bijou.bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.myhdg.bijou.R;

public class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.ViewHolder> {

    private List<Suggest> suggestList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout suggestItem;
        TextView suggestText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            suggestItem = itemView.findViewById(R.id.suggest_item);
            suggestText = itemView.findViewById(R.id.suggest_text);
        }

    }

    public SuggestAdapter(List<Suggest> suggestList) {
        this.suggestList = suggestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Suggest suggest = suggestList.get(position);
        holder.suggestItem.setOnClickListener(suggest.getSuggestItemClickListener());
        holder.suggestText.setText(suggest.getSuggestText());
    }

    @Override
    public int getItemCount() {
        return suggestList.size();
    }

}
