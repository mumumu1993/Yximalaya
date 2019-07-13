package com.yuqingsen.yximalaya.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yuqingsen.yximalaya.R;

@SuppressLint("AppCompatCustomView")
public class LoadingView extends ImageView {
    //旋转角度
    private int rotateDegree = 0;

    private boolean mNeedRoate = false;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.load);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRoate = true;
        //绑定到Window的时候
        post(new Runnable() {
            @Override
            public void run() {
                rotateDegree += 30;
                rotateDegree = rotateDegree<=360?rotateDegree:0;
                invalidate();
                //是否继续旋转
                if (mNeedRoate) {
                    postDelayed(this,100);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //从Window解绑了
        mNeedRoate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 第一个参数，旋转角度
         * 第二次参数，旋转的X坐标
         * 第三个参数，旋转的Y坐标
         */
        canvas.rotate(rotateDegree,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
