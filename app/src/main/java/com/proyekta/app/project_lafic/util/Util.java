package com.proyekta.app.project_lafic.util;

import android.util.Log;

import com.proyekta.app.project_lafic.model.KategoriBarang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WINDOWS 10 on 07/05/2017.
 */

public class Util {

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
}
