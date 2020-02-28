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

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private List<BookmarkItem> bookmarkList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView bookmarkTitleText;
        TextView bookmarkSiteText;
        LinearLayout bookmarkItem;
        Button editBookmarkButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookmarkTitleText = itemView.findViewById(R.id.bookmark_title_text);
            bookmarkSiteText = itemView.findViewById(R.id.bookmark_site_text);
            bookmarkItem = itemView.findViewById(R.id.bookmark_item);
            editBookmarkButton = itemView.findViewById(R.id.bookmark_edit_button);
        }

    }

    public BookmarkAdapter(List<BookmarkItem> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookmarkItem bookmarkItem = bookmarkList.get(position);
        holder.bookmarkTitleText.setText(bookmarkItem.getTitle());
        holder.bookmarkSiteText.setText(bookmarkItem.getWebsite());
        holder.bookmarkItem.setOnClickListener(bookmarkItem.getOpenClickListener());
        holder.editBookmarkButton.setOnClickListener(bookmarkItem.getEditClickListener());
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public List<BookmarkItem> getBookmarkList() {
        return bookmarkList;
    }

}
