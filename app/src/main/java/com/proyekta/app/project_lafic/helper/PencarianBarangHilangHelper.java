package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.BarangHilang;

import java.util.List;
import java.util.Vector;

/**
 * Created by WINDOWS 10 on 24/05/2017.
 */

public class PencarianBarangHilangHelper {

    private static List<BarangHilang> pencarianBarangHilang;

    public static List<BarangHilang> getPencarianBarangHilang(){
        if (pencarianBarangHilang == null){
            pencarianBarangHilang = new Vector<>();
        }
        return pencarianBarangHilang;
    }
}
