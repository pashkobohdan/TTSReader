package com.pashkobohdan.ttsreader.ui.fragments.fileChoose.widget;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pashkobohdan.ttsreader.R;
import com.pashkobohdan.ttsreader.databinding.WidgetFileChoosingItemBinding;
import com.pashkobohdan.ttsreader.model.dto.file.FileDTO;
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder;
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractListItemWidget;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class FileChoosingItemWidget extends AbstractListItemWidget<FileDTO> {

    @BindView(R.id.file_choose_item_image)
    ImageView image;
    @BindView(R.id.file_choose_item_title)
    TextView title;

    @Inject
    public FileChoosingItemWidget() {
    }

    @Override
    public AbstractListItemHolder<FileDTO> getHolder(ViewGroup parent, Action1<FileDTO> okClickCallback) {
        WidgetFileChoosingItemBinding binding = WidgetFileChoosingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ButterKnife.bind(this, binding.getRoot());
        return new AbstractListItemHolder<>(binding.getRoot(), okClickCallback, fileDTO -> {
            if(fileDTO.getFile() == null) {
                title.setText("...");
            } else {
                binding.setFile(fileDTO);
            }
            image.setImageResource(fileDTO.getFile() == null ? R.drawable.back : fileDTO.getIsDirectory() ? R.drawable.folder : R.drawable.file);
        });
    }
}
