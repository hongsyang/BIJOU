package top.myhdg.bijou.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zhouwei.library.CustomPopWindow;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import top.myhdg.bijou.R;
import top.myhdg.bijou.bean.Bookmark;
import top.myhdg.bijou.bean.BookmarkAdapter;
import top.myhdg.bijou.bean.BookmarkItem;
import top.myhdg.bijou.bean.HomeQuick;

public class BookmarkActivity extends BaseActivity {

    private RecyclerView bookmarkRecyclerView;
    public static List<Bookmark> bookmarks;
    private List<BookmarkItem> bookmarkList;
    private BookmarkAdapter adapter;

    private CustomPopWindow addBookmarkPopWindow;
    private CustomPopWindow editBookmarkPopWindow;

    private Bookmark editBookMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmark);

        Button exitButton = findViewById(R.id.exit_bookmark_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookmarkActivity.this.finish();
            }
        });

        bookmarkRecyclerView = findViewById(R.id.bookmark_recycler_view);
        bookmarkRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookmarkList = new ArrayList<>();
        bookmarks = LitePal.findAll(Bookmark.class);
        adapter = new BookmarkAdapter(bookmarkList);
        bookmarkRecyclerView.setAdapter(adapter);
        syncBookmarkList();
        ItemTouchHelper helper = new ItemTouchHelper(new DragBookmarkItemCallback(adapter));
        helper.attachToRecyclerView(bookmarkRecyclerView);

        FloatingActionButton fabAddBookmark = findViewById(R.id.fab_add_bookmark_);
        fabAddBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("InflateParams") View view = LayoutInflater.from(BookmarkActivity.this).inflate(R.layout.add_bookmark_pop_window, null);
                initAddBookmarkPopWindow(view);
                addBookmarkPopWindow = new CustomPopWindow.PopupWindowBuilder(BookmarkActivity.this)
                        .setView(view)
                        .enableBackgroundDark(true)
                        .create()
                        .showAtLocation(findViewById(R.id.bookmark_layout), Gravity.CENTER, 0, 0);
            }
        });
    }

    /**
     * 同步书签列表
     */
    private void syncBookmarkList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bookmarkList.clear();
                for (int i = 0; i < bookmarks.size(); i++) {
                    final Bookmark bookmark = bookmarks.get(i);
                    BookmarkItem bookmarkItem = new BookmarkItem();
                    bookmarkItem.setId(bookmark.getId());
                    bookmarkItem.setTitle(bookmark.getTitle());
                    bookmarkItem.setWebsite(bookmark.getWebsite());
                    bookmarkItem.setEditClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editBookMark = bookmark;
                            @SuppressLint("InflateParams") View view = LayoutInflater.from(BookmarkActivity.this).inflate(R.layout.add_bookmark_pop_window, null);
                            initEditBookmarkPopWindow(view);
                            editBookmarkPopWindow = new CustomPopWindow.PopupWindowBuilder(BookmarkActivity.this)
                                    .setView(view)
                                    .enableBackgroundDark(true)
                                    .create()
                                    .showAtLocation(findViewById(R.id.bookmark_layout), Gravity.CENTER, 0, 0);
                        }
                    });
                    bookmarkItem.setOpenClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent data = new Intent();
                            data.putExtra("url", bookmark.getWebsite());
                            setResult(Activity.RESULT_FIRST_USER, data);
                            BookmarkActivity.this.finish();
                        }
                    });
                    bookmarkList.add(bookmarkItem);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化添加书签弹窗
     */
    private void initAddBookmarkPopWindow(View view) {
        final EditText titleEdit = view.findViewById(R.id.title_edit);
        final EditText websiteEdit = view.findViewById(R.id.website_edit);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_bookmark_button:
                        if (!titleEdit.getText().toString().equals("") && !websiteEdit.getText().toString().equals("")) {
                            Bookmark bookmark = new Bookmark();
                            bookmark.setTitle(titleEdit.getText().toString());
                            bookmark.setWebsite(websiteEdit.getText().toString());
                            bookmark.save();
                            bookmarks.add(bookmark);
                            syncBookmarkList();
                            addBookmarkPopWindow.dissmiss();
                            showSnackBar(bookmarkRecyclerView, "书签添加成功");
                        }
                        break;
                    case R.id.add_home_quick_button:
                        if (!titleEdit.getText().toString().equals("") && !websiteEdit.getText().toString().equals("")) {
                            HomeQuick homeQuick = new HomeQuick();
                            homeQuick.setTitle(titleEdit.getText().toString());
                            homeQuick.setWebsite(websiteEdit.getText().toString());
                            homeQuick.save();
                            Intent data = new Intent();
                            data.putExtra("add_home_quick", true);
                            setResult(Activity.RESULT_FIRST_USER, data);
                            addBookmarkPopWindow.dissmiss();
                            showSnackBar(bookmarkRecyclerView, "已添加至主页");
                        }
                        break;
                    case R.id.cancel_add_bookmark_button:
                        addBookmarkPopWindow.dissmiss();
                        break;
                }
            }
        };

        TextView addButton = view.findViewById(R.id.add_bookmark_button);
        addButton.setOnClickListener(clickListener);
        TextView homeButton = view.findViewById(R.id.add_home_quick_button);
        homeButton.setOnClickListener(clickListener);
        TextView cancelButton = view.findViewById(R.id.cancel_add_bookmark_button);
        cancelButton.setOnClickListener(clickListener);
    }

    /**
     * 初始化编辑书签弹窗
     */
    private void initEditBookmarkPopWindow(View view) {
        TextView textView = view.findViewById(R.id.add_bookmark_text);
        textView.setText("编辑");
        final EditText titleEdit = view.findViewById(R.id.title_edit);
        titleEdit.setText(editBookMark.getTitle());
        final EditText websiteEdit = view.findViewById(R.id.website_edit);
        websiteEdit.setText(editBookMark.getWebsite());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_bookmark_button:
                        if (!titleEdit.getText().toString().equals("") && !websiteEdit.getText().toString().equals("")) {
                            bookmarks.get(bookmarks.indexOf(editBookMark)).setTitle(titleEdit.getText().toString());
                            bookmarks.get(bookmarks.indexOf(editBookMark)).setWebsite(websiteEdit.getText().toString());
                            bookmarks.get(bookmarks.indexOf(editBookMark)).save();
                            syncBookmarkList();
                            editBookmarkPopWindow.dissmiss();
                            showSnackBar(bookmarkRecyclerView, "已保存更改");
                        }
                        break;
                    case R.id.add_home_quick_button:
                        if (!titleEdit.getText().toString().equals("") && !websiteEdit.getText().toString().equals("")) {
                            HomeQuick homeQuick = new HomeQuick();
                            homeQuick.setTitle(titleEdit.getText().toString());
                            homeQuick.setWebsite(websiteEdit.getText().toString());
                            homeQuick.save();
                            Intent data = new Intent();
                            data.putExtra("add_home_quick", true);
                            setResult(Activity.RESULT_FIRST_USER, data);
                            editBookmarkPopWindow.dissmiss();
                            showSnackBar(bookmarkRecyclerView, "已添加至主页");
                        }
                        break;
                    case R.id.cancel_add_bookmark_button:
                        editBookmarkPopWindow.dissmiss();
                        showActionSnackBar(bookmarkRecyclerView, "确定删除书签吗？(此操作不可逆)", "删除", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LitePal.delete(Bookmark.class, editBookMark.getId());
                                bookmarks.remove(editBookMark);
                                syncBookmarkList();
                                showSnackBar(bookmarkRecyclerView, "书签已删除");
                            }
                        });
                        break;
                }
            }
        };

        TextView editButton = view.findViewById(R.id.add_bookmark_button);
        editButton.setText("更改");
        editButton.setOnClickListener(clickListener);
        TextView homeButton = view.findViewById(R.id.add_home_quick_button);
        homeButton.setOnClickListener(clickListener);
        TextView cancelButton = view.findViewById(R.id.cancel_add_bookmark_button);
        cancelButton.setText("删除");
        cancelButton.setOnClickListener(clickListener);
    }

    /**
     * 获取SnackBar背景颜色
     */
    private int getSnackBarBackgroundColor() {
        SharedPreferences prefs = getSharedPreferences("BIJOU", MODE_PRIVATE);
        if (prefs.getBoolean("dark_mode", false)) {
            return ContextCompat.getColor(this, R.color.colorAccent_dark);
        } else {
            return ContextCompat.getColor(this, R.color.colorAccent);
        }
    }

    /**
     * 显示SnackBar
     */
    private void showSnackBar(View view, String content) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getSnackBarBackgroundColor())
                .setTextColor(Color.parseColor("#ffffff"))
                .show();
    }

    /**
     * 显示带Action的SnackBar
     */
    private void showActionSnackBar(View view, String content, String action, View.OnClickListener clickListener) {
        Snackbar.make(view, content, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getSnackBarBackgroundColor())
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setAction(action, clickListener)
                .setActionTextColor(Color.parseColor("#ffffff"))
                .show();
    }

    static class DragBookmarkItemCallback extends ItemTouchHelper.Callback {

        private BookmarkAdapter adapter;

        public DragBookmarkItemCallback(BookmarkAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlag;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                        | ItemTouchHelper.START | ItemTouchHelper.END;
            } else {
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlag, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Bookmark bookmark1;
            Bookmark bookmark2;
            Bookmark bookmarkTemp = new Bookmark();

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    bookmark1 = BookmarkActivity.bookmarks.get(i);
                    bookmark2 = BookmarkActivity.bookmarks.get(i + 1);
                    bookmarkTemp.setTitle(bookmark1.getTitle());
                    bookmarkTemp.setWebsite(bookmark1.getWebsite());
                    bookmark1.setTitle(bookmark2.getTitle());
                    bookmark1.setWebsite(bookmark2.getWebsite());
                    bookmark1.save();
                    bookmark2.setTitle(bookmarkTemp.getTitle());
                    bookmark2.setWebsite(bookmarkTemp.getWebsite());
                    bookmark2.save();
                    Collections.swap(adapter.getBookmarkList(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    bookmark1 = BookmarkActivity.bookmarks.get(i);
                    bookmark2 = BookmarkActivity.bookmarks.get(i - 1);
                    bookmarkTemp.setTitle(bookmark1.getTitle());
                    bookmarkTemp.setWebsite(bookmark1.getWebsite());
                    bookmark1.setTitle(bookmark2.getTitle());
                    bookmark1.setWebsite(bookmark2.getWebsite());
                    bookmark1.save();
                    bookmark2.setTitle(bookmarkTemp.getTitle());
                    bookmark2.setWebsite(bookmarkTemp.getWebsite());
                    bookmark2.save();
                    Collections.swap(adapter.getBookmarkList(), i, i - 1);
                }
            }

            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder.itemView.setAlpha(0.5f);
            }
        }

        @Override
        public void clearView(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(1);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

    }

}
