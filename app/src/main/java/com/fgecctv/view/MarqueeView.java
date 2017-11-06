package com.fgecctv.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public class MarqueeView extends TextView {
    public static final int SPEED_NORMAL = 40;
    private int currentScrollX;
    private int screenWidth;
    private int speed = SPEED_NORMAL;
    private ScrollText mScrollText = new ScrollText();

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) Math.max(getTextWidth(), screenWidth), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        postDelayed(mScrollText, speed);
    }

    public float getTextWidth() {
        Paint paint = getPaint();
        return paint.measureText(getText().toString());
    }

    public void setPeriod(int speed) {
        this.speed = speed;
    }

    public void resetOffset() {
        currentScrollX = 0;
    }

    public static class Params {
        public String text;
        public float textSize;
        public int textColor;
        public float alpha;
        public ViewGroup.LayoutParams layoutParams;
        public int period;
        public long showTime;
    }

    public class ScrollText implements Runnable {
        @Override
        public void run() {
            currentScrollX++;
            scrollTo(currentScrollX, 0);
            if (getScrollX() >= getTextWidth()) {
                scrollTo(-screenWidth, 0);
                currentScrollX = -screenWidth;
            }
        }
    }
}