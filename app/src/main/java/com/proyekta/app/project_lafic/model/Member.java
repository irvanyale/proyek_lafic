package com.proyekta.app.project_lafic.model;

/**
 * Created by WINDOWS 10 on 23/04/2017.
 */

public class Member {
    private String MEMBER_ID;
    private String NAMA_MEMBER;
    private String PASSWORD_MEMBER;
    private String EMAIL_MEMBER;
    private String TELEPON;
    private String KELAMIN;
    private String NOMOR_ID;

    public Member(String NAMA_MEMBER, String PASSWORD_MEMBER, String EMAIL_MEMBER, String TELEPON, String KELAMIN, String NOMOR_ID) {
        this.NAMA_MEMBER = NAMA_MEMBER;
        this.PASSWORD_MEMBER = PASSWORD_MEMBER;
        this.EMAIL_MEMBER = EMAIL_MEMBER;
        this.TELEPON = TELEPON;
        this.KELAMIN = KELAMIN;
        this.NOMOR_ID = NOMOR_ID;
    }

    public String getMEMBER_ID() {
        return MEMBER_ID;
    }

    public String getNAMA_MEMBER() {
        return NAMA_MEMBER;
    }

    public String getPASSWORD_MEMBER() {
        return PASSWORD_MEMBER;
    }

    public String getEMAIL_MEMBER() {
        return EMAIL_MEMBER;
    }

    public String getTELEPON() {
        return TELEPON;
    }

    public String getKELAMIN() {
        return KELAMIN;
    }

    public String getNOMOR_ID() {
        return NOMOR_ID;
    }
}
