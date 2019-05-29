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

import com.gruposca.sapev.datastore.net.ProgressConverter;
import com.gruposca.sapev.exception.ErrorBundle;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.repository.PlanRepository;

import javax.inject.Inject;

public class PlanSubmit implements Runnable, ProgressConverter.ProgressListener {

    protected final ThreadExecutor threadExecutor;
    protected final PostThreadExecutor postExecutionThread;
    private final PlanRepository planRepository;
    private final ProgressConverter converter;
    private ProgressCallback<Boolean> callback;

    @Inject
    public PlanSubmit(ThreadExecutor threadExecutor, PostThreadExecutor postExecutionThread, PlanRepository planRepository, ProgressConverter converter) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
        this.planRepository = planRepository;
        this.converter = converter;
    }

    public void execute(ProgressCallback<Boolean> callback) {
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            converter.setProgressListener(this);
            notifyResponse(planRepository.submitPlan());
        } catch (final Exception e) {
            postExecutionThread.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(new ErrorBundle(e));
                }
            });
        }
    }

    @Override
    public void onProgress(int progress, int maximum) {
        notifyProgress(progress * 100 / maximum);
    }

    protected void notifyResponse(final Boolean response) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
        converter.setProgressListener(null);
    }

    protected void notifyError(final Exception error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(new ErrorBundle(error));
            }
        });
        converter.setProgressListener(null);
    }

    protected void notifyProgress(final int progress) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onProgress(progress);
            }
        });
    }
}