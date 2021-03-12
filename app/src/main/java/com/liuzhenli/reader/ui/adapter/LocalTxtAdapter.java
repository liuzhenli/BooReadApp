package com.liuzhenli.reader.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liuzhenli.greendao.BookShelfBeanDao;
import com.liuzhenli.reader.bean.LocalFileBean;
import com.liuzhenli.common.utils.Constant;
import com.liuzhenli.common.utils.FileUtils;
import com.liuzhenli.common.widget.recyclerview.adapter.BaseViewHolder;
import com.liuzhenli.common.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.micoredu.reader.bean.BookShelfBean;
import com.micoredu.reader.helper.DbHelper;
import com.microedu.reader.R;
import com.microedu.reader.databinding.ItemLocalBinding;

import org.greenrobot.greendao.query.Query;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * describe:
 *
 * @author Liuzhenli on 2019-12-15 10:38
 */
public class LocalTxtAdapter extends RecyclerArrayAdapter<LocalFileBean> {

    private SimpleDateFormat simpleDateFormat;

    public LocalTxtAdapter(Context context) {
        super(context);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_local);
    }

    class ViewHolder extends BaseViewHolder<LocalFileBean> {

        ItemLocalBinding inflate;

        public ViewHolder(ViewGroup parent, int res) {
            super(parent, res);
            inflate = ItemLocalBinding.inflate(LayoutInflater.from(mContext));
        }

        @Override
        public void setData(LocalFileBean item) {
            super.setData(item);
            //处理文件选中的逻辑
            Query<BookShelfBean> build = DbHelper.getDaoSession().getBookShelfBeanDao().queryBuilder().where(BookShelfBeanDao.Properties.NoteUrl.eq(item.file.toString())).build();
            List<BookShelfBean> list = build.list();
            int size = list.size();
            inflate.name.setText(item.file.getName());
            if (item.fileType == null || item.fileType.equals(Constant.FileAttr.ZERO)) {
                inflate.imageLocalBook.setImageResource(R.drawable.dir);
                inflate.cbLocalCheck.setVisibility(View.GONE);
                inflate.tvLocalImport.setVisibility(View.GONE);
                inflate.size.setText(item.fileCount);
            } else {
                inflate.imageLocalBook.setImageResource(R.drawable.txt);
                inflate.cbLocalCheck.setVisibility(size == 0 ? View.VISIBLE : View.GONE);
                inflate.tvLocalImport.setVisibility(size == 0 ? View.GONE : View.VISIBLE);
                inflate.cbLocalCheck.setChecked(item.isSelected);
                String fileSize = FileUtils.formatFileSizeToString(item.file.length());
                long data = item.file.lastModified();
                String time = simpleDateFormat.format(data);
                inflate.size.setText(String.format("%s     %s", time, fileSize));
            }
        }
    }
}
