package app.proyekta.app.project_lafic.helper;

import app.proyekta.app.project_lafic.model.BarangHilang;

import java.util.List;
import java.util.Vector;

/**
 * Created by WINDOWS 10 on 14/05/2017.
 */

public class BarangHilangHelper {
    private static List<BarangHilang> barangHilang;

    public static List<BarangHilang> getBarangHilang(){
        if (barangHilang == null){
            barangHilang = new Vector<>();
        }
        return barangHilang;
    }
}
