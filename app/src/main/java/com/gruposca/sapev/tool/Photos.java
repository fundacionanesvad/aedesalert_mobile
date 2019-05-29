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

package com.gruposca.sapev.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import com.gruposca.sapev.BaseApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Photos {

    private final static String PHOTO_EXTENSION = ".jpg";
    private final static int PHOTO_WIDTH = 480;
    private final static int PHOTO_HEIGHT = 360;
    private final static int PHOTO_QUALITY = 90;
    private final static String PHOTO_TEMP = "temp";

    public static File getPath(String name) {
        if (!name.endsWith(PHOTO_EXTENSION))
            name += PHOTO_EXTENSION;
        return new File(getFolder(), name);
    }

    public static Uri getTempUri() {
        return getUri(PHOTO_TEMP);
    }

    public static Uri getUri(String name) {
        return Uri.fromFile(getPath(name));
    }

    public static boolean exists(String name) {
        return getPath(name).exists();
    }

    public static void resize(String name) {
        try {
            File source = getPath(PHOTO_TEMP);
            File destination = getPath(name);
            Bitmap bm = decodeSampledBitmapFromUri(source.getAbsolutePath(), PHOTO_WIDTH, PHOTO_HEIGHT);
            bm = resizeSampledBitmap(bm, PHOTO_WIDTH, PHOTO_HEIGHT);
            bm = rotateBitmap(bm, source);
            FileOutputStream output = new FileOutputStream(destination);
            bm.compress(Bitmap.CompressFormat.JPEG, PHOTO_QUALITY, output);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fake(String name) {
        try {
            File path = getPath(name);
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bm = Bitmap.createBitmap(PHOTO_WIDTH * 2, PHOTO_HEIGHT * 2, conf);
            Canvas canvas = new Canvas(bm);
            Paint paint = new Paint();
            paint.setColor(0xFFFF0000);
            paint.setStrokeWidth(50);
            paint.setAntiAlias(true);
            canvas.drawLine(0, 0, bm.getWidth(), bm.getHeight(), paint);
            canvas.drawLine(0, bm.getHeight(), bm.getWidth(), 0, paint);
            FileOutputStream output = new FileOutputStream(path);
            bm.compress(Bitmap.CompressFormat.JPEG, PHOTO_QUALITY, output);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAll() {
        File folder = getFolder();
        if (folder.isDirectory()) {
            String[] files = folder.list();
            for (String file : files) {
                delete(file);
            }
        }
    }

    public static void delete(String name) {
        File file = getPath(name);
        if (file.exists()) {
            trace("Elimina", file);
            if (!file.delete())
                trace("Error al elimnar el fichero");
        }
    }

    private static File getFolder() {
        File folder = BaseApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (!folder.exists()) {
            trace("Crea", folder);
            if (!folder.mkdirs())
                trace("Error al crear el directorio");
        }
        return folder;
    }

    private static Bitmap resizeSampledBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        trace("Objective: " + reqWidth + " - " + reqHeight);
        float height = bitmap.getHeight();
        float width = bitmap.getWidth();
        trace("Original: " + width + " - " + height);
        if (height < reqHeight && width < reqWidth) {
            return bitmap;
        } else {
            float ratioWidth = reqWidth / width;
            float ratioHeight = reqHeight / height;
            trace("Ratio: " + ratioWidth + " - " + ratioHeight);
            if (ratioWidth < ratioHeight) {
                width = reqWidth;
                height = height * ratioWidth;
            } else {
                width = width * ratioHeight;
                height = reqHeight;
            }
            trace("Final: " + width + " - " + height);
            return Bitmap.createScaledBitmap(bitmap, (int)width, (int)height, true);
        }
    }

    private static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap rotateBitmap(Bitmap source, File path) {
        int angle = 0;
        try {
            ExifInterface ei = new ExifInterface(path.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (angle == 0) {
            return source;
        } else {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            if (width > height)
                inSampleSize = Math.round((float)height / (float)reqHeight);
            else
                inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        return inSampleSize;
    }

    public static void trace(String message) {
        Logs.log(Logs.VERBOSE, "Photos", message);
    }

    public static void trace(String message, File path) {
        trace(message + " " + path.getAbsolutePath());
    }
}