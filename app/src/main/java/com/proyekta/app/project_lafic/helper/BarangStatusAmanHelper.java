package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.Barang;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by WINDOWS 10 on 21/05/2017.
 */

public class BarangStatusAmanHelper {

    private static List<Barang> barangAman;
    private static List<Barang> allBarang = BarangHelper.getBarang();

    private static void setBarangAman(){
        barangAman = new ArrayList<>();
        for (Barang item : allBarang){
            if (item.getSTATUS().equals("AMAN")){
                barangAman.add(item);
            }
        }
    }

    public static List<Barang> getBarangAman(){
        setBarangAman();
        return barangAman;
    }
}
