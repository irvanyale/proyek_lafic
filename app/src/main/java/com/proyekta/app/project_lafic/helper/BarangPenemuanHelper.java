package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.BarangPenemuan;

import java.util.List;
import java.util.Vector;

/**
 * Created by WINDOWS 10 on 14/05/2017.
 */

public class BarangPenemuanHelper {
    private static List<BarangPenemuan> barangPenemuan;

    public static List<BarangPenemuan> getBarangPenemuan(){
        if (barangPenemuan == null){
            barangPenemuan = new Vector<>();
        }
        return barangPenemuan;
    }
}
