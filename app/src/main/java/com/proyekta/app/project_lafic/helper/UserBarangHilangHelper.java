package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.BarangHilang;

import java.util.List;
import java.util.Vector;

/**
 * Created by WINDOWS 10 on 28/05/2017.
 */

public class UserBarangHilangHelper {

    private static List<BarangHilang> userBarangHilang;

    public static List<BarangHilang> getUserBarangHilang(){
        if (userBarangHilang == null){
            userBarangHilang = new Vector<>();
        }
        return userBarangHilang;
    }
}
