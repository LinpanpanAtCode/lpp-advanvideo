package com.lpp.lppvideoplayer.controller.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class MargueeTextView extends AppCompatTextView {
    public MargueeTextView(Context context) {
        super(context);
    }

    public MargueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MargueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean isFocused() {//必须重写，且返回值是true，表示始终获取焦点
        return true;
    }
}
