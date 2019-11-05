package com.example.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;


/**
 * TODO: document your custom view class.
 */
public class RunText extends SurfaceView implements SurfaceHolder.Callback {
    public static String TAG = "SurfaceView";
    private String runTextString; // TODO: use a default from R.string...
    private int runTextColor = Color.RED; // TODO: use a default from R.color...
    private float runTextSize = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public  Context mContext;
    private int mBackgroundColor = Color.WHITE;
    private boolean mIsReapeat;
    /*
    开始滚动的位置，0为从最左边开始，1为从最右边开始。
     */
    private int mStartPoint;
    /*
    滚动的方向,0为向左滚动，1为向右滚动。
     */
    private int mDirection;
    /*
    滚动的速度
     */
    private int mSpeed;
    /*
    滚动开始等待时间,单位为0.1秒
     */
    private int mWaitTime;

    private SurfaceHolder holder;
    private RunTextThread mThread;
    private int textWidth=0,textHeight=0;
    private int ShadowColor = Color.BLACK;
    /*
    当前X的位置
     */
    public int currentX=0;
    /*
    每一步滚动的距离
     */
    public int sepX=5;

    RunTextHandler mHandler = new RunTextHandler(this);

    public RunText(Context context) {
        this(context,null);
    }

    public RunText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RunText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext=context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RunText, defStyle, 0);

        runTextString = a.getString(
                R.styleable.RunText_exampleString);
        runTextColor = a.getColor(
                R.styleable.RunText_runTextColor,
                runTextColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        runTextSize = a.getDimension(
                R.styleable.RunText_runTextSize,
                48);

        mIsReapeat = a.getBoolean(R.styleable.RunText_isRepeat,false);
        mSpeed = a.getInteger(R.styleable.RunText_speed,20);
        mDirection = a.getInteger(R.styleable.RunText_textDirection,0);
        mStartPoint = a.getInteger(R.styleable.RunText_startPoint,0);
        mWaitTime = a.getInteger(R.styleable.RunText_waitTime,0);
        mBackgroundColor = a.getColor(R.styleable.RunText_backgroundColor,Color.WHITE);

        a.recycle();
        //添加监听
        holder = this.getHolder();
        holder.addCallback(this);

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        //invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(runTextSize);
        mTextPaint.setColor(runTextColor);
        mTextWidth = mTextPaint.measureText(runTextString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        int width = size.x;

        if(mStartPoint==0){
            currentX = 0;
        }else{
            currentX = width-getPaddingLeft()-getPaddingRight();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder){
        Log.e(TAG,"surfaceCreated");
        this.holder = surfaceHolder;

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder,int i, int i1, int i2){
        Log.e(TAG,"surfaceChanged");
        if (mThread != null){
            mThread.isRun = true;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){
        Log.e(TAG,"surfaceDestroyed");
        if (mThread!=null){
            mThread.isRun = false;
        }

    }

    /**
     * 开始滚动
     */
    public void startScroll(){
        if(mThread!=null && mThread.isRun){
            return;
        }
        mThread = new RunTextThread(holder);
        mThread.start();
    }

    /**
     * 停止滚动
     */
    public void stopScroll(){
        if(mThread!=null){
            mThread.isRun = false;
            mThread.interrupt();
        }
        mThread = null;
    }

    /**
     * 定制一个控制滚动的线程
     */
    class RunTextThread extends Thread{
        private SurfaceHolder holder;
        //判断是否在运行
        public boolean isRun;

        public RunTextThread(SurfaceHolder holder){
            this.holder = holder;
            //在控制动画的线程中持有holder的引用，用于控制View
            isRun = true;
        }
        public void doDraw(){
            try{
                synchronized (holder){
                    if(runTextString.length()==0){
                       Thread.sleep(1000);
                       return;
                    }
                    //获取绘制的Canvas
                    Canvas canvas = holder.lockCanvas();
                    int paddingLeft = getPaddingLeft();
                    int paddingTop = getPaddingTop();
                    int paddingRight = getPaddingRight();
                    int paddingBottom = getPaddingBottom();
                    int startWidth=getWidth() - paddingLeft - paddingRight;
                    int contentWidth = (int)mTextWidth - paddingLeft - paddingRight;
                    int contentHeight = getHeight() - paddingTop - paddingBottom;
                    //取得纵轴中心线
                    int centerYLine = paddingTop + contentHeight / 2;
                    //向左移动
                    if(mDirection==0){
                        if(currentX<=-mTextWidth){
                            if(!mIsReapeat){
                                mHandler.sendEmptyMessage(ROLL_OVER);
                            }
                            currentX = startWidth;
                        }else{
                            currentX-=sepX;
                        }
                    }else{
                        if(currentX>=contentWidth){
                            if(!mIsReapeat){
                                mHandler.sendEmptyMessage(ROLL_OVER);
                            }
                            currentX =- textWidth;
                        }else{
                            currentX += sepX;
                        }
                    }
                    if(canvas!=null){
                        canvas.drawColor(mBackgroundColor);
                        canvas.drawText(runTextString,currentX,centerYLine+dip2px(getContext(),mTextHeight)/2,mTextPaint);
                        holder.unlockCanvasAndPost(canvas);//结束锁定canvas，提交改变

                        int a=textWidth/runTextString.trim().length();
                        int b=a/sepX;
                        int c = mSpeed/b == 0 ? 1:mSpeed/b;

                        Thread.sleep(c);//睡眠移动的频率

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            while(isRun){
                doDraw();
            }
        }
    }
    /*
    使用静态内部类防止内存泄漏
     */
    public static final int ROLL_OVER=100;

    public static class RunTextHandler extends Handler{
        private final WeakReference<RunText> mRunText;

        public RunTextHandler(RunText runText){
            mRunText = new WeakReference<>(runText);
        }

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what){
                case ROLL_OVER:
                    if(mRunText.get() != null){
                        mRunText.get().stopScroll();
                    }
                    if(mOnRunTextListener!=null){
                        mOnRunTextListener.onRollOver();
                    }
                    break;
            }
        }
    }



    public interface OnRunTextListener{
        void onRollOver();
        //滚动完毕
    }

    static OnRunTextListener mOnRunTextListener;
    public void setOnRunTextListener(OnRunTextListener mOnRunTextListener){
        this.mOnRunTextListener = mOnRunTextListener;
    }


    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getRunTextString() {
        return runTextString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param runTextStr The example string attribute value to use.
     */
    public void setRunTextString(String runTextStr) {
        runTextString = runTextStr;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getRunTextColor() {
        return runTextColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param textColor The example color attribute value to use.
     */
    public void setRunTextColor(int textColor) {
        runTextColor = textColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getRunTextSize() {
        return runTextSize;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param runTextSize1 The example dimension attribute value to use.
     */
    public void setRunTextSize(float runTextSize1) {
        runTextSize = runTextSize1;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * dip转换为px
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void reset(){
        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if(mStartPoint==0)
            currentX=0;
        else
            currentX=contentWidth;
    }

}
