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

package com.gruposca.sapev.interactor;

import com.gruposca.sapev.datastore.database.query.InventoryListQuery;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;

import java.util.List;

public abstract class AbsInteractor<T> implements Runnable {

    protected final ThreadExecutor threadExecutor;
    protected final PostThreadExecutor postExecutionThread;
    protected Callback<T> callback;

    public AbsInteractor(ThreadExecutor threadExecutor, PostThreadExecutor postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    protected void notifyResponse(final T response) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }

    protected void notifyError(final Exception error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(new ErrorBundle(error));
            }
        });
    }


}