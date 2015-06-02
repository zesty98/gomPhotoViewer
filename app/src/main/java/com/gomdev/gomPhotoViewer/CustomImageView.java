package com.gomdev.gomPhotoViewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * Created by gomdev on 15. 5. 28..
 */
public class CustomImageView extends ImageView {
    private static final String CLASS = "CustomImageView";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private static final int TRANSLATE_X_INDEX = 2;
    private static final int TRANSLATE_Y_INDEX = 5;

    class Transform {
        float mScale = 1f;
        float mTranslateX = 0f;
        float mTranslateY = 0f;
    }

    enum State {
        NONE,
        DRAG,
        FLING,
        ZOOM
    }

    private GestureDetector mGestureDetector = null;
    private ScaleGestureDetector mScaleGestureDetector = null;

    private Transform mOriginalTransform = new Transform();
    private State mState = State.NONE;

    private float mCurrentScale = 1f;

    private Matrix mMatrix = new Matrix();

    private float mWidth = 0f;
    private float mHeight = 0f;

    private float mImageWidth = 0f;
    private float mImageHeight = 0f;

    private boolean mCanScrollLeft = true;
    private boolean mCanScrollRight = true;

    private RectF mViewport = null;

    // drag
    private int mActiveID = -1;
    private boolean mIsDown = false;
    private float mPrevX = 0f;
    private float mPrevY = 0f;

    // fling
    private Scroller mScroller = null;
    private float mCurrentFlingX = 0f;
    private float mCurrentFlingY = 0f;

    public CustomImageView(Context context) {
        super(context);

        init(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mGestureListener);

        mScroller = new Scroller(context);
    }

    boolean canScroll(int dx) {
        if (dx >= 0 && mCanScrollRight == true) {
            return false;
        }

        if (dx <= 0 && mCanScrollLeft == true) {
            return false;
        }

        return true;
    }

    private void setState(State state) {
        mState = state;
    }

    private State getState() {
        return mState;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        Rect rect = drawable.getBounds();

        mWidth = getWidth();
        mHeight = getHeight();

        mImageWidth = rect.width();
        mImageHeight = rect.height();

        fitToScreen();

        float left = (mWidth - mImageWidth * mOriginalTransform.mScale) * 0.5f;
        float right = left + mImageWidth * mOriginalTransform.mScale;
        float top = (mHeight - mImageHeight * mOriginalTransform.mScale) * 0.5f;
        float bottom = top + mImageHeight * mOriginalTransform.mScale;

        mViewport = new RectF(left, top, right, bottom);
    }

    private void fitToScreen() {
        float ratio = mWidth / mHeight;
        float imageRatio = mImageWidth / mImageHeight;

        float scale = 1f;
        float translateX = 0f;
        float translateY = 0f;
        if (ratio > imageRatio) {
            // fit height
            scale = mHeight / mImageHeight;

            translateX = (mWidth - scale * mImageWidth) * 0.5f;
        } else {
            // fit width
            scale = mWidth / mImageWidth;

            translateY = (mHeight - scale * mImageHeight) * 0.5f;
        }

        mOriginalTransform.mScale = scale;
        mOriginalTransform.mTranslateX = translateX;
        mOriginalTransform.mTranslateY = translateY;

        mMatrix.setScale(scale, scale);
        mMatrix.postTranslate(translateX, translateY);

        mCurrentScale = scale;

        setScaleType(ScaleType.MATRIX);
        setImageMatrix(mMatrix);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);

        State state = getState();
        if (state == State.ZOOM) {
            return true;
        }

        int action = event.getAction();
        int index = event.getActionIndex();
        int activeID = event.getPointerId(index);

        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);

                mIsDown = true;
                mActiveID = activeID;

                setState(State.DRAG);

                break;
            case MotionEvent.ACTION_UP:
                if (mActiveID != activeID) {
                    return true;
                }

                setState(State.NONE);

                mIsDown = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsDown == false || mActiveID != activeID) {
                    return true;
                }

                if (state == State.DRAG) {
                    mMatrix.postTranslate(x - mPrevX, y - mPrevY);
                    adjustPosition();
                    CustomImageView.this.setImageMatrix(mMatrix);
                }

                break;
        }

        mPrevX = x;
        mPrevY = y;

        return super.onTouchEvent(event);
    }

    private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            float scaleFactor = detector.getScaleFactor();

            float currentScale = mCurrentScale * scaleFactor;
            if (currentScale < mOriginalTransform.mScale) {
                return true;
            }

            mCurrentScale = currentScale;

            mMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);

            adjustPosition();

            mCanScrollLeft = false;
            mCanScrollRight = false;

            CustomImageView.this.setImageMatrix(mMatrix);

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (DEBUG) {
                Log.d(TAG, "onScaleBegin()");
            }
            setState(State.ZOOM);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (DEBUG) {
                Log.d(TAG, "onScaleEnd()");
            }
            setState(State.NONE);
            super.onScaleEnd(detector);
        }
    };


    private void adjustPosition() {
        float[] values = new float[9];
        mMatrix.getValues(values);

        float deltaX = values[TRANSLATE_X_INDEX];
        float deltaY = values[TRANSLATE_Y_INDEX];

        float currentImageWidth = getCurrentImageWidth();

        float translateX = 0f;

        float left = 0f;
        float right = 0f;

        if (currentImageWidth > mWidth) {
            left = 0f;
            right = mWidth;
        } else {
            left = (mWidth - currentImageWidth) * 0.5f;
            right = left + currentImageWidth;
        }

        if (deltaX >= left) {
            translateX = left - deltaX;
            mCanScrollLeft = true;
            mCanScrollRight = false;
        } else if ((deltaX + currentImageWidth) <= right) {
            translateX = right - (deltaX + currentImageWidth);
            mCanScrollRight = true;
            mCanScrollLeft = false;
        } else {
            translateX = 0f;

            mCanScrollRight = false;
            mCanScrollLeft = false;
        }

        if (currentImageWidth <= mWidth) {
            mCanScrollLeft = true;
            mCanScrollRight = true;
        }

        float currentImageHeight = getCurrentImageHeight();

        float translateY = 0f;
        float top = 0f;
        float bottom = 0f;

        if (currentImageHeight > mHeight) {
            top = 0f;
            bottom = mHeight;
        } else {
            top = (mHeight - currentImageHeight) * 0.5f;
            bottom = top + currentImageHeight;
        }

        if (deltaY > top) {
            translateY = top - deltaY;
        } else if ((deltaY + currentImageHeight) < bottom) {
            translateY = bottom - (deltaY + currentImageHeight);
        } else {
            translateY = 0f;
        }

        mMatrix.postTranslate(translateX, translateY);
    }

    private float getCurrentImageWidth() {
        return mImageWidth * mCurrentScale;
    }

    private float getCurrentImageHeight() {
        return mImageHeight * mCurrentScale;
    }

    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            State state = getState();

            if (state != State.DRAG) {
                return true;
            }

            fling((int) velocityX, (int) velocityY);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }
    };

    private void fling(int velocityX, int velocityY) {
        setState(State.FLING);

        float[] values = new float[9];
        mMatrix.getValues(values);

        mCurrentFlingX = (int) values[TRANSLATE_X_INDEX];
        mCurrentFlingY = (int) values[TRANSLATE_Y_INDEX];

        mScroller.forceFinished(true);

        float currentImageWidth = getCurrentImageWidth();
        float currentImageHeight = getCurrentImageHeight();

        int minX = 0;
        int maxX = 0;
        if (currentImageWidth > mWidth) {
            minX = (int) (mWidth - currentImageWidth);
            maxX = 0;
        } else {
            minX = maxX = (int) mCurrentFlingX;
        }

        int minY = 0;
        int maxY = 0;
        if (currentImageHeight > mHeight) {
            minY = (int) (mHeight - currentImageHeight);
            maxY = 0;
        } else {
            minY = maxY = (int) mCurrentFlingY;
        }

        mScroller.fling(
                (int) mCurrentFlingX,
                (int) mCurrentFlingY,
                velocityX,
                velocityY,
                minX, maxX,
                minY, maxY);

        ViewCompat.postInvalidateOnAnimation(this);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();

            float translateX = x - mCurrentFlingX;
            float translateY = y - mCurrentFlingY;

            mMatrix.postTranslate(translateX, translateY);
            adjustPosition();
            setImageMatrix(mMatrix);

            mCurrentFlingX = x;
            mCurrentFlingY = y;

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
