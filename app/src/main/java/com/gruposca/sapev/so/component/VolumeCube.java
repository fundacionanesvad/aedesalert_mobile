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
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.gruposca.sapev.R;

public class VolumeCube extends View {

    private static final float CUBE_RATIO = 1f;
    private static final float CUBE_RATIO_X = 1.3f;
    private static final float CUBE_RATIO_Y = 1.4f;

    private static final float EDGE_SIZE = 10;

    public static final int EDGE_WIDTH = 0x01;
    public static final int EDGE_HEIGHT = 0x02;
    public static final int EDGE_DEPTH = 0x04;

    private int cubeEdge;
    private boolean edgeWidth;
    private boolean edgeHeight;
    private boolean edgeDepth;

    private Paint edgePaint;
    private Paint selectionPaint;
    private Paint waterPaint;
    private Paint surfacePaint;

    private RectF rectTop;
    private RectF rectBottom;
    private Path pathLine;
    private Path waterPath;
    private Path surfacePath;

    public VolumeCube(Context context) {
        super(context);
        initialize(context, null);
    }

    public VolumeCube(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public VolumeCube(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        edgeWidth = false;
        edgeHeight = false;
        edgeDepth = false;
        int edgeColor = resources.getColor(R.color.primary);
        int selectionColor = resources.getColor(R.color.accent);
        int waterColor = resources.getColor(R.color.water);
        int surfaceColor = resources.getColor(R.color.surface);
        if (attrs != null) {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VolumeCube, 0, 0);
            cubeEdge = array.getInteger(R.styleable.VolumeCube_cubeEdge, 0);
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
        waterPaint.setStyle(Paint.Style.FILL);

        surfacePaint = new Paint();
        surfacePaint.setColor(surfaceColor);
        surfacePaint.setAntiAlias(true);
        surfacePaint.setStyle(Paint.Style.FILL);
    }

    private void calculateEdges() {
        edgeWidth = (cubeEdge & EDGE_WIDTH) == EDGE_WIDTH;
        edgeHeight = (cubeEdge & EDGE_HEIGHT) == EDGE_HEIGHT;
        edgeDepth = (cubeEdge & EDGE_DEPTH) == EDGE_DEPTH;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int width = w - getPaddingLeft() - getPaddingRight();
        int height = h - getPaddingTop() - getPaddingBottom();
        int size = Math.min(width, (int) (height / CUBE_RATIO));
        int left = (width - size) / 2;
        int top = (height - (int)(size * CUBE_RATIO)) / 2;
        RectF rect = new RectF(getPaddingLeft() + left, getPaddingTop() + top, getPaddingLeft() + left + size, getPaddingTop() + top + size * CUBE_RATIO - getPaddingBottom());
        rectTop = new RectF(rect.left, rect.top, rect.left + size / CUBE_RATIO_X, rect.top + size / CUBE_RATIO_Y);
        rectBottom = new RectF(rect.right - size / CUBE_RATIO_X, rect.bottom - size / CUBE_RATIO_Y, rect.right, rect.bottom);
        pathLine = new Path();
        pathLine.moveTo(rectBottom.left, rectBottom.bottom);
        pathLine.lineTo(rectTop.left, rectTop.bottom);
        pathLine.lineTo(rectTop.left, rectTop.top);
        pathLine.lineTo(rectTop.right, rectTop.top);
        pathLine.lineTo(rectBottom.right, rectBottom.top);

        float water = rectBottom.height() * 0.6f;
        waterPath = new Path();
        waterPath.moveTo(rectBottom.right, rectBottom.bottom);
        waterPath.lineTo(rectBottom.left, rectBottom.bottom);
        waterPath.lineTo(rectTop.left, rectTop.bottom);
        waterPath.lineTo(rectTop.left, rectTop.bottom - water);
        waterPath.lineTo(rectBottom.left, rectBottom.bottom - water);
        waterPath.lineTo(rectBottom.right, rectBottom.bottom - water);
        surfacePath = new Path();
        surfacePath.moveTo(rectBottom.right, rectBottom.bottom - water);
        surfacePath.lineTo(rectBottom.left, rectBottom.bottom - water);
        surfacePath.lineTo(rectTop.left, rectTop.bottom - water);
        surfacePath.lineTo(rectTop.right, rectTop.bottom - water);
    }

    protected void onDraw(Canvas canvas) {
        //Water
        canvas.drawPath(waterPath, waterPaint);
        canvas.drawPath(surfacePath, surfacePaint);

        //Cube
        canvas.drawPath(pathLine, edgePaint);
        canvas.drawLine(rectTop.left, rectTop.top, rectBottom.left, rectBottom.top, edgePaint);
        canvas.drawLine(rectTop.right, rectTop.top, rectTop.right, rectTop.top + rectTop.height() * 0.4f, edgePaint);
        canvas.drawRect(rectBottom, edgePaint);
        if (edgeDepth) {
            canvas.drawLine(rectTop.left, rectTop.bottom, rectBottom.left, rectBottom.bottom, selectionPaint);
        }
        if (edgeHeight) {
            canvas.drawLine(rectBottom.right, rectBottom.bottom - rectBottom.height() * 0.6f, rectBottom.right, rectBottom.bottom + EDGE_SIZE / 2, selectionPaint);
        }
        if (edgeWidth) {
            canvas.drawLine(rectBottom.left, rectBottom.bottom, rectBottom.right, rectBottom.bottom, selectionPaint);
        }
    }

    public void setCubeEdge(int edge) {
        cubeEdge = edge;
        calculateEdges();
        invalidate();
    }

    public int getCubeEdge() {
        return cubeEdge;
    }
}