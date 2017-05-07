package com.proyekta.app.project_lafic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.proyekta.app.project_lafic.model.KategoriBarang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WINDOWS 10 on 07/05/2017.
 */

public class Util {

    final static String PREF_NAME = "lafic";
    final static String TOKEN = "status";
    final static int PRIVATE_MODE = 0;
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;

    public static List<String> getIdKategori(List<String> kategoriBarang){
        List<String> listId = new ArrayList<>();
        String[] id;
        for (String data : kategoriBarang){
            id = data.split("~");
            listId.add(id[0]);
            Log.d("TAG", "getIdKategori: "+id.length);
        }
        return listId;
    }

    public static List<String> getNamaKategori(List<String> kategoriBarang){
        List<String> listNama = new ArrayList<>();
        String[] nama;
        for (String data : kategoriBarang){
            nama = data.split("~");
            listNama.add(nama[1]);
        }
        return listNama;
    }

    public static List<String> getKeteranganKategori(List<String> kategoriBarang){
        List<String> listKet = new ArrayList<>();
        String[] ket;
        for (String data : kategoriBarang){
            ket = data.split("~");
            listKet.add(ket[2]);
        }
        return listKet;
    }

    public static void setToken(Context context, String token){

        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context){
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(TOKEN, "");
    }
}
