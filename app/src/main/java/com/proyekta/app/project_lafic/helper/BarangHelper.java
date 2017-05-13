package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.KategoriBarang;

import java.util.List;
import java.util.Vector;

/**
 * Created by Ervina Aprilia S on 13/05/2017.
 */

public class BarangHelper {
    private static List<Barang> barang;

    public static List<Barang> getBarang(){
        if (barang == null){
            barang = new Vector<>();
        }
        return barang;
    }
}
