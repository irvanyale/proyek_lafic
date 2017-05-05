package com.proyekta.app.project_lafic.model;

/**
 * Created by WINDOWS 10 on 01/05/2017.
 */

public class Item {

    private String BARANG_ID;
    private String MEMBER_ID;
    private String ID_KATEGORY;
    private String NAMA_BARANG;
    private String STATUS;
    private String WARNA_BARANG;
    private String TIPE_BARANG;
    private String QRCODE;

    public Item(String MEMBER_ID, String ID_KATEGORY, String NAMA_BARANG, String STATUS, String WARNA_BARANG, String TIPE_BARANG) {
        this.MEMBER_ID = MEMBER_ID;
        this.ID_KATEGORY = ID_KATEGORY;
        this.NAMA_BARANG = NAMA_BARANG;
        this.STATUS = STATUS;
        this.WARNA_BARANG = WARNA_BARANG;
        this.TIPE_BARANG = TIPE_BARANG;
    }

    public String getBARANG_ID() {
        return BARANG_ID;
    }

    public String getMEMBER_ID() {
        return MEMBER_ID;
    }

    public String getID_KATEGORY() {
        return ID_KATEGORY;
    }

    public String getNAMA_BARANG() {
        return NAMA_BARANG;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public String getWARNA_BARANG() {
        return WARNA_BARANG;
    }

    public String getTIPE_BARANG() {
        return TIPE_BARANG;
    }

    public String getQRCODE() {
        return QRCODE;
    }
}
