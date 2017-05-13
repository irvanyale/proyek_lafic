package com.proyekta.app.project_lafic.model;

/**
 * Created by Ervina Aprilia S on 01/05/2017.
 */

public class Barang {

    private String BARANG_ID;
    private String MEMBER_ID;
    private String ID_KATEGORY;
    private String JENIS_BARANG;
    private String MERK_BARANG;
    private String STATUS;
    private String WARNA_BARANG;
    private String KETERANGAN;
    private String QRCODE;

    public Barang(String MEMBER_ID, String ID_KATEGORY, String JENIS_BARANG, String MERK_BARANG, String STATUS, String WARNA_BARANG, String KETERANGAN) {
        this.MEMBER_ID = MEMBER_ID;
        this.ID_KATEGORY = ID_KATEGORY;
        this.JENIS_BARANG = JENIS_BARANG;
        this.MERK_BARANG = MERK_BARANG;
        this.STATUS = STATUS;
        this.WARNA_BARANG = WARNA_BARANG;
        this.KETERANGAN = KETERANGAN;
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

    public String getMERK_BARANG() {
        return MERK_BARANG;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public String getWARNA_BARANG() {
        return WARNA_BARANG;
    }

    public String getKETERANGAN() {
        return KETERANGAN;
    }

    public String getQRCODE() {
        return QRCODE;
    }

    public String getJENIS_BARANG() {
        return JENIS_BARANG;
    }
}
