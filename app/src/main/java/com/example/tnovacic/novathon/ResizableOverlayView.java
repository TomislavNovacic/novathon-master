package com.example.tnovacic.novathon;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ResizableOverlayView extends LinearLayout implements View.OnTouchListener {

    @UiThread
    public interface RegionChangeListener {
        void regionStartedChanging();

        void regionStoppedChanging();

        void regionChanged(RectF region);
    }

    public static final int SCAN_ANIMATION_DURATION_MS = 100;
    public static final int SCAN_ANIMATION_DELTA_DP = 10;

    private final Rect mPreviewOverlayWindowRect = new Rect();

    boolean mTracking = false;

    View mPreviewOverlayUpperView;
    View mPreviewOverlayLowerView;
    View mPreviewOverlayMidView;
    View mPreviewOverlayWindow;

    private RegionChangeListener mRegionChangeListener;

    private float mLastX;
    private float mLastY;
    private RectF mLastRegion;
    private int mTouchSlop;
    private int mWindowEdgeMargin, mWindowMaxHeightPercentage;
    private int mWindowMinWidth, mWindowMaxWidth;
    private int mWindowMinHeight, mWindowMaxHeight;
    private float mTrackingStartX;
    private float mTrackingStartY;
    // saved dimensions before flip
    private int mDesiredWindowWidth, mDesiredWindowHeight;

    private Runnable mDelayedRegionSizeSetter;

    public ResizableOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( attrs, 0 );
    }

    @TargetApi(11)
    public ResizableOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init( attrs, defStyleAttr );
    }

    private void init( AttributeSet attrs, int defStyleAttr ) {
        float density = getResources().getDisplayMetrics().density;
        TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ResizableOverlayView, defStyleAttr, 0);

        mWindowMinWidth = (int)a.getDimension(
                R.styleable.ResizableOverlayView_windowMinWidth, 40*density);
        mWindowEdgeMargin = (int)a.getDimension(
                R.styleable.ResizableOverlayView_windowEdgeMargin, 10*density);
        mWindowMinHeight = (int)a.getDimension(
                R.styleable.ResizableOverlayView_windowMinHeight, 20*density);
        mWindowMaxHeightPercentage = a.getInteger(
                R.styleable.ResizableOverlayView_windowMaxHeightPercentage, 100);

        a.recycle();

        mTouchSlop = ViewConfiguration.get( getContext() ).getScaledTouchSlop();

        setOnTouchListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mPreviewOverlayUpperView = findViewById( R.id.preview_overlay_upper  );
        mPreviewOverlayLowerView = findViewById( R.id.preview_overlay_lower  );
        mPreviewOverlayMidView   = findViewById( R.id.preview_overlay_mid    );
        mPreviewOverlayWindow    = findViewById( R.id.preview_overlay_window );
    }


    public void setRegionChangeListener(RegionChangeListener regionChangeListener) {
        mRegionChangeListener = regionChangeListener;
    }

    public void setRegionSize( final float width, final float height ) {
        if( getWidth() == 0 || getHeight() == 0 ) {
            // layout still hasn't completed, schedule region size setting on next layout pass
            mDelayedRegionSizeSetter = new Runnable() {
                @Override
                public void run() {
                    setWindowSize((int) (width * getWidth()), (int) (height * getHeight()));
                }
            };
        } else {
            setWindowSize((int) (width * getWidth()), (int) (height * getHeight()));
        }
    }

    public void setWindowSize(int w, int h) {

        if (mWindowMinWidth != 0 && w < mWindowMinWidth) {
            w = mWindowMinWidth;
        } else if (mWindowMaxWidth != 0 && w > mWindowMaxWidth) {
            w = mWindowMaxWidth;
        }
        if (mWindowMinHeight != 0 && h < mWindowMinHeight) {
            h = mWindowMinHeight;
        } else if (mWindowMaxHeight != 0 && h > mWindowMaxHeight) {
            h = mWindowMaxHeight;
        }

        ViewGroup.LayoutParams layoutParams = mPreviewOverlayWindow.getLayoutParams();
        if (w != layoutParams.width || h != layoutParams.height) {
            layoutParams.width = w;
            layoutParams.height = h;

            mPreviewOverlayWindow.setLayoutParams(layoutParams);
        }
    }

    public Rect getPreviewOverlayWindowRect(){
        return mPreviewOverlayWindowRect;
    }

    private void flipWindowSizeIfRequired() {
        int width = mPreviewOverlayWindow.getLayoutParams().width;
        int height = mPreviewOverlayWindow.getLayoutParams().height;

        if( width > getWidth() || height > getHeight() ) {
            // this is possible if rotation has been performed
            // flip dimensions
            setWindowSize( height, width );
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if( mDelayedRegionSizeSetter != null ) {
            mDelayedRegionSizeSetter.run();
            mDelayedRegionSizeSetter = null;
        }

        flipWindowSizeIfRequired();

        mWindowMaxWidth = getWidth() - mWindowEdgeMargin - mWindowEdgeMargin;
        mWindowMaxHeight = (int)(mWindowMaxHeightPercentage / 100. * (getHeight() - mPreviewOverlayUpperView.getLayoutParams().height - mPreviewOverlayLowerView.getLayoutParams().height)) - mWindowEdgeMargin - mWindowEdgeMargin;

        mPreviewOverlayWindowRect.left = mPreviewOverlayWindow.getLeft();
        mPreviewOverlayWindowRect.top = mPreviewOverlayMidView.getTop();
        mPreviewOverlayWindowRect.right = mPreviewOverlayWindow.getRight();
        mPreviewOverlayWindowRect.bottom = mPreviewOverlayMidView.getBottom();


        final float left = mPreviewOverlayWindowRect.left / (float) getWidth();
        final float top = mPreviewOverlayWindowRect.top / (float) getHeight();
        final float right = mPreviewOverlayWindowRect.right / (float) getWidth();
        final float bottom = mPreviewOverlayWindowRect.bottom / (float) getHeight();

        RectF region = new RectF(left, top, right, bottom);

        if (mLastRegion == null || !mLastRegion.equals(region)) {

            mLastRegion = region;

            if (mRegionChangeListener != null) {
                mRegionChangeListener.regionChanged(region);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTrackingStartX = event.getX();
                mTrackingStartY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTracking) {
                    mTracking = false;
                    if (mRegionChangeListener != null) {
                        mRegionChangeListener.regionStoppedChanging();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (!mTracking) {
                    if (Math.abs(event.getX() - mTrackingStartX) > mTouchSlop ||
                            Math.abs(event.getY() - mTrackingStartY) > mTouchSlop) {
                        mLastX = mTrackingStartX;
                        mLastY = mTrackingStartY;
                        mTracking = true;
                        if (mRegionChangeListener != null) {
                            mRegionChangeListener.regionStartedChanging();
                        }
                    }
                }

                if (mTracking) {
                    int dx = (int)(event.getX() - mLastX), dy = (int)(event.getY() - mLastY);

                    if(mTrackingStartX <getWidth()/2){
                        dx = -dx;
                    }
                    if(mTrackingStartY<(mPreviewOverlayMidView.getTop() + mPreviewOverlayMidView.getBottom())>>1){
                        dy =-dy;
                    }

                    ViewGroup.LayoutParams windowLayoutParams = mPreviewOverlayWindow.getLayoutParams();

                    if (Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT) {
                        setWindowSize(windowLayoutParams.width + dx, windowLayoutParams.height + dy);
                    } else {
                        setWindowSize(windowLayoutParams.width + dx + dx, windowLayoutParams.height + dy + dy); // Need to add the delta twice since it gets equaly distributed to both sides of width
                    }

                    mLastX = event.getX();
                    mLastY = event.getY();
                    return true;
                }
                break;
        }

        return true;
    }
}
