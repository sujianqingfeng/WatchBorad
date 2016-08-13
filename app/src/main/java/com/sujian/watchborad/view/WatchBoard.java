package com.sujian.watchborad.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sujian.watchborad.R;

import java.util.Calendar;

/**
 * Created by sujian on 2016/8/13.
 * Mail:121116111@qq.com
 */
public class WatchBoard extends View {
    private float mRadius;
    private float mPadding;
    private float mTextSize;
    private float mHourPointWidth;
    private float mMinutePointWidth;
    private float mSecondPointWidth;
    private int mPointRadius;
    private float mPointEndLength;

    private int mColorLong;
    private int mColorShort;
    private int mHourPointColor;
    private int mMinutePointColor;
    private int mSecondPointColor;

    private Paint paint;

    public WatchBoard(Context context) {
        this(context,null);
    }

    public WatchBoard(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WatchBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttr(attrs);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width=1000;

        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode==MeasureSpec.AT_MOST||widthMode==MeasureSpec.UNSPECIFIED||heightMeasureSpec==MeasureSpec.AT_MOST||heightMeasureSpec==MeasureSpec.UNSPECIFIED){
            try {
                throw new NoDetermineSizeException("必须有一个确定值");
            } catch (NoDetermineSizeException e) {
                e.printStackTrace();
            }
        }else {
            if (widthMode==MeasureSpec.EXACTLY){
                width=Math.min(widthSize,width);
            }
            if (heightMode==MeasureSpec.EXACTLY){
                width=Math.min(heightSize,width);
            }
        }

        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius=(Math.min(w,h)-mPadding)/2;
        mPointEndLength=mRadius/6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        canvas.translate(getWidth()/2,getHeight()/2);
        paintCircle(canvas);
        paintScale(canvas);
        paintPointer(canvas);
        canvas.restore();
        postInvalidateDelayed(1000);
    }

    private void paintPointer(Canvas canvas) {
        canvas.translate(getWidth() / 2, getHeight() / 2);

        Calendar c=Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        int second=c.get(Calendar.SECOND);

        int angleHour = (hour % 12) * 360 / 12;
        int angleMinute = minute * 360 / 60;
        int angleSecond = second * 360 / 60;

        canvas.save();
        canvas.rotate(angleHour);
        RectF rectHour=new RectF(-mHourPointWidth/2,-mRadius / 2,mHourPointWidth/2,mPointEndLength);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mHourPointWidth);
        canvas.drawRoundRect(rectHour,mPointRadius,mPointRadius,paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(angleMinute);
        RectF rectFMinute = new RectF(-mMinutePointWidth / 2, -mRadius * 3.5f / 5, mMinutePointWidth / 2, mPointEndLength);
        paint.setColor(mMinutePointColor);
        paint.setStrokeWidth(mMinutePointWidth);
        canvas.drawRoundRect(rectFMinute, mPointRadius, mPointRadius, paint);
        canvas.restore();


        canvas.save();
        canvas.rotate(angleSecond);
        RectF rectFSecond = new RectF(-mSecondPointWidth / 2, -mRadius + 15, mSecondPointWidth / 2, mPointEndLength);
        paint.setColor(mSecondPointColor);
        paint.setStrokeWidth(mSecondPointWidth);
        canvas.drawRoundRect(rectFSecond, mPointRadius, mPointRadius, paint);
        canvas.restore();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mSecondPointColor);
        canvas.drawCircle(0, 0, mSecondPointWidth * 4, paint);
    }

    private void paintScale(Canvas canvas) {
        paint.setStrokeWidth(dp2px(1));
        int lineWidth=0;
        for (int i = 0; i <60 ; i++) {
            if (i%5==0){
                paint.setStrokeWidth(UiUitls.dip2px(getContext(),1.5f));
                paint.setColor(mColorLong);
                lineWidth=40;
                //绘制文字
                paint.setTextSize(mTextSize);
                paint.setColor(Color.BLACK);
                String text=((i/5)==0?12:(i/5))+"";
                Rect bound=new Rect();
                paint.getTextBounds(text,0,text.length(),bound);

                canvas.save();
                canvas.translate(0,-mRadius+lineWidth+dp2px(10)+(bound.bottom-bound.top));
                canvas.rotate(-i*6);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText(text,-(bound.right-bound.left)/2,bound.bottom+10,paint);
                canvas.restore();
            }else {
                paint.setStrokeWidth(dp2px(1));
                paint.setColor(mColorShort);
                lineWidth=30;
            }
            canvas.drawLine(0,-mRadius+dp2px(10),0,-mRadius+dp2px(10)+lineWidth,paint);
            canvas.rotate(6);
        }
        canvas.restore();
    }

    private void paintCircle(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0,0,mRadius,paint);
    }

    private void init() {
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
    }


    private void obtainStyledAttr(AttributeSet set){
        TypedArray typedArray=null;
        try {
            typedArray=getContext().obtainStyledAttributes(set, R.styleable.WatchBoard);

            mPadding=typedArray.getDimension(R.styleable.WatchBoard_wb_padding,dp2px(10));
            mTextSize=typedArray.getDimension(R.styleable.WatchBoard_wb_text_size,sp2px(16));
            mHourPointWidth=typedArray.getDimension(R.styleable.WatchBoard_wb_hour_point_width,dp2px(5));
            mMinutePointWidth=typedArray.getDimension(R.styleable.WatchBoard_wb_minute_point_width,dp2px(3));
            mSecondPointWidth=typedArray.getDimension(R.styleable.WatchBoard_wb_second_point_width,dp2px(2));
            mPointEndLength=typedArray.getDimension(R.styleable.WatchBoard_wb_point_end_length,dp2px(10));
            mPointRadius=(int)typedArray.getDimension(R.styleable.WatchBoard_wb_point_corner_radius,dp2px(10));

            mHourPointColor=typedArray.getColor(R.styleable.WatchBoard_wb_hour_point_color, Color.BLACK);
            mMinutePointColor=typedArray.getColor(R.styleable.WatchBoard_wb_minute_point_color,Color.BLACK);
            mSecondPointColor=typedArray.getColor(R.styleable.WatchBoard_wb_second_point_color,Color.BLUE);
            mColorLong=typedArray.getColor(R.styleable.WatchBoard_wb_scale_long_color,Color.argb(225,0,0,0));
            mColorShort=typedArray.getColor(R.styleable.WatchBoard_wb_scale_short_color,Color.argb(115,0,0,0));
        } catch (Exception e) {
            e.printStackTrace();
            mPadding = dp2px(10);
            mTextSize = sp2px(16);
            mHourPointWidth = dp2px(5);
            mMinutePointWidth = dp2px(3);
            mSecondPointWidth = dp2px(2);
            mPointRadius = (int) dp2px(10);
            mPointEndLength = dp2px(10);

            mColorLong = Color.argb(225, 0, 0, 0);
            mColorShort = Color.argb(125, 0, 0, 0);
            mMinutePointColor = Color.BLACK;
            mSecondPointColor = Color.RED;
        } finally {
            if (typedArray!=null)
                typedArray.recycle();
        }
    }

    private float dp2px(int i){
        return UiUitls.dip2px(getContext(),i);
    }

    private float sp2px(int i){
        return UiUitls.sp2px(getContext(),i);
    }

    class NoDetermineSizeException extends Exception{
        public NoDetermineSizeException(String message){
            super(message);
        }
    }

}
