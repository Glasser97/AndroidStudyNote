package com.example.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class VoteButton extends View {
    private int width;
    private int height;
    private String mLeftString; // TODO: use a default from R.string...
    private String mRightString; // TODO: use a default from R.string...
    private int mTextColor = Color.WHITE;
    private int mLeftColor = Color.RED; // TODO: use a default from R.color...
    private int mRightColor = Color.BLUE; // TODO: use a default from R.color...

    private int mLeftNo = 0;
    private int mRightNo = 0;
    private float mTextSize = 0; // TODO: use a default from R.dimen...
    private float mSlashWidth = 5;
    private Drawable mExampleDrawable;

    private TextPaint mLeftTextPaint;
    private TextPaint mRightTextPaint;
    private float mLeftTextWidth;
    private float mLeftTextHeight;
    private float mRightTextWidth;
    private float mRightTextHeight;
    private float mSlashUnderWidth = 0;
    private float mXRadius = 30;
    private float mYRadius = 30;

    //装载按钮控件的区域
    private RectF rectF = new RectF();



    public VoteButton(Context context) {
        super(context);
        init(null, 0);
    }

    public VoteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public VoteButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.VoteButton, defStyle, 0);

        mLeftString = a.getString(
                R.styleable.VoteButton_leftString);
        mRightString = a.getString(
                R.styleable.VoteButton_rightString);
        mTextColor = a.getColor(R.styleable.VoteButton_textColor,
                mTextColor);
        mLeftColor = a.getColor(R.styleable.VoteButton_leftColor,
                mLeftColor);
        mRightColor = a.getColor(R.styleable.VoteButton_rightColor,
                mRightColor);
        mSlashUnderWidth = a.getDimension(R.styleable.VoteButton_slashUnderWidth,
                mSlashUnderWidth);
        mSlashWidth = a.getDimension(R.styleable.VoteButton_slashWidth,mSlashWidth);
        mXRadius = a.getDimension(R.styleable.VoteButton_XRadius,mXRadius);
        mYRadius = a.getDimension(R.styleable.VoteButton_YRadius, mYRadius);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mTextSize = a.getDimension(
                R.styleable.VoteButton_textSize,
                mTextSize);
        rectF.left = 0;
        rectF.top = 0;
//        rectF.right = width;
//        rectF.


//        if (a.hasValue(R.styleable.VoteButton_exampleDrawable)) {
//            mExampleDrawable = a.getDrawable(
//                    R.styleable.VoteButton_exampleDrawable);
//            mExampleDrawable.setCallback(this);
//        }

        a.recycle();

        // Set up a default TextPaint object
        mLeftTextPaint = new TextPaint();
        mLeftTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mLeftTextPaint.setTextAlign(Paint.Align.LEFT);
        mRightTextPaint = new TextPaint();
        mRightTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mRightTextPaint.setTextAlign(Paint.Align.RIGHT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mLeftTextPaint.setTextSize(mTextSize);
        mLeftTextPaint.setColor(mTextColor);
        mRightTextPaint.setTextSize(mTextSize);
        mRightTextPaint.setColor(mTextColor);
        mLeftTextWidth = mLeftTextPaint.measureText(mLeftString);
        mRightTextWidth = mRightTextPaint.measureText(mRightString);
        Paint.FontMetrics leftFontMetrics = mLeftTextPaint.getFontMetrics();
        mLeftTextHeight = leftFontMetrics.bottom;
        Paint.FontMetrics rightFontMetrics = mRightTextPaint.getFontMetrics();
        mRightTextHeight = rightFontMetrics.bottom;

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
//        height = measuredHeight(heightMeasureSpec);
//        width = measureWidth(widthMeasureSpec);
//        setMeasureDimension(width, height);
//    }
//
//    private int measureWidth(int widthMeasureSpec) {
//        int result;
//        int specMode = MeasureSpec.getMode(widthMeasureSpec);
//        int specSize = MeasureSpec.getSize(widthMeasureSpec);
//        if (specMode == MeasureSpec.EXACTLY) {
//            result = specSize;
//        } else {//这是wrap的模式，给一个固定大小
////            result = (int) getContext().getResources().getDimension(R.dimen.dp_150);
//            result = buttonString.length() * mTextSize + height * 5 / 3;
//            if (specMode == MeasureSpec.AT_MOST) {
//                result = Math.min(result, specSize);
//            }
//        }
//        return result;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mLeftString,
                paddingLeft + (contentWidth - mLeftTextWidth) / 2,
                paddingTop + (contentHeight + mLeftTextHeight) / 2,
                mLeftTextPaint);
        //canvas.drawRoundRect(rectF,);
        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }


    public int getmLeftNo() {
        return mLeftNo;
    }

    public void setmLeftNo(int mLeftNo) {
        this.mLeftNo = mLeftNo;
    }

    public int getmRightNo() {
        return mRightNo;
    }

    public void setmRightNo(int mRightNo) {
        this.mRightNo = mRightNo;
    }


    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
