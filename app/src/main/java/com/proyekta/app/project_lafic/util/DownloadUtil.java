package com.proyekta.app.project_lafic.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.proyekta.app.project_lafic.activity.AddItemActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by WINDOWS 10 on 01/05/2017.
 */

public class DownloadUtil {

    private static final String TAG = "DownloadUtil";

    public static void DownloadImage(final Context context, final String urls, final String targetPath, final String filename){

        class DownloadTask extends AsyncTask<Void, Void, Void>{

            private ProgressDialog dialog = new ProgressDialog(context);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Loading...");
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                int count;
                try {
                    if (urls != null && urls.length() != 0){

                        File file = new File(targetPath, filename+".jpg");

                        Log.d(TAG, "URL "+urls);
                        URL url = new URL(urls);
                        URLConnection conexion = url.openConnection();
                        conexion.connect();

                        InputStream input = new BufferedInputStream(url.openStream());
                        //OutputStream output = new FileOutputStream(targetPath+"/"+filename+".jpg");
                        OutputStream output = new FileOutputStream(file);
                        byte data[] = new byte[1024];
                        while ((count = input.read(data)) != -1) {
                            output.write(data, 0, count);
                        }

                        output.flush();
                        output.close();
                        input.close();

                        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.d("TAG", "onScanCompleted: "+path);
                                Log.d("TAG", "onScanCompleted: "+uri);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dialog.dismiss();
                Toast.makeText(context, "Download Sukses", Toast.LENGTH_SHORT).show();
                super.onPostExecute(aVoid);
            }
        }

        new DownloadTask().execute();
    }
}
