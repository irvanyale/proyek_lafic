package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.Pesan;

import java.util.List;
import java.util.Vector;

/**
 * Created by WINDOWS 10 on 22/05/2017.
 */

public class PesanHelper {

    private static List<Pesan> pesan;

    public static List<Pesan> getPesan(){
        if (pesan == null){
            pesan = new Vector<>();
        }
        return pesan;
    }
}
