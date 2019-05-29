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

package com.gruposca.sapev.datastore.net;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import retrofit.converter.GsonConverter;
import retrofit.mime.TypedOutput;

public class ProgressConverter extends GsonConverter {
    private final Gson gson;
    private String charset;
    private ProgressListener progressListener;

    public ProgressConverter(Gson gson) {
        this(gson, "UTF-8");
    }

    public ProgressConverter(Gson gson, String charset) {
        super(gson, charset);
        this.gson = gson;
        this.charset = charset;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public interface ProgressListener {
        void onProgress(int progress, int maximum);
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            return new ProgressTypedOutput(gson.toJson(object).getBytes(charset), charset, progressListener);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private static class ProgressTypedOutput implements TypedOutput {

        private final int BUFFER_SIZE = 4096;

        private final byte[] jsonBytes;
        private final String mimeType;
        private final ProgressListener progressListener;

        ProgressTypedOutput(byte[] jsonBytes, String encode, ProgressListener progressListener) {
            this.jsonBytes = jsonBytes;
            this.mimeType = "application/json; charset=" + encode;
            this.progressListener = progressListener;
        }

        @Override public String fileName() {
            return null;
        }

        @Override public String mimeType() {
            return mimeType;
        }

        @Override public long length() {
            return -1;
        }

        @Override public void writeTo(OutputStream out) throws IOException {
            int bytes;
            int offset = 0;
            do {
                //Log.d("SAPEV", String.format("%d de %d", offset, jsonBytes.length));
                bytes = Math.min(BUFFER_SIZE, jsonBytes.length - offset);
                out.write(jsonBytes, offset, bytes);
                if (progressListener != null)
                    progressListener.onProgress(offset, jsonBytes.length);
                offset+=bytes;
            } while(bytes > 0);
        }
    }
}