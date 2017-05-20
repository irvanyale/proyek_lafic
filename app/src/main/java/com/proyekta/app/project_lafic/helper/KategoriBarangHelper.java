package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.KategoriBarang;

import java.util.List;
import java.util.Vector;

/**
 * Created by Ervina Aprilia S on 13/05/2017.
 */


public class KategoriBarangHelper {

    private static List<KategoriBarang> kategoriBarang;

    public static List<KategoriBarang> getKategoriBarang(){
        if (kategoriBarang == null){
            kategoriBarang = new Vector<>();
        }
        return kategoriBarang;
    }

}
