package me.wsj.fengyun.view.skyview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;

import me.wsj.fengyun.R;
import per.wsj.commonlib.utils.BitmapUtil;
import per.wsj.commonlib.utils.DisplayUtil;
import per.wsj.commonlib.utils.LogUtil;

public class SunView extends View {

    /**
     * view宽度/2
     */
    private int mHalfWidth;
    /**
     * 离顶部的高度
     */
    private int marginTop;
    private int mCircleColor;  //圆弧颜色
    private int mFontColor;  //字体颜色
    private int mRadius;  //圆的半径

    private float mTotalMinute; //总时间(日落时间减去日出时间的总分钟数)
    private float mNeedMinute; //当前时间减去日出时间后的总分钟数
    private float mPercentage; //根据所给的时间算出来的百分占比
    private float positionX, positionY; //太阳图片的x、y坐标
    private float mFontSize;  //字体大小

    private String mStartTime; //开始时间(日出时间)
    private String mEndTime; //结束时间（日落时间）
    private String mCurrentTime; //当前时间

    private int lineBias;

    /**
     * 日出日落等文字画笔
     */
    private Paint mTextPaint;
    /**
     * 日出日落时间画笔
     */
    private Paint mTimePaint;

    /**
     * 底部线
     */
    private Paint mLinePaint;

    /**
     * 太阳轨迹画笔
     */
    private Paint mPathPaint;

    private Paint mCirclePaint;

    private Paint shadePaint;

    /**
     * 圆弧所在矩形
     */
    private RectF mRectF = new RectF();

    private Context mContext;

    private float endHour;

    private boolean isSun = true;

    private Bitmap mSunIcon; //太阳图片

    private int iconSize;


    public SunView(Context context) {
        this(context, null);
    }

    public SunView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        mContext = context;
        marginTop = DisplayUtil.dp2px(12);

        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.SunView);
        mCircleColor = type.getColor(R.styleable.SunView_sun_circle_color, getContext().getResources().getColor(R.color.sun_line_color));
        mFontColor = type.getColor(R.styleable.SunView_sun_font_color, getContext().getResources().getColor(R.color.colorAccent));
        mRadius = (int) type.getDimension(R.styleable.SunView_sun_circle_radius, 75);
        mFontSize = type.getDimension(R.styleable.SunView_sun_font_size, 13);

        isSun = type.getBoolean(R.styleable.SunView_type, true);
        type.recycle();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mFontColor);
        mTextPaint.setTextSize(mFontSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimePaint.setColor(getResources().getColor(R.color.air_text_common_light));
        mTimePaint.setTextSize(mFontSize);
        mTimePaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setDither(true);// 防止抖动
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setColor(mContext.getResources().getColor(R.color.color_ccc));

        // 渐变遮罩的画笔
        shadePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadePaint.setColor(mContext.getResources().getColor(R.color.back_white));
        shadePaint.setStyle(Paint.Style.FILL);

        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setColor(mContext.getResources().getColor(R.color.attention_text_light));
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(3);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(mCircleColor);

        iconSize = DisplayUtil.dp2px(18);
        if (isSun) {
            mSunIcon = BitmapUtil.compressBySize(getContext(), R.drawable.icon_sun, iconSize, iconSize);
        } else {
            mSunIcon = BitmapUtil.compressBySize(getContext(), R.drawable.icon_moon, iconSize, iconSize);
        }

        lineBias = DisplayUtil.dp2px(10);
    }

    public void setTimes(String startTime, String endTime, String currentTime) {
        mStartTime = startTime;
        mEndTime = endTime;
        mCurrentTime = currentTime;

        String[] currentTimes = currentTime.split(":");
        String[] startTimes = startTime.split(":");
        String[] endTimes = endTime.split(":");
        float currentHour = Float.parseFloat(currentTimes[0]);
        float currentMinute = Float.parseFloat(currentTimes[1]);

        float startHour = Float.parseFloat(startTimes[0]);
        endHour = Float.parseFloat(endTimes[0]);
        if (!isSun && endHour < startHour) {
            endHour += 24;
        }
        float endMinute = Float.parseFloat(endTimes[1]);

        if (isSun) {
            if (currentHour > endHour) {
                mCurrentTime = endTime;
            } else if (currentHour == endHour && currentMinute >= endMinute) {
                mCurrentTime = endTime;
            }
        }

        mTotalMinute = calculateTime(mStartTime, mEndTime, false);//计算总时间，单位：分钟
        mNeedMinute = calculateTime(mStartTime, mCurrentTime, true);//计算当前所给的时间 单位：分钟
        mPercentage = Float.parseFloat(formatTime(mTotalMinute, mNeedMinute));//当前时间的总分钟数占日出日落总分钟数的百分比
//        LogUtil.d("percentage: " + mPercentage);
        float currentAngle = 180 * mPercentage;
//        LogUtil.d("currentAngle: " + currentAngle);

        setAnimation(0, currentAngle, 3000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mHalfWidth = getMeasuredWidth() >> 1;
        // start x of sun
        positionX = mHalfWidth - mRadius - DisplayUtil.dp2px(9);
        // start y of sun
        positionY = mRadius + marginTop - (mSunIcon.getHeight() >> 1);

        mRectF.set(mHalfWidth - mRadius, marginTop, mHalfWidth + mRadius, mRadius * 2 + marginTop);

        int height = mRadius + marginTop + DisplayUtil.dp2px(40);

        setMeasuredDimension(getMeasuredWidth(), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // step1：draw half circle
        canvas.drawArc(mRectF, 180, 180, false, mCirclePaint);

        // step2：draw bottom line
        canvas.drawLine(mHalfWidth - mRadius - lineBias, mRadius + marginTop, mHalfWidth + mRadius + lineBias, mRadius + marginTop, mLinePaint);

        // step3：draw text
        drawText(canvas);

        // step4：draw sun/moon
        canvas.save();
        canvas.rotate(mCurrentAngle, mHalfWidth, mRadius + marginTop);
        canvas.drawBitmap(mSunIcon, positionX, positionY, mPathPaint);
        canvas.restore();
    }

    /**
     * 绘制底部左右边的日出时间和日落时间
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {

        String startTime = TextUtils.isEmpty(mStartTime) ? "" : mStartTime;
        String endTime = TextUtils.isEmpty(mEndTime) ? "" : mEndTime;
        String sunrise = "日出";
        String sunset = "日落";
        if (!isSun) {
            sunrise = "月出";
            sunset = "月落";
        }
        int dp8 = DisplayUtil.dp2px(8);

        canvas.drawText(sunrise, mHalfWidth - mRadius + dp8, mRadius + DisplayUtil.dp2px(21) + marginTop, mTextPaint);
        canvas.drawText(startTime, mHalfWidth - mRadius + dp8, mRadius + DisplayUtil.dp2px(37) + marginTop, mTimePaint);
        canvas.drawText(sunset, mHalfWidth + mRadius - dp8, mRadius + DisplayUtil.dp2px(21) + marginTop, mTextPaint);
        canvas.drawText(endTime, mHalfWidth + mRadius - dp8, mRadius + DisplayUtil.dp2px(37) + marginTop, mTimePaint);
    }

    /**
     * 根据日出和日落时间计算出一天总共的时间:单位为分钟
     *
     * @param startTime 日出时间
     * @param endTime   日落时间
     * @return
     */
    private float calculateTime(String startTime, String endTime, boolean isCurrent) {
        String[] startTimes = startTime.split(":");
        String[] endTimes = endTime.split(":");
        float startHour = Float.parseFloat(startTimes[0]);
        float startMinute = Float.parseFloat(startTimes[1]);

        float endHour = Float.parseFloat(endTimes[0]);
        float endMinute = Float.parseFloat(endTimes[1]);

        if (!isCurrent && !isSun && endHour < startHour) {
            endHour += 24;
        }

        if (isSun) {
            if (startHour > endHour) {
                return 0;
            } else if (startHour == endHour && startMinute >= endMinute) {
                return 0;
            }
        } else {
            if (isCurrent) {
                if (startHour > endHour) {
                    return 0;
                } else if (startHour == endHour && startMinute >= endMinute) {
                    return 0;
                }
            } else {
                if (startHour >= endHour + 24) {
                    return 0;
                }
            }
        }

        if (checkTime(startTime, endTime)) {
            return (endHour - startHour - 1) * 60 + (60 - startMinute) + endMinute;
        }
        return 0;
    }

    /**
     * 对所给的时间做一下简单的数据校验
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private boolean checkTime(String startTime, String endTime) {
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)
                || !startTime.contains(":") || !endTime.contains(":")) {
            return false;
        }

        String[] startTimes = startTime.split(":");
        String[] endTimes = endTime.split(":");
        float startHour = Float.parseFloat(startTimes[0]);
        float startMinute = Float.parseFloat(startTimes[1]);

        float endHour = Float.parseFloat(endTimes[0]);
        float endMinute = Float.parseFloat(endTimes[1]);

        //如果所给的时间(hour)小于日出时间（hour）或者大于日落时间（hour）
        if ((startHour < Float.parseFloat(mStartTime.split(":")[0]))
                || (endHour > this.endHour)) {
            return false;
        }

        //如果所给时间与日出时间：hour相等，minute小于日出时间
        if ((startHour == Float.parseFloat(mStartTime.split(":")[0]))
                && (startMinute < Float.parseFloat(mStartTime.split(":")[1]))) {
            return false;
        }

        //如果所给时间与日落时间：hour相等，minute大于日落时间
        if ((startHour == this.endHour)
                && (endMinute > Float.parseFloat(mEndTime.split(":")[1]))) {
            return false;
        }

        if (startHour < 0 || endHour < 0
                || startHour > 23 || endHour > 23
                || startMinute < 0 || endMinute < 0
                || startMinute > 60 || endMinute > 60) {
            return false;
        }
        return true;
    }

    /**
     * 根据具体的时间、日出日落的时间差值 计算出所给时间的百分占比
     *
     * @param totalTime 日出日落的总时间差
     * @param needTime  当前时间与日出时间差
     * @return
     */
    private String formatTime(float totalTime, float needTime) {
        if (totalTime == 0)
            return "0.00";
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//保留2位小数，构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(needTime / totalTime);//format 返回的是字符串
    }

    private float mCurrentAngle = 0;

    private void setAnimation(float startAngle, float currentAngle, int duration) {
        ValueAnimator sunAnimator = ValueAnimator.ofFloat(startAngle, currentAngle);
        sunAnimator.setDuration(duration);
        sunAnimator.addUpdateListener(animation -> {
            //每次要绘制的圆弧角度
            mCurrentAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        sunAnimator.start();
    }
}