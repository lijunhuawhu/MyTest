package com.example.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ss.android.common.R;

import java.util.ArrayList;

public class WrapLineFlowLayout extends ViewGroup {

    protected int mHSpacing = 0;
    protected int mVSpacing = 0;
    protected final boolean mAlignCenter;
    final ArrayList<LayoutParams> mTmpRow = new ArrayList<LayoutParams>();

    public WrapLineFlowLayout(Context context) {
        this(context, null, 0);
    }

    public WrapLineFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapLineFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WrapLineFlowLayout, defStyle, 0);
        mHSpacing = ta.getDimensionPixelOffset(R.styleable.WrapLineFlowLayout_hSpacing, 0);
        mVSpacing = ta.getDimensionPixelOffset(R.styleable.WrapLineFlowLayout_vSpacing, 0);
        mAlignCenter = ta.getBoolean(R.styleable.WrapLineFlowLayout_alignCenter, false);
        ta.recycle();
        if (mHSpacing < 0)
            mHSpacing = 0;
        if (mVSpacing < 0)
            mVSpacing = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int top = getPaddingTop();
        int left = getPaddingLeft();
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = 0;
        }
        final int netWidth = widthSize - getPaddingLeft() - getPaddingRight();
        final int count = getChildCount();
        int x = left;
        int y = top;
        int rowHeight = 0; // max_height in one row
        int w = netWidth;
        mTmpRow.clear();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams)child.getLayoutParams();
            int w_spec;
            int h_spec;
            if (netWidth <= 0) {
                w_spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
                h_spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
                child.measure(w_spec, h_spec);
                lp.x = x;
                lp.y = y;
                continue;
            }
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                w_spec = MeasureSpec.makeMeasureSpec(netWidth, MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.MATCH_PARENT) {
                w_spec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
            } else {
                w_spec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                h_spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            } else if (lp.height == LayoutParams.MATCH_PARENT) {
                h_spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
            } else {
                h_spec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }
            child.measure(w_spec, h_spec);
            if (child.getMeasuredWidth() > w) {
                if (rowHeight > 0) {
                    y += rowHeight + mVSpacing;
                }
                if (mAlignCenter && w > 1 && !mTmpRow.isEmpty()) {
                    int off = w / 2;
                    for (LayoutParams t_lp: mTmpRow)
                        t_lp.x += off;
                }
                mTmpRow.clear();
                x = left;
                w = netWidth;
                rowHeight = 0;
            }
            lp.x = x;
            lp.y = y;
            if (mAlignCenter)
                mTmpRow.add(lp);
            int itemWidth = child.getMeasuredWidth() + mHSpacing;
            x += itemWidth;
            w -= itemWidth;
            if (child.getMeasuredHeight() > rowHeight) {
                rowHeight = child.getMeasuredHeight();
            }
        }
        if (mAlignCenter && w > 1 && !mTmpRow.isEmpty()) {
            int off = w / 2;
            for (LayoutParams t_lp: mTmpRow)
                t_lp.x += off;
        }
        if (rowHeight > 0) {
            y += rowHeight;
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = y + getPaddingBottom();
        } else if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = Math.min(heightSize, y + getPaddingBottom());
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams)child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }
    }
    
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }
    
    public static class LayoutParams extends ViewGroup.LayoutParams {

        public int x;
        public int y;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }
}
