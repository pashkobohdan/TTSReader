package com.pashkobohdan.ttsreader.ui.fragments.fileChoose;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pashkobohdan.ttsreader.R;
import com.pashkobohdan.ttsreader.model.dto.file.FileDTO;
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder;
import com.pashkobohdan.ttsreader.ui.common.adapter.AbstractListAdapter;
import com.pashkobohdan.ttsreader.ui.fragments.fileChoose.widget.FileChoosingItemWidget;
import com.pashkobohdan.ttsreader.ui.dialog.DialogUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action0;
import rx.functions.Action1;

public class FileChooserDialog {
    @BindView(R.id.file_chooser_file_full_path_elements)
    RecyclerView filePathElementList;
    @BindView(R.id.file_chooser_file_list)
    RecyclerView fileList;

    private AlertDialog dialog;

    private Context context;
    private AbstractListAdapter adapter;
    Action1<File> okCallback;
    Action0 cancelCallback;
    private File currentPath;

    private FileFilter fileFilters;

    @Inject
    Provider<FileChoosingItemWidget> fileChoosingItemWidgetProvider;

    @Inject
    public FileChooserDialog(Context context) {
        this.context = context;
    }

    public void show(File rootPath, @NonNull FileFilter fileFilters, String title, Action1<File> okCallback, Action0 cancelCallback) {
        this.fileFilters = fileFilters;
        this.okCallback = okCallback;
        this.cancelCallback = cancelCallback;

        LayoutInflater factory = LayoutInflater.from(context);
        View dialogInputView = factory.inflate(R.layout.dialog_choose_file, null);
        ButterKnife.bind(this, dialogInputView);

        LinearLayoutManager filePathLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true);
        filePathElementList.setLayoutManager(filePathLayoutManager);
        fileList.setLayoutManager(new LinearLayoutManager(context));

        adapter = new AbstractListAdapter<FileDTO>(new ArrayList<>()) {
            @Override
            public AbstractListItemHolder<FileDTO> createItemHolder(ViewGroup parent) {
                return fileChoosingItemWidgetProvider.get().getHolder(parent, fileDTO -> {
                    File chooseFile = fileDTO.getFile();
                    if (chooseFile == null) {
                        goBack();
                    } else if (chooseFile.isDirectory()) {
                        loadPath(chooseFile);
                    } else {
                        //TODO show confirm dialog before !
                        okCallback.call(chooseFile);
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onBindViewHolder(AbstractListItemHolder<FileDTO> holder, int position) {
                holder.onBindViewHolder(dataList.get(position));
            }
        };
        fileList.setAdapter(adapter);

        loadPath(rootPath);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    cancelCallback.call();
                    dialog.dismiss();
                })
                .setView(dialogInputView);
        dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(view -> trySelectFile(dialog));

            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancelButton.setOnClickListener(v -> {
                DialogUtils.showConfirm(R.string.file_choosing_dialog_cancel_confirm,
                        context, () -> {
                            cancelCallback.call();
                            dialog.dismiss();
                        });
            });
        });
        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                goBack();
            }
            return true;
        });

        dialog.show();
    }

    private void loadPath(File file) {
        this.currentPath = file;

        if (!file.isDirectory()) {
            file = new File(file.getPath());
        }

        List<FileDTO> fileDTOList = new ArrayList<>();
        if (canGoBack()) {
            fileDTOList.add(new FileDTO(null));
        }
        
        File[] subFiles = file.listFiles(fileFilters);
        List<File> listOfFiles = subFiles == null ? null : Arrays.asList(subFiles);
        if (listOfFiles != null) {
            Collections.sort(listOfFiles, (o1, o2) -> o1.getAbsolutePath().compareTo(o2.getAbsolutePath()));
            for (File subFile : listOfFiles) {
                if (subFile.isDirectory()) {
                    fileDTOList.add(new FileDTO(subFile));
                }
            }
            for (File subFile : listOfFiles) {
                if (!subFile.isDirectory()) {
                    fileDTOList.add(new FileDTO(subFile));
                }
            }
        }

        adapter.setDataList(fileDTOList);
    }

    private boolean canGoBack() {
        return !currentPath.getAbsolutePath().equals("/");
    }

    private void goBack() {
        if (canGoBack()) {
            loadPath(new File(currentPath.getParent()));
        }
    }

    private void trySelectFile(Dialog dialog) {
        //TODO !
    }
}
