package app.proyekta.app.project_lafic.util;

import android.os.Environment;
import android.util.Log;

/**
 * Created by Ervina Aprilia S on 01/05/2017.
 */

public class StorageUtil {
    private static final String TAG = "StorageUtil";

    public static final String LAFIC_DIRECTORY_NAME = "LAFIC";
    public static final String FILE_DIRECTORY_NAME = LAFIC_DIRECTORY_NAME + "/QRCODE";

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
}
