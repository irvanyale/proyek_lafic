package com.proyekta.app.project_lafic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.proyekta.app.project_lafic.activity.BerandaActivity;
import com.proyekta.app.project_lafic.activity.LoginActivity;
import com.proyekta.app.project_lafic.util.StorageUtil;

import java.io.File;
import java.util.HashMap;

public class SessionManagement {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LaficApp";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEEP_LOGIN = "KeepLogin";
    public static final String KEY_ID_MEMBER = "id_member";
    public static final String KEY_NAMA = "nama";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_TELEPON = "telepon";
    public static final String KEY_KELAMIN = "kelamin";
    public static final String KEY_NOMOR_ID = "nomor_id";
    public static final String KEY_STATUS = "status";

    public SessionManagement(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    // Login Session
    public void createLoginSession(String id_member, String nama, String password, String email, String telepon, String kelamin, String nomor_id, String status){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID_MEMBER, id_member);
        editor.putString(KEY_NAMA, nama);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_TELEPON, telepon);
        editor.putString(KEY_KELAMIN, kelamin);
        editor.putString(KEY_NOMOR_ID, nomor_id);
        editor.putString(KEY_STATUS, status);
        editor.commit();
    }

    // Get Session
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_ID_MEMBER, pref.getString(KEY_ID_MEMBER, null));
        user.put(KEY_NAMA, pref.getString(KEY_NAMA, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_TELEPON, pref.getString(KEY_TELEPON, null));
        user.put(KEY_KELAMIN, pref.getString(KEY_KELAMIN, null));
        user.put(KEY_NOMOR_ID, pref.getString(KEY_NOMOR_ID, null));
        user.put(KEY_STATUS, pref.getString(KEY_STATUS, null));
        return user;
    }

    public void checkLogin(){
        if (this.isLoggedIn()){
            Intent i = new Intent(context, BerandaActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            createDirectory();
            context.startActivity(i);
        } else {
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    // Clear Session
    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void createKeepLogin(boolean value){
        editor.putBoolean(KEEP_LOGIN, value);
        editor.commit();
    }

    public boolean isKeepLogin(){
        return pref.getBoolean(KEEP_LOGIN, false);
    }

    public void checkKeepLogin(){
        if (this.isKeepLogin()){
            Intent i = new Intent(context, BerandaActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            ((Activity)context).finish();
        }
    }

    private void createDirectory() {
        File file = new File(StorageUtil.getLaficDirectoryPath());
        if (!file.exists()) {
            file.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.d("TAG", "onScanCompleted: "+path);
                    Log.d("TAG", "onScanCompleted: "+uri);
                }
            });
        }
        file = new File(StorageUtil.getFileDirectoryPath());
        if (!file.exists()) {
            file.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.d("TAG", "onScanCompleted: "+path);
                    Log.d("TAG", "onScanCompleted: "+uri);
                }
            });
        }
    }
}
