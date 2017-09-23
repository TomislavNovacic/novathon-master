package com.example.tnovacic.novathon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by dodo on 24/11/14.
 */
public class SimpleRectangleView extends View {

    private Paint mPaint = null;
    private Rect[] mRects = null;

    @SuppressWarnings("NewApi")
    public SimpleRectangleView(Context context) {
        super(context);
        setBackgroundColor(Color.TRANSPARENT);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.ADD ) );
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int frameBorderWidth = (dm.densityDpi + 79) / 80;

        mPaint.setStrokeWidth(frameBorderWidth);
    }

    public void setRectangles(Rect[] rectangles) {
        mRects = rectangles;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mRects != null) {
            for(Rect r : mRects) {
                canvas.drawRect(r, mPaint);
            }
        }
    }
}
