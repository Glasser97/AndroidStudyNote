package com.example.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class VoteButton extends View {
    private int width;
    private int height;
    private String mLeftString; // TODO: use a default from R.string...
    private String mRightString; // TODO: use a default from R.string...
    private int mTextColor = Color.WHITE;
    private int mLeftColor = Color.RED; // TODO: use a default from R.color...
    private int mRightColor = Color.BLUE; // TODO: use a default from R.color...

    private int mLeftNo;
    private int mRightNo;
    private float mTextSize = 12; // TODO: use a default from R.dimen...
    private float mSlashWidth = 5;
    private int middleWall;
    private VoteClickListener mVoteClickListener;

    private TextPaint mLeftTextPaint;
    private TextPaint mRightTextPaint;
    private Paint leftColorPaint;
    private Paint rightColorPaint;
    private float mLeftTextWidth;
    private float mLeftTextHeight;
    private float mRightTextWidth;
    private float mRightTextHeight;
    private float mSlashUnderWidth = 0;

    //定义绘制的左右Path和圆角
    private Path leftTriPath;
    private Path rightTriPath;
    private CornerPathEffect corner;

    //定义Path绘制圆的扇形区域
    RectF leftRectF;
    RectF rightRectF;

    //定义渐变色的shader
    LinearGradient leftGradient;
    LinearGradient rightGradient;
    int[] leftColors;
    int[] rightColors;
    float[] colorPositions;


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

        mLeftString = a.getString(R.styleable.VoteButton_leftString);
        mRightString = a.getString(R.styleable.VoteButton_rightString);
        mTextColor = a.getColor(R.styleable.VoteButton_textColor, mTextColor);
        mLeftColor = a.getColor(R.styleable.VoteButton_leftColor, mLeftColor);
        mRightColor = a.getColor(R.styleable.VoteButton_rightColor, mRightColor);
        mSlashUnderWidth = a.getDimension(R.styleable.VoteButton_slashUnderWidth, mSlashUnderWidth);
        mSlashWidth = a.getDimension(R.styleable.VoteButton_slashWidth,mSlashWidth);
        mTextSize = a.getDimension(R.styleable.VoteButton_textSize, mTextSize);

        a.recycle();

        // 初始化绘图Path，以及圆角
        leftTriPath = new Path();
        rightTriPath = new Path();
        corner = new CornerPathEffect(10);
        leftRectF = new RectF();
        rightRectF = new RectF();

        //初始化渐变颜色数组和渐变位置数组

        leftColors = new int[]{mLeftColor, brighterColor(mLeftColor,1.5f)};
        rightColors = new int[]{mRightColor, brighterColor(mRightColor,1.5f)};
        colorPositions = new float[]{0.3f,0.8f};


        // 初始化画笔
        mLeftTextPaint = new TextPaint();
        mLeftTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mLeftTextPaint.setTextAlign(Paint.Align.LEFT);
        mRightTextPaint = new TextPaint();
        mRightTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mRightTextPaint.setTextAlign(Paint.Align.RIGHT);
        leftColorPaint = new Paint();
        leftColorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        leftColorPaint.setColor(mLeftColor);
        leftColorPaint.setStyle(Paint.Style.FILL);
        leftColorPaint.setPathEffect(corner);
        rightColorPaint = new Paint();
        rightColorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        rightColorPaint.setColor(mRightColor);
        rightColorPaint.setStyle(Paint.Style.FILL);
        rightColorPaint.setPathEffect(corner);


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

    //测量控件的宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        height = measuredHeight(heightMeasureSpec);
        width = measureWidth(widthMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 在除了Exactly的模式下，认为空间的宽度为填满父控件的宽度
     * 高度如果为wrap，模式则为文字的高度加上35dp的高度
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        result = specSize;
        return result;
    }

    private int measuredHeight(int heightMeasureSpec){
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int)(mTextSize + getContext().getResources().getDimension(R.dimen.dp_20));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public void setOnVoteClickListener(VoteClickListener voteClickListener){
        this.mVoteClickListener = voteClickListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.TRANSPARENT);
        //canvas.drawColor(Color.WHITE);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int halfH = contentHeight/2;
        int rectWidth = contentWidth-contentHeight;
        int halfSlash = (int)(mSlashUnderWidth/2 + mSlashWidth/2);
//        canvas.drawCircle(paddingLeft+halfH,halfH+paddingTop,halfH,leftColorPaint);
//        canvas.drawCircle(getWidth()-paddingRight-halfH,paddingTop+halfH,halfH,rightColorPaint);

        //计算左边所占百分比
        int leftRectWidth = (int)(computeLength(mLeftNo,mRightNo) * rectWidth);
        //设置点击事件的中间分割线
        middleWall = paddingLeft+halfH+leftRectWidth;

        if(leftGradient == null){
            leftGradient = new LinearGradient(paddingLeft,paddingTop+halfH,
                    paddingLeft+halfH+leftRectWidth-halfSlash+mSlashUnderWidth, paddingTop,
                    leftColors,colorPositions, Shader.TileMode.CLAMP);
        }
        if(rightGradient == null){
            rightGradient = new LinearGradient(getWidth()-paddingRight,paddingTop+halfH,
                    paddingLeft+halfH+leftRectWidth+halfSlash-mSlashUnderWidth,paddingTop+contentHeight,
                    rightColors,colorPositions, Shader.TileMode.CLAMP);
        }

        //使用path画出左边的按钮
        leftRectF.set(paddingLeft,paddingTop,paddingLeft+contentHeight,paddingTop+contentHeight);
        leftTriPath.moveTo(paddingLeft+halfH+leftRectWidth-halfSlash,paddingTop);
        leftTriPath.lineTo(paddingLeft+halfH,paddingTop);
        leftTriPath.arcTo(leftRectF,270 ,-180,false);
        leftTriPath.lineTo(paddingLeft+halfH+leftRectWidth-halfSlash,paddingTop+contentHeight);
        leftTriPath.lineTo(paddingLeft+halfH+leftRectWidth-halfSlash+mSlashUnderWidth,paddingTop);
        leftTriPath.close();
        leftColorPaint.setShader(leftGradient);
        canvas.drawPath(leftTriPath,leftColorPaint);

        //使用path画出右边的按钮
        rightRectF.set(getWidth()-paddingRight-contentHeight,paddingTop,getWidth()-paddingRight,paddingTop+contentHeight);
        rightTriPath.moveTo(paddingLeft+halfH+leftRectWidth+halfSlash,paddingTop+contentHeight);
        rightTriPath.lineTo(getWidth()-paddingRight-halfH,paddingTop+contentHeight);
        rightTriPath.arcTo(rightRectF,90,-180,false);
        rightTriPath.lineTo(paddingLeft+halfH+leftRectWidth+halfSlash, paddingTop);
        rightTriPath.lineTo(paddingLeft+halfH+leftRectWidth+halfSlash-mSlashUnderWidth,paddingTop+contentHeight);
        rightTriPath.close();
        rightColorPaint.setShader(rightGradient);
        canvas.drawPath(rightTriPath,rightColorPaint);



        //绘制文字
        canvas.drawText(mLeftString, paddingLeft + halfH, paddingTop + halfH + mLeftTextHeight+6, mLeftTextPaint);
        canvas.drawText(mRightString,getWidth()-paddingRight-halfH,paddingTop + halfH + mLeftTextHeight+6,mRightTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int x = (int)event.getX();
                if (x < middleWall) {
                    mVoteClickListener.onClickLeft();
                } else if(x > middleWall){
                    mVoteClickListener.onClickRight();
                }
                //invalidate();
                break;
        }
        return true;
    }

    private int brighterColor(int color, float brighterK){
        int alpha = (color & 0xff000000);
        int red = (int)(((color & 0x00ff0000) >> 16) * brighterK);
        int green = (int)(((color & 0x0000ff00) >> 8) * brighterK);
        int blue = (int)((color & 0x000000ff) * brighterK);
        red = red > 255 ? 255:red;
        green = green > 255 ? 255:green;
        blue = blue > 255 ? 255:blue;
        return alpha + (red << 16) + (green << 8) + blue;
    }
    private float computeLength(int leftNo, int rightNo){
        float half = 1.0f/2.0f;
        if(leftNo == 0 || rightNo == 0){
            return half;
        }else{
            return (float) leftNo/(float) (leftNo+rightNo);
        }
    }

    public int getmLeftNo() {
        return mLeftNo;
    }

    public int setmLeftNo(int mLeftNo) {
        this.mLeftNo = mLeftNo;
        return this.mLeftNo;
    }

    public int getmRightNo() {
        return mRightNo;
    }

    public int setmRightNo(int mRightNo) {
        this.mRightNo = mRightNo;
        return this.mRightNo;
    }

    public void setmSlashUnderWidth(float mSlashUnderWidth) {
        this.mSlashUnderWidth = mSlashUnderWidth;
    }

    public float getmSlashUnderWidth() {
        return mSlashUnderWidth;
    }

    public interface VoteClickListener{
        void onClickLeft();
        void onClickRight();
    }
}
