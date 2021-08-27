package com.liuzhenli.reader.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.common.widget.recyclerview.adapter.BaseViewHolder;
import com.liuzhenli.common.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.liuzhenli.reader.bean.LocalFileBean;
import com.micoredu.reader.bean.BookShelfBean;
import com.micoredu.reader.helper.AppReaderDbHelper;
import com.microedu.reader.R;
import com.microedu.reader.databinding.ItemLocalBinding;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * describe:
 *
 * @author Liuzhenli on 2019-12-15 10:38
 */
public class LocalFileAdapter extends RecyclerArrayAdapter<LocalFileBean> {


    public LocalFileAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_local);
    }

    class ViewHolder extends BaseViewHolder<LocalFileBean> {
        ItemLocalBinding inflate;

        public ViewHolder(ViewGroup parent, int res) {
            super(parent, res);
            inflate = ItemLocalBinding.bind(itemView);
        }

        @Override
        public void setData(LocalFileBean item) {
            super.setData(item);

            if (item.fileType != null && TextUtils.equals(item.fileType, Constant.FileAttr.DIRECTORY)) {
                inflate.imageLocalBook.setImageResource(R.drawable.dir);
            } else {
                inflate.imageLocalBook.setImageResource(R.drawable.txt);
            }
            inflate.name.setText(item.fileName);
            try {
                inflate.size.setText(item.fileCount);
            } catch (Exception e) {
                e.printStackTrace();
                inflate.size.setVisibility(View.GONE);
            }
            //处理文件选中的逻辑
            List<BookShelfBean> list = AppReaderDbHelper.getInstance().getDatabase().getBookShelfDao().getByNoteUrl(item.filePath);
            int size = list.size();
            if (TextUtils.equals(item.fileType, Constant.FileAttr.DIRECTORY)) {
                inflate.cbLocalCheck.setVisibility(View.GONE);
                inflate.tvLocalImport.setVisibility(View.GONE);
            } else {
                inflate.cbLocalCheck.setVisibility(size == 0 ? View.VISIBLE : View.GONE);
                inflate.tvLocalImport.setVisibility(size == 0 ? View.GONE : View.VISIBLE);
                inflate.cbLocalCheck.setChecked(item.isSelected);
            }

        }
    }
}
