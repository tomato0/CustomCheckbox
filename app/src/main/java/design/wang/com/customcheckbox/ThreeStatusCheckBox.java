package design.wang.com.customcheckbox;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2017/1/2 0002.
 * design.wang.com.customcheckbox
 * 功能、作用：
 */

public class ThreeStatusCheckBox extends View {
    private static final int NO_SELECT = 0;
    private static final int HALF_SELECT = 1;
    private static final int ALL_SELECT = 2;
    private int mSize;
    private int mStrokeWidth;
    private Paint mStrokePaint;
    private RectF mStrokeRec;
    private Paint mInnerPaint;
    private RectF mInnerRec;
    private Paint mNoSelectPaint;
    private RectF mNoSelectRec;
    private Bitmap mTickBitmap;
    private Rect mTickBitmapRec;
    private Paint mTickPaint;
    private ObjectAnimator mInnerAnimator;
    private int selectStatus;
    private boolean attachedToWindow;
    private int mHalfSelectColor;
    private int mAllSelectColor;
    private int mStrokeColor;
    private OnThreeStatusCheckBoxChangeListener mCheckBoxListener;

    public void setCheckBoxListener(OnThreeStatusCheckBoxChangeListener mCheckBoxListener) {
        this.mCheckBoxListener = mCheckBoxListener;
    }

    public ThreeStatusCheckBox(Context context) {
        this(context,null);
    }

    public ThreeStatusCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ThreeStatusCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThreeStatusCheckBox);
        mSize = typedArray.getDimensionPixelSize(R.styleable.ThreeStatusCheckBox_size,40);
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.ThreeStatusCheckBox_strokeWidth,4);
        mStrokeColor = typedArray.getColor(R.styleable.ThreeStatusCheckBox_strokeColor, Color.GREEN);
        mHalfSelectColor = typedArray.getColor(R.styleable.ThreeStatusCheckBox_halfSelect_color, Color.YELLOW);
        mAllSelectColor = typedArray.getColor(R.styleable.ThreeStatusCheckBox_allSelect_color, Color.RED);

        initPaint();
    }

    private void initPaint() {
        mStrokePaint = new Paint();
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStyle(Paint.Style.FILL);
        mStrokePaint.setAntiAlias(true);
        mStrokeRec = new RectF(0,0,mSize,mSize);

        mNoSelectPaint = new Paint();
        mNoSelectPaint.setColor(Color.WHITE);
        mNoSelectPaint.setStyle(Paint.Style.FILL);
        mNoSelectPaint.setAntiAlias(true);
        mNoSelectRec = new RectF(0 + mStrokeWidth, 0 + mStrokeWidth, mSize - mStrokeWidth, mSize - mStrokeWidth);

        mInnerPaint = new Paint();
        mInnerPaint.setColor(mAllSelectColor);
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setAntiAlias(true);
        mInnerRec = new RectF(0 + mStrokeWidth, 0 + mStrokeWidth, mSize - mStrokeWidth, mSize - mStrokeWidth);

        mTickBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tick);
        mTickBitmapRec = new Rect(0, 0, mTickBitmap.getWidth(), mTickBitmap.getHeight());
        mTickPaint = new Paint();
        mTickPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mStrokeRec,3,3,mStrokePaint);

        float checkProgress = progress < 0.5f ? 0.0f : (progress - 0.5f) / 0.5f;
        mNoSelectRec.set(mStrokeWidth+(mSize/2)*checkProgress,
                mStrokeWidth+(mSize/2)*checkProgress, mSize - mStrokeWidth-(mSize/2)*checkProgress,
                mSize - mStrokeWidth-(mSize/2)*checkProgress);
        canvas.drawRoundRect(mNoSelectRec,3,3,mNoSelectPaint);

        float p =1 -  checkProgress;
        mInnerRec.set(mStrokeWidth+(mSize/2)*p,
                mStrokeWidth+(mSize/2)*p, mSize - mStrokeWidth-(mSize/2)*p,
                mSize - mStrokeWidth-(mSize/2)*p);
        canvas.drawRoundRect(mInnerRec,3,3,mInnerPaint);

        canvas.drawBitmap(mTickBitmap,mTickBitmapRec,mInnerRec,mTickPaint);
    }

    private float progress;
    public void setProgress(float value) {
        if (progress == value) {
            return;
        }
        progress = value;
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    private void addAnim(float checkStatus){
        float progress;
        if (checkStatus == NO_SELECT){
            progress = 0.0f;
        }else {
            progress = 1.0f;
        }
        mInnerAnimator = ObjectAnimator.ofFloat(this,"progress",progress);
        mInnerAnimator.setDuration(500);
        mInnerAnimator.start();
    }

    public void setChecked(int checkedStatus) {
        if (attachedToWindow) {
            addAnim(checkedStatus);
        } else {
            cancelAnim();
            if (checkedStatus == NO_SELECT){
                setProgress(0.0f);
            }else {
                setProgress(1.0f);
            }
        }
        switch (checkedStatus){
            case NO_SELECT:
                selectStatus = 0;
                mNoSelectPaint.setColor(Color.WHITE);
                mStrokePaint.setColor(mStrokeColor);
                break;
            case HALF_SELECT:
                selectStatus = 1;
                mStrokePaint.setColor(mHalfSelectColor);
                mInnerPaint.setColor(mHalfSelectColor);
                break;
            case ALL_SELECT:
                selectStatus = 2;
                mStrokePaint.setColor(mAllSelectColor);
                mInnerPaint.setColor(mAllSelectColor);
                break;
        }
        mCheckBoxListener.CheckBoxStatusChange(selectStatus);
    }

    private void cancelAnim() {
        if (mInnerAnimator != null){
            mInnerAnimator.cancel();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attachedToWindow = false;
    }

    public interface OnThreeStatusCheckBoxChangeListener{
        void CheckBoxStatusChange(int checkStatus);
    }
}
