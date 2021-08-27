package com.liuzhenli.reader.bean;

import java.io.File;
import java.util.Date;

/**
 * Description:
 *
 * @author liuzhenli 2020/8/6
 * Email: 848808263@qq.com
 */
public class LocalFileBean {
    /**
     * {@link  com.liuzhenli.common.utils.Constant.FileAttr}
     */
    public String fileType;
    public String fileName;
    public long size;
    public Date time;
    public File file;
    public String filePath;
    public boolean isSelected;
    /***后缀**/
    public String FileSuffix;
    /***文件夹子文件的个数*/
    public String fileCount;
}
