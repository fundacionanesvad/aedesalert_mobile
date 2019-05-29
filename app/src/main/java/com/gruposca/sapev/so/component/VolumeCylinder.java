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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.gruposca.sapev.R;

public class VolumeCylinder extends View {

    private static final float CYLINDER_RATIO = 1.5f;
    private static final float CYLINDER_ANGLE = 3f;

    private static final float EDGE_SIZE = 10;

    public static final int EDGE_DIAMETER = 0x01;
    public static final int EDGE_HEIGHT = 0x02;

    private int edgeCylinder;
    private boolean edgeDiameter;
    private boolean edgeHeight;

    private Paint edgePaint;
    private Paint selectionPaint;
    private Paint waterPaint;
    private Paint surfacePaint;

    private RectF rectCylinderTop;
    private RectF rectCylinderCenter;
    private RectF rectCylinderBottom;
    private RectF rectWaterTop;
    private RectF rectWaterCenter;

    public VolumeCylinder(Context context) {
        super(context);
        initialize(context, null);
    }

    public VolumeCylinder(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public VolumeCylinder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        int edgeColor = resources.getColor(R.color.primary);
        int selectionColor = resources.getColor(R.color.accent);
        int waterColor = resources.getColor(R.color.water);
        int surfaceColor = resources.getColor(R.color.surface);
        if (attrs != null) {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VolumeCylinder, 0, 0);
            edgeCylinder = array.getInteger(R.styleable.VolumeCylinder_cylinderEdge, 0);
            calculateEdges();
        }

        edgePaint = new Paint();
        edgePaint.setColor(edgeColor);
        edgePaint.setAntiAlias(true);
        edgePaint.setStrokeWidth(EDGE_SIZE);
        edgePaint.setStyle(Paint.Style.STROKE);

        selectionPaint = new Paint();
        selectionPaint.setColor(selectionColor);
        selectionPaint.setAntiAlias(true);
        selectionPaint.setStrokeWidth(EDGE_SIZE);
        selectionPaint.setStyle(Paint.Style.STROKE);

        waterPaint = new Paint();
        waterPaint.setColor(waterColor);
        waterPaint.setAntiAlias(true);
        waterPaint.setStrokeWidth(EDGE_SIZE);
        waterPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        surfacePaint = new Paint();
        surfacePaint.setColor(surfaceColor);
        surfacePaint.setAntiAlias(true);
        surfacePaint.setStrokeWidth(EDGE_SIZE);
        surfacePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private void calculateEdges() {
        edgeDiameter = (edgeCylinder & EDGE_DIAMETER) == EDGE_DIAMETER;
        edgeHeight = (edgeCylinder & EDGE_HEIGHT) == EDGE_HEIGHT;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int width = w - getPaddingLeft() - getPaddingRight();
        int height = h - getPaddingTop() - getPaddingBottom();
        int size = Math.min(width, (int)(height / CYLINDER_RATIO));
        int left = (width - size) / 2;
        int top = (height - (int)(size * CYLINDER_RATIO)) / 2;
        RectF rect = new RectF(getPaddingLeft() + left, getPaddingTop() + top, getPaddingLeft() + left + size, getPaddingTop() + top + size * CYLINDER_RATIO - getPaddingBottom());
        rectCylinderTop = new RectF(rect.left, rect.top, rect.right, rect.top + size / CYLINDER_ANGLE);
        rectCylinderCenter = new RectF(rect.left, rect.top + size / CYLINDER_ANGLE / 2, rect.right, rect.bottom - size / CYLINDER_ANGLE / 2);
        rectCylinderBottom = new RectF(rect.left, rect.bottom - size / CYLINDER_ANGLE, rect.right, rect.bottom);

        float water = rectCylinderCenter.top + size / CYLINDER_ANGLE * 4 / 3;
        rectWaterTop = new RectF(rect.left + EDGE_SIZE, water - size / CYLINDER_ANGLE / 2 + EDGE_SIZE, rect.right - EDGE_SIZE, water + size / CYLINDER_ANGLE / 2 - EDGE_SIZE);
        rectWaterCenter = new RectF(rect.left + EDGE_SIZE, water, rect.right - EDGE_SIZE, rectCylinderCenter.bottom);
    }

    protected void onDraw(Canvas canvas) {
        //Water
        canvas.drawRect(rectWaterCenter, waterPaint);
        canvas.drawArc(rectWaterTop, 0, 360, false, surfacePaint);
        canvas.drawArc(rectCylinderBottom, 0, 180, false, waterPaint);

        //Cylinder
        canvas.drawLine(rectCylinderCenter.left, rectCylinderCenter.top, rectCylinderCenter.left, rectCylinderCenter.bottom, edgePaint);
        canvas.drawLine(rectCylinderCenter.right, rectCylinderCenter.top, rectCylinderCenter.right, rectCylinderCenter.bottom, edgePaint);
        if (edgeHeight) {
            canvas.drawLine(rectCylinderCenter.right, rectWaterCenter.top, rectCylinderCenter.right, rectWaterCenter.bottom, selectionPaint);
        }
        if (edgeDiameter) {
            canvas.drawLine(rectCylinderTop.left, rectCylinderTop.centerY(), rectCylinderTop.right, rectCylinderTop.centerY(), selectionPaint);
        }
        canvas.drawArc(rectCylinderTop, 0, 360, false, edgePaint);
        canvas.drawArc(rectCylinderBottom, 0, 180, false, edgePaint);
    }

    public void setCylinderEdge(int edge) {
        edgeCylinder = edge;
        calculateEdges();
        invalidate();
    }

    public int getCylinderEdge() {
        return edgeCylinder;
    }
}
