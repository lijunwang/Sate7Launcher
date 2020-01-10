package com.sate7.sate7launcher.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.sate7.sate7launcher.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//English and china
//no time init;
public class CalendarDateView extends View {
    private final String TAG = "CalendarDateView";

    public CalendarDateView(Context context) {
        super(context);
    }

    public CalendarDateView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mPaintCircle.setColor(Color.RED);
        mPaintCircle.setStrokeWidth(STROKE_WIDTH);
        mPaintCircle.setStyle(Paint.Style.STROKE);

        mTimeChangedFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mTimeChangedFilter.addAction(Intent.ACTION_TIME_TICK);
        mTimeChangedFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        mTimeChangedFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        getContext().registerReceiver(mTimeChangedReceiver, mTimeChangedFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mTimeChangedReceiver);
    }

    private int mCenterX;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int STROKE_WIDTH = 4;
    private Rect mRect = new Rect();
    private int mCurrentY;
    private final int TITLE_MARGIN_TOP_BOTTOM = 20;
    private final int TEXT_SIZE_TITLE = 30;
    private final int SPLIT_LINE_HEIGHT = 5;
    private final int DAY_MARGIN_TOP_BOTTOM = 30;
    private final int TEXT_SIZE_DAY = 140;
    private final boolean DEBUG = false;
    private final int TEXT_SIZE_WEEK = 15;
    private final int WEEK_MARGIN_LEFT = 10;
    private final int LUNAR_MARGIN_BOTTOM = 10;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCenterX = canvas.getWidth() / 2;
        Log.d(TAG, "onDraw ... " + canvas.getWidth() + "," + canvas.getHeight());
        drawTopDate(canvas);
        drawSplitLine(canvas);
        drawDay(canvas);
        drawWeek(canvas);
        drawLunar(canvas);
    }

    private void drawTopDate(Canvas canvas) {
        String title = new SimpleDateFormat(getResources().getString(R.string.date_title)).format(new Date());
        Log.d(TAG, "title :" + title);
        mPaint.setTextSize(TEXT_SIZE_TITLE);
        mPaint.setColor(Color.WHITE);
        mPaint.getTextBounds(title, 0, title.length(), mRect);
        int titleLeft = mCenterX - mRect.width() / 2;
        //bottom of title
        mCurrentY = TITLE_MARGIN_TOP_BOTTOM + mRect.height();
        canvas.drawText(title, 0, title.length(), titleLeft, mCurrentY, mPaint);
    }

    private void drawSplitLine(Canvas canvas) {
        mCurrentY += TITLE_MARGIN_TOP_BOTTOM;
        canvas.drawLine(0, mCurrentY, canvas.getWidth(), mCurrentY + SPLIT_LINE_HEIGHT, mPaint);
    }

    private void drawDay(Canvas canvas) {
        String day = new SimpleDateFormat("d").format(new Date());
        Log.d(TAG, "drawDay :" + day);
        mPaint.setTextSize(TEXT_SIZE_DAY);
        mPaint.getTextBounds(day, 0, day.length(), mRect);
        mCurrentY += DAY_MARGIN_TOP_BOTTOM;
        int dayLeft = mCenterX - mRect.width() / 2;
        canvas.drawText(day, 0, day.length(), dayLeft, mCurrentY + mRect.height(), mPaint);
        mCurrentY += mRect.height();
    }

    private void drawWeek(Canvas canvas) {
        mCurrentY += DAY_MARGIN_TOP_BOTTOM;
        String[] week = getResources().getStringArray(R.array.week_lunar);
        Log.d(TAG, "week ..." + Arrays.toString(week) + "," + mCurrentY);
        int leftSpaceCenterY = (canvas.getHeight() - mCurrentY) / 2 + mCurrentY;
        mPaint.setTextSize(TEXT_SIZE_WEEK);
        if (DEBUG) {
            canvas.drawCircle(mCenterX, leftSpaceCenterY, 5, mPaint);
        }
        int totalWidthOfWeek = 0;
        for (String s : week) {
            mPaint.getTextBounds(s, 0, s.length(), mRect);
            totalWidthOfWeek += mRect.width();
        }
        int space = (canvas.getWidth() - totalWidthOfWeek - WEEK_MARGIN_LEFT * 2) / week.length;
        int i = 0;
        int rightMove = 0;
        int heightWeek = mRect.height();
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int bottom;
        if (isZh()) {
            bottom = mCurrentY + heightWeek / 2;
        } else {
            bottom = leftSpaceCenterY + heightWeek / 2;
        }
        for (String s : week) {
            mPaint.getTextBounds(s, 0, s.length(), mRect);
            mPaint.setTextSize(TEXT_SIZE_WEEK);
            rightMove += mRect.width();
            int left = rightMove + WEEK_MARGIN_LEFT + space * i - mRect.width() / 2;
            canvas.drawText(s, left, bottom, mPaint);
            i++;
            //select current day;
            if (isZh() && i == dayOfWeek - 1) {
//                int radio = Math.max(mRect.width(), mRect.height())/* + Math.min(mRect.width(), mRect.height()*/;
//                canvas.drawCircle(left + mRect.width() / 2, bottom - heightWeek / 2, radio, mPaintCircle);
                canvas.drawRect(new Rect(left - 5, bottom - heightWeek - 5, left + mRect.width() + 5, bottom + 5), mPaintCircle);
            } else if (!isZh() && i == dayOfWeek) {
//                int radio = Math.min(mRect.width(), heightWeek)/* + Math.min(mRect.width(), heightWeek)*/;
//                canvas.drawCircle(left + mRect.width() / 2, bottom - heightWeek / 2, radio, mPaintCircle);
                canvas.drawRect(new Rect(left - 5, bottom - heightWeek - 5, left + mRect.width() + 5, bottom + 5), mPaintCircle);
            }
            if (DEBUG) {
                mPaintCircle.setColor(Color.RED);
                mPaintCircle.setStyle(Paint.Style.STROKE);
                int radio = (Math.max(mRect.width(), mRect.height()) + Math.min(mRect.width(), mRect.height())) / 2;
                canvas.drawCircle(left + mRect.width() / 2, bottom - heightWeek / 2, radio, mPaint);
                canvas.drawRect(new Rect(left, bottom - heightWeek, left + mRect.width(), bottom), mPaint);
            }
        }

        mCurrentY += heightWeek;
    }

    private void drawLunar(Canvas canvas) {
        if (isZh()) {
            Solar solar = new Solar();
            String[] solarInfo = new SimpleDateFormat("yyyy-M-d").format(new Date()).split("-");
            solar.solarYear = Integer.parseInt(solarInfo[0]);
            solar.solarMonth = Integer.parseInt(solarInfo[1]);
            solar.solarDay = Integer.parseInt(solarInfo[2]);
            Lunar lunar = LunarSolarConverter.SolarToLunar(solar);
            StringBuilder lunarBuilder = new StringBuilder();
            lunarBuilder.append(getResources().getString(R.string.lunar));
            lunarBuilder.append(getResources().getStringArray(R.array.month_lunar)[lunar.lunarMonth - 1]);
            lunarBuilder.append(getResources().getStringArray(R.array.lunar_days)[lunar.lunarDay - 1]);
            mPaint.getTextBounds(lunarBuilder.toString(), 0, lunarBuilder.toString().length(), mRect);
            canvas.drawText(lunarBuilder.toString(), 0, lunarBuilder.toString().length(), mCenterX - mRect.width() / 2, canvas.getHeight() - LUNAR_MARGIN_BOTTOM, mPaint);
        }
        canvas.drawLine(0, canvas.getHeight() - 2, canvas.getWidth(), canvas.getHeight(), mPaint);
    }


    private IntentFilter mTimeChangedFilter = new IntentFilter();
    private TimeChangedReceiver mTimeChangedReceiver = new TimeChangedReceiver();

    private class TimeChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive ... " + intent.getAction());
            invalidate();
        }
    }

    private boolean isZh() {
        Locale locale = getContext().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")) {
            return true;
        } else {
            return false;
        }
    }
}
