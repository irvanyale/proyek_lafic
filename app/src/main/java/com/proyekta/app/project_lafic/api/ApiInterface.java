package com.proyekta.app.project_lafic.api;

import com.proyekta.app.project_lafic.model.Barang;
import com.proyekta.app.project_lafic.model.BarangHilang;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.model.Login;
import com.proyekta.app.project_lafic.model.Member;
import com.proyekta.app.project_lafic.model.Pesan;
import com.proyekta.app.project_lafic.model.SuksesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Ervina Aprilia S on 13/05/2017.
 */

public interface ApiInterface {

    @POST("member")
    @FormUrlEncoded
    Call<Member> doRegister(@Field("NAMA_MEMBER") String NAMA_MEMBER,
                            @Field("PASSWORD_MEMBER") String PASSWORD_MEMBER,
                            @Field("EMAIL_MEMBER") String EMAIL_MEMBER,
                            @Field("TELEPON") String TELEPON,
                            @Field("KELAMIN") String KELAMIN,
                            @Field("NOMOR_ID") String NOMOR_ID,
                            @Field("QRCODE") String QRCODE);

    @PUT("member")
    Call<Member> doUpdateProfile(@Body Member member);

    @GET("barang")
    Call<List<Barang>> getAllBarang(@Query("MEMBER_ID") String id);

    @PUT("barang")
    Call<Barang> doUpdateBarang(@Body Barang Barang);

    @POST("barang")
    @FormUrlEncoded
    Call<Barang> doSubmit(@Field("BARANG_ID") String BARANG_ID,
                        @Field("MEMBER_ID") String MEMBER_ID,
                        @Field("ID_KATEGORY") String ID_KATEGORY,
                        @Field("JENIS_BARANG") String JENIS_BARANG,
                        @Field("MERK_BARANG") String MERK_BARANG,
                        @Field("STATUS") String STATUS,
                        @Field("WARNA_BARANG") String WARNA_BARANG,
                        @Field("KETERANGAN") String KETERANGAN,
                        @Field("QRCODE") String QRCODE);

    @GET("barang")
    Call<Barang> getBarang(@Query("BARANG_ID") String id);

    @GET("kategoribarang")
    Call<List<KategoriBarang>> getKategoriBarang();

    @GET("barangHilang")
    Call<List<BarangHilang>> getAllBarangHilang();

    @POST("barangHilang")
    Call<BarangHilang> postBarangHilang(@Body BarangHilang barangHilang);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "barangHilang", hasBody = true)
    Call<SuksesResponse> deleteBarangHilang(@Field("BARANG_ID") String BARANG_ID);

    @POST("pesan")
    Call<Pesan> sendMessage(@Body Pesan pesan);
}
