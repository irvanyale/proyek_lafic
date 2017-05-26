package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.model.BarangPenemuan;

import java.util.List;
import java.util.Vector;

/**
 * Created by WINDOWS 10 on 24/05/2017.
 */

public class PencarianBarangPenemuanHelper {

    private static List<BarangPenemuan> pencarianBarangPenemuan;

    public static List<BarangPenemuan> getPencarianBarangPenemuan(){
        if (pencarianBarangPenemuan == null){
            pencarianBarangPenemuan = new Vector<>();
        }
        return pencarianBarangPenemuan;
    }
}
