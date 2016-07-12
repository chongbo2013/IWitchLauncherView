package example.ferris.com.iwitchlauncherview;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * 每个圆环信息
 * Created by ferris on 2016/7/7.
 */
public class CircleBean {

    //圆环路径
    private Path mPath;
    //第几个圆环
    private int index;
    //圆环的区域
    private RectF mRectF;
    //圆环的图标数量
    private int iconCounts;

    public Path getmPath() {
        return mPath;
    }

    public void setmPath(Path mPath) {
        this.mPath = mPath;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public RectF getmRectF() {
        return mRectF;
    }

    public void setmRectF(RectF mRectF) {
        this.mRectF = mRectF;
    }

    public int getIconCounts() {
        return iconCounts;
    }

    public void setIconCounts(int iconCounts) {
        this.iconCounts = iconCounts;
    }
}
