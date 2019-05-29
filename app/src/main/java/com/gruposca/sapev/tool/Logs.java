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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.gruposca.sapev.BaseApplication;
import com.gruposca.sapev.R;

public final class Logs {

	private static final String LOG_TAG = "SAPEV";
	private static final String LOG_FILE_NAME = "application.txt";

	public static final String SYNC_DATA_COLLECT       = "Recopilando datos del plan para realizar la sincronización";
	public static final String SYNC_DATA_COLLECT_OK    = "Recopilación de datos del plan completada.";
	public static final String SYNC_CONNECT            = "Conectando con el servidor API para sincronizar el plan.";
	public static final String SYNC_VISITS             = "Comprobando si existen visitas registradas para el plan.";
	public static final String SYNC_VISITS_OK          = "Existen visitas en el plan actual";
	public static final String SYNC_VISITS_ERROR       = "No existen visitas en el plan actual";
	public static final String SYNC_CANCELED           = "Sincronización cancelada por el usuario";
	public static final String SYNC_INI                = "Comienzo del proceso de sincronización";
	public static final String SYNC_DATA_CONNECT       = "Comprobando la conexión";
	public static final String SYNC_DATA_CONNECT_OK    = "La conexión es correcta";
	public static final String SYNC_DATA_CONNECT_ERROR = "No hay conexión disponible";
	public static final String SYNC_PLAN_OK 	 	   = "Plan sincronizado";
	public static final String SYNC_LOGIN 			   = "Conectando con el servidor API para comprobar los datos de usuario y contraseña";
	public static final String SYNC_LOGIN_OK 		   = "Comprobación de datos de usuario correcta";

	public static final String VERBOSE = "Verbose";
	public static final String DEBUG = "Debug";
	public static final String INFO = "Info";
	public static final String WARN = "Warn";
	public static final String ERROR = "Error";
	public static final String WTF = "Wtf";

	public static void log(String level, String module, String method) {
		log(level, module, method, null);
	}

	public static void log(String level, String module, String method, String message) {
		if (isLoggable(LOG_TAG, level)) {
			String log = renderMessageForLog(module, method, message);
			switch (level) {
				case VERBOSE:
					android.util.Log.v(LOG_TAG, log);
					break;
				case DEBUG:
					android.util.Log.d(LOG_TAG, log);
					break;
				case INFO:
					android.util.Log.i(LOG_TAG, log);
					break;
				case WARN:
					android.util.Log.w(LOG_TAG, log);
					break;
				case ERROR:
					android.util.Log.e(LOG_TAG, log);
					break;
				case WTF:
					android.util.Log.w(LOG_TAG, log);
					break;
			}
			toFile(level, module, method, message);
		}
	}

	public static void log(String level, String module, String method, String message, Throwable error) {
		if (isLoggable(LOG_TAG, level)) {
			String log = renderMessageForLog(module, method, message);
			switch (level) {
				case VERBOSE:
					android.util.Log.v(LOG_TAG, log, error);
					break;
				case DEBUG:
					android.util.Log.d(LOG_TAG, log, error);
					break;
				case INFO:
					android.util.Log.i(LOG_TAG, log, error);
					break;
				case WARN:
					android.util.Log.w(LOG_TAG, log, error);
					break;
				case ERROR:
					android.util.Log.e(LOG_TAG, log, error);
					break;
				case WTF:
					android.util.Log.w(LOG_TAG, log, error);
					break;
			}
			toFile(level, module, method, message, error);
		}
	}

	public static void deleteLogsFile() {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File logsFile = getLogFile();
			if (logsFile.exists()) {
				logsFile.delete();
			}
		}
	}

	private static void toFile(String level, String module, String method, String message) {
		toFile(level, module, method, message, null);
	}

	private static void toFile(String level, String module, String method, String message, Throwable error) {
		try {
			String storageState = Environment.getExternalStorageState();
			if (storageState.equals(Environment.MEDIA_MOUNTED)) {
				File logsFile = getLogFile();
				String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
				FileWriter writer = new FileWriter(logsFile, true);
				writer.write(date + "\t" + level + "\t" + renderMessageForFile(module, method, message) + "\n");
				if (error != null)
					writer.write(date + "\t" + throwable2string(error) + "\n");
				writer.close();
			} else {
				android.util.Log.i(LOG_TAG, "The external SD is not ready or present.");
			}
		} catch (IOException e) {
			android.util.Log.e(LOG_TAG, "toFile:" + e.toString());
		}
	}

	private static String renderMessageForFile( String module, String method, String message) {
		if (message == null || message.equals(""))
			return String.format("%s.%s", module, method);
		else
			return String.format("%s.%s - %s", module, method, message);
	}

	private static String renderMessageForLog(String module, String method, String message) {
		if (message == null)
			return String.format("%s.%s", module, method);
		else
			return String.format("%s.%s - %s", module, method, message);
	}

	public static File getLogFile() {
		File externalStorageFolder = getApplicationCacheFilesFolder();
		File logsFile = new File(externalStorageFolder, LOG_FILE_NAME);
		return logsFile;
	}

	private static File getApplicationCacheFilesFolder() {
		File externalStorageFolder = BaseApplication.getContext().getExternalFilesDir(null);
		if (!externalStorageFolder.exists())
			externalStorageFolder.mkdirs();
		return externalStorageFolder;
	}

	private static String throwable2string(Throwable tr) {
		StringWriter stackWriter = new StringWriter();
		PrintWriter stackPrinter = new PrintWriter(stackWriter);
		tr.printStackTrace(stackPrinter);
		return stackWriter.toString();
	}

	private static boolean isLoggable(String tag, String level) {
		return (BaseApplication.isDebuggable() || (!level.equals(VERBOSE) && !level.equals(DEBUG)));
	}

	/*public static void resetLogcat(){
		try{
			Runtime.getRuntime().exec("logcat -c");
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void printLogcat(){
		try{
			String newline = "\r\n";

			Process process = Runtime.getRuntime().exec("logcat -d");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			Writer output;
			output = new BufferedWriter(new FileWriter(file, true));
			output.append(newline+"*** SALIDA LOGCAT *** "+newline);

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				output.append(line+newline);
			}
			output.close();

		}catch (IOException e){
			e.printStackTrace();
		}
	}
    */

}