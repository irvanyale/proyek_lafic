package com.proyekta.app.project_lafic.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WINDOWS 10 on 13/05/2017.
 */

public class SubKategoriBarangHelper {

    public static List<String> setListSubKategori(String id) {
        List<String> listSubKategori = new ArrayList<>();

        listSubKategori.add("Pilih SubKategori");
        switch (id){
            case "1":
                listSubKategori.add("Laptop");
                listSubKategori.add("Charger Laptop");
                listSubKategori.add("HP");
                listSubKategori.add("Charger HP");
                break;
            case "2":
                listSubKategori.add("STNK");
                listSubKategori.add("SIM");
                listSubKategori.add("KTM ");
                listSubKategori.add("KTP");
                break;
            case "3":
                listSubKategori.add("Jam Tangan ");
                listSubKategori.add("Dompet");
                break;
        }

        return listSubKategori;
    }
}
