package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.BarangPenemuan;

import java.util.List;
import java.util.Vector;

/**
 * Created by WINDOWS 10 on 23/05/2017.
 */

public class UserBarangPenemuanHelper {

    private static List<BarangPenemuan> userBarangPenemuan;

    public static List<BarangPenemuan> getUserBarangPenemuan(){
        if (userBarangPenemuan == null){
            userBarangPenemuan = new Vector<>();
        }
        return userBarangPenemuan;
    }
}
