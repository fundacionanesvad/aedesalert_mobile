/*
 * Aedes Alert, Support to collect data to combat dengue
 * Copyright (C) 2017 Fundación Anesvad
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.gruposca.sapev.so.component;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.gruposca.sapev.R;

public class CaptureGps extends View {

    private static final float DEFAULT_TEXT_SIZE = 12;
    private static final float DEFAULT_PROGRESS_SIZE = 30;
    private static final int ACCURACY_MIN = 20;
    private static final int ACCURACY_MAX = 320;

    private Paint textPaint;
    private Paint backgroundPaint;
    private Paint centerPaint;
    private Paint progressPaint;
    private Paint successPaint;

    private RectF rect;
    private RectF center;
    private Location location = null;
    private Float progress = 0f;
    private String text;

    public CaptureGps(Context context) {
        super(context);
        initialize(context, null);
    }

    public CaptureGps(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public CaptureGps(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, getResources().getDisplayMetrics());
        int textColor = resources.getColor(R.color.primary_text);
        int backgroundColor = resources.getColor(R.color.divider);
        int centerColor = resources.getColor(R.color.text);
        int progressColor = resources.getColor(R.color.primary);
        int successColor = resources.getColor(R.color.success);
        if (attrs != null) {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CaptureGps, 0, 0);
            textSize = array.getDimension(R.styleable.CaptureGps_textSize, textSize);
        }

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAntiAlias(true);
        centerPaint = new Paint();
        centerPaint.setColor(centerColor);
        centerPaint.setAntiAlias(true);
        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setAntiAlias(true);
        successPaint= new Paint();
        successPaint.setColor(successColor);
        successPaint.setAntiAlias(true);

        setLocation(null);
    }

    private void calculate() {
        float newProgress = progress;

        if (location == null) {
            progress = 0f;
            text = getResources().getString(R.string.gps_none);
        } else {
            float accuracy = location.getAccuracy();
            text = getResources().getString(R.string.gps_accuracy, (int) accuracy);
            accuracy = Math.min(ACCURACY_MAX, accuracy);
            accuracy = Math.max(ACCURACY_MIN, accuracy);
            newProgress = 360f - (float)(Math.sqrt((accuracy - ACCURACY_MIN) / (ACCURACY_MAX - ACCURACY_MIN)) * 360);
        }
        invalidate();

        if (newProgress != progress) {
            ValueAnimator animator = ValueAnimator.ofFloat(progress, newProgress);
            animator.setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progress = (Float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.start();
        }
    }

    public void setLocation(Location location) {
        this.location = location;
        calculate();
    }

    public Location getLocation() {
        return location;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int width = w - getPaddingLeft() - getPaddingRight();
        int height = h - getPaddingTop() - getPaddingBottom();
        int size = Math.min(width, height);
        int left = (width - size) / 2;
        int top = (height - size) / 2;
        rect = new RectF(getPaddingLeft() + left, getPaddingTop() + top, getPaddingLeft() + left + size, getPaddingTop() + top + size);

        float progressSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROGRESS_SIZE, getResources().getDisplayMetrics());
        center = new RectF(rect);
        center.inset(progressSize, progressSize);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawArc(rect, -90, progress, true, progress == 360 ? successPaint : progressPaint);
        canvas.drawArc(rect, progress - 90, 360 - progress, true, backgroundPaint);
        canvas.drawOval(center, centerPaint);
        canvas.drawText(text, rect.centerX(), rect.centerY(), textPaint);
    }
}