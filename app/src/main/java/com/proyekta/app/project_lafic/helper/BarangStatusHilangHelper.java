package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.Barang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WINDOWS 10 on 21/05/2017.
 */

public class BarangStatusHilangHelper {

    private static List<Barang> barangHilang;
    private static List<Barang> allBarang = BarangHelper.getBarang();

    private static void setBarangHilang(){
        barangHilang = new ArrayList<>();
        for (Barang item : allBarang){
            if (item.getSTATUS().equals("HILANG")){
                barangHilang.add(item);
            }
        }
    }

    public static List<Barang> getBarangHilang(){
        setBarangHilang();
        return barangHilang;
    }

}
