package com.proyekta.app.project_lafic.model;

/**
 * Created by WINDOWS 10 on 21/05/2017.
 */

public class Pesan {
    private String MEMBER_ID;
    private String KEPADA;
    private String PENGIRIM_ID;
    private String PENGIRIM;
    private String JUDUL_PESAN;
    private String ISI_PESAN;
    private String TANGGAL_PESAN;

    public String getMEMBER_ID() {
        return MEMBER_ID;
    }

    public void setMEMBER_ID(String MEMBER_ID) {
        this.MEMBER_ID = MEMBER_ID;
    }

    public String getKEPADA() {
        return KEPADA;
    }

    public void setKEPADA(String KEPADA) {
        this.KEPADA = KEPADA;
    }

    public String getPENGIRIM_ID() {
        return PENGIRIM_ID;
    }

    public void setPENGIRIM_ID(String PENGIRIM_ID) {
        this.PENGIRIM_ID = PENGIRIM_ID;
    }

    public String getPENGIRIM() {
        return PENGIRIM;
    }

    public void setPENGIRIM(String PENGIRIM) {
        this.PENGIRIM = PENGIRIM;
    }

    public String getJUDUL_PESAN() {
        return JUDUL_PESAN;
    }

    public void setJUDUL_PESAN(String JUDUL_PESAN) {
        this.JUDUL_PESAN = JUDUL_PESAN;
    }

    public String getISI_PESAN() {
        return ISI_PESAN;
    }

    public void setISI_PESAN(String ISI_PESAN) {
        this.ISI_PESAN = ISI_PESAN;
    }

    public String getTANGGAL_PESAN() {
        return TANGGAL_PESAN;
    }

    public void setTANGGAL_PESAN(String TANGGAL_PESAN) {
        this.TANGGAL_PESAN = TANGGAL_PESAN;
    }
}
