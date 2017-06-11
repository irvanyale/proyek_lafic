package com.proyekta.app.project_lafic.util;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ervina Aprilia S on 01/05/2017.
 */

public class StorageUtil {
    private static final String TAG = "StorageUtil";

    public static final String LAFIC_DIRECTORY_NAME = "LAFIC";
    public static final String FILE_DIRECTORY_NAME = LAFIC_DIRECTORY_NAME + "/QRCODE";
    public static final String IMAGE_DIRECTORY_NAME = "LAFIC";

    public static String getLaficDirectoryPath() {
        String path = "";
        try {
            path = Environment.getExternalStorageDirectory() + "/" + LAFIC_DIRECTORY_NAME;
            //path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            //path = Environment.getExternalStorageDirectory().toString();
            Log.d(TAG, "getLaficDirectoryPath: "+path);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return path;
    }

    public static String getFileDirectoryPath() {
        String path = "";
        try {
            path = Environment.getExternalStorageDirectory() + "/" + FILE_DIRECTORY_NAME;
            Log.d(TAG, "getFileDirectoryPath: "+path);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return path;
    }

    public static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
