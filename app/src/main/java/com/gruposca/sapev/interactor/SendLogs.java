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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;

import com.gruposca.sapev.R;
import com.gruposca.sapev.executor.PostThreadExecutor;
import com.gruposca.sapev.executor.ThreadExecutor;
import com.gruposca.sapev.tool.Logs;
import com.gruposca.sapev.tool.Mail;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by cf1 on 06/02/2018.
 */

public class SendLogs extends AbsInteractor<Boolean>{

    private Context context;

    @Inject
    public SendLogs(ThreadExecutor threadExecutor, PostThreadExecutor postExecutionThread, Context context) {
        super(threadExecutor, postExecutionThread);
        this.context = context;
    }

    public void execute(Callback<Boolean> callback) {
        this.callback = callback;
        threadExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            final File file = Logs.getLogFile();
            if (file.exists()) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                ApplicationInfo app = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = app.metaData;
                final String mail_user = bundle.getString("mail_user");
                final String mail_password = bundle.getString("mail_password");
                final String mail_to = bundle.getString("mail_to");
                final String mail_from = bundle.getString("mail_from");

                Mail mail = new Mail(mail_user, mail_password);
                String[] to = { mail_to };
                mail.setTo(to);
                mail.setFrom(mail_from);
                mail.setSubject("Archivo de logs Aedes Alert");
                mail.setBody("Adjunto archivo de logs");
                mail.addAttachment(file.getAbsolutePath());

                if (mail.send()) {
                    Logs.deleteLogsFile();
                    notifyResponse(true);
                } else {
                    Logs.log(Logs.ERROR, null, null, context.getString(R.string.send_email_logs_error));
                    notifyResponse(false);
                }
            } else {
                notifyResponse(false);
            }
        } catch (Exception e) {
            Logs.log(Logs.ERROR, null, null, context.getString(R.string.send_email_logs_error) + ":" + e.toString());
            notifyError(e);
        }
    }
}