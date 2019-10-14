package com.example.chatapp.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sylversky.indexablelistview.scroller.ListViewIndexScroller;

public class IndexableListView extends ListView {

    private ListViewIndexScroller mScroller = null;
    private GestureDetector mGestureDetector = null;
    private boolean invisible;

    public IndexableListView(Context context) {
        super(context);
        init();
    }

    public IndexableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndexableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        setFastScrollEnabled(true);
    }

    public void setInvisibleIndexer(boolean invisibleIndexer) {
        this.invisible = invisibleIndexer;
        mScroller.setInvisible(invisibleIndexer);
    }

    public void setIndexTypeface(Typeface typeface){
        mScroller.setDefaultTypeface(typeface);
    }

    @Override
    public void setFastScrollEnabled(boolean enabled) {
//        super.setFastScrollEnabled(enabled);
        if (enabled) {
            mScroller = new ListViewIndexScroller(getContext(), this);
        } else {
            if (mScroller != null) {
                mScroller.hide();
                mScroller = null;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Overlay index bar
        if (mScroller != null) {
            mScroller.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Intercept ListView's touch event
        if (mScroller != null && mScroller.onTouchEvent(ev))
            return true;

        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2,
                                       float velocityX, float velocityY) {
                    // If fling happens, index bar shows
                    if (mScroller != null)
                        mScroller.show();
                    return super.onFling(e1, e2, velocityX, velocityY);
                }

            });
        }
        mGestureDetector.onTouchEvent(ev);

        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mScroller!=null){
            return mScroller.contains(ev.getX(), ev.getY()) || super.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (mScroller != null) {
            mScroller.setAdapter(adapter);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mScroller != null) {
            mScroller.onSizeChanged(w, h, oldw, oldh);

            if(getChildCount() > 0 && !invisible) {
                if (h < oldh) {
                    mScroller.setInvisible(true);
                } else {
                    mScroller.setInvisible(false);
                }
            }
        }
    }

}