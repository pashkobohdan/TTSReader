package com.pashkobohdan.ttsreader.ui.fragments.book;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.github.clans.fab.FloatingActionMenu;
import com.pashkobohdan.ttsreader.R;
import com.pashkobohdan.ttsreader.TTSReaderProApplication;
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO;
import com.pashkobohdan.ttsreader.mvp.bookList.BookListPresenter;
import com.pashkobohdan.ttsreader.mvp.bookList.view.BookListView;
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder;
import com.pashkobohdan.ttsreader.ui.dialog.BookEditDialog;
import com.pashkobohdan.ttsreader.ui.fragments.book.widget.BookListItemWidget;
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractListFragment;
import com.pashkobohdan.ttsreader.ui.fragments.fileChoose.FileChooserDialog;
import com.pashkobohdan.ttsreader.utils.ui.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class BookListFragment extends AbstractListFragment<BookListPresenter, BookDTO> implements BookListView {

    private static final int mScrollOffset = 4;

    @InjectPresenter
    BookListPresenter presenter;
    @Inject
    Provider<BookListItemWidget> bookListItemWidgetProvider;
    @Inject
    Provider<FileChooserDialog> fileChooserDialogProvider;

    @BindView(R.id.book_list_recycler_view)
    RecyclerView bookListRecyclerView;
    @BindView(R.id.fragment_book_list_waitem_mask)
    ProgressBar waiterProgressBar;
    @BindView(R.id.add_book_fab_menu)
    FloatingActionMenu addBookActionMenu;

    @OnClick(R.id.add_book_from_file_fab)
    void addBookFromFileButtonClicked() {
        addBookActionMenu.close(true);
        //TODO open select file chooser
//        FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(getContext());
//        fileListerDialog.setOnFileSelectedListener((file, path) -> {
        //your code here
        fileChooserDialogProvider.get().show(Environment.getExternalStorageDirectory(), pathname -> true, "Choose a path", file1 -> {

        }, () -> {

        });

//        });
//        fileListerDialog.setDefaultDir(new File("/"));//TODO replace
//        fileListerDialog.setFileFilter(FileListerDialog.FILE_FILTER.ALL_FILES);
//        fileListerDialog.show();
    }

    @OnClick(R.id.add_book_create_fab)
    void createBookButtonClicked() {
        addBookActionMenu.close(true);
        showCreateNewBookDialog();
    }

    @OnClick(R.id.add_book_download_fab)
    void downloadBookButtonClicked() {
        addBookActionMenu.close(true);
        //TODO open download book dialog/fragment
    }

    private ListAdapter adapter;

    @ProvidePresenter
    public BookListPresenter getSamplePresenter() {
        return presenterProvider.get();
    }

    public static BookListFragment getNewInstance() {
        return new BookListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        TTSReaderProApplication.INSTANCE.getApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHeaderTitle("Book list fragment");

        adapter = new ListAdapter(new ArrayList<>());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        bookListRecyclerView.setLayoutManager(mLayoutManager);
        bookListRecyclerView.setAdapter(adapter);

        bookListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > mScrollOffset) {
                    if (dy > 0) {
                        addBookActionMenu.hideMenu(true);
                    } else {
                        addBookActionMenu.showMenu(true);
                    }
                }
            }
        });
    }

    @Override
    public void onPresenterAttached(BookListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showBookList(List<BookDTO> bookDTOList) {
        adapter.setDataList(bookDTOList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addBook(BookDTO bookDTO) {
        adapter.addData(bookDTO);
    }

    @Override
    public void deleteBook(int deletedBookPosition) {
        adapter.notifyItemChanged(deletedBookPosition);
        adapter.notifyItemChanged(deletedBookPosition + 1);//TODO think about this
    }

    private void showCreateNewBookDialog() {
        BookEditDialog dialog = new BookEditDialog(getContext(), null, new SaveBookAction(), null);
        dialog.show();
    }

    @Override
    public void showDataExecutionError() {
        DialogUtils.showAlert("Error", "Data execution error, try later", getContext(), null);//TODO move to string.xml
    }

    @Override
    public void showRefresh() {
        waiterProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRefresh() {
        waiterProgressBar.setVisibility(View.GONE);
    }

    @Override
    public AbstractListItemHolder<BookDTO> getItemHolder(ViewGroup parent) {
        return bookListItemWidgetProvider.get().getHolder(parent, (data) -> {
            //TODO add presenter method !
            Toast.makeText(getContext(), "clicked: " + data.getName(), Toast.LENGTH_SHORT).show();
        }, bookDTO -> {

        });
    }

    class SaveBookAction implements Action1<BookDTO> {
        @Override
        public void call(BookDTO bookDTO) {
            presenter.saveBook(bookDTO,
                    () -> {
                        Snackbar.make(addBookActionMenu, "Book is saved successfully",
                                Snackbar.LENGTH_LONG).show();
                        addBook(bookDTO);
                    },
                    () -> Snackbar.make(addBookActionMenu, "Failure whn saving book",
                            Snackbar.LENGTH_LONG).setAction("Try again", view -> call(bookDTO)).show());
        }
    }
}
