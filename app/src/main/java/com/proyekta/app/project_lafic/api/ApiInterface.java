package com.proyekta.app.project_lafic.api;

import com.proyekta.app.project_lafic.model.Item;
import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.model.Login;
import com.proyekta.app.project_lafic.model.Member;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by WINDOWS 10 on 24/04/2017.
 */

public interface ApiInterface {

    @POST("member")
    Call<Login> doLogin(@Body Login login);

    @POST("member")
    @FormUrlEncoded
    Call<Member> doRegister(@Field("NAMA_MEMBER") String NAMA_MEMBER,
                            @Field("PASSWORD_MEMBER") String PASSWORD_MEMBER,
                            @Field("EMAIL_MEMBER") String EMAIL_MEMBER,
                            @Field("TELEPON") String TELEPON,
                            @Field("KELAMIN") String KELAMIN,
                            @Field("NOMOR_ID") String NOMOR_ID);

    @GET("barang")
    Call<Item> getAllBarang();

    @GET("barang?BARANG_ID={id}")
    Call<Item> getAllBarang(@Path("id") String id);

    @POST("barang")
    @FormUrlEncoded
    Call<Item> doSubmit(@Field("BARANG_ID") String BARANG_ID,
                        @Field("MEMBER_ID") String MEMBER_ID,
                        @Field("ID_KATEGORY") String ID_KATEGORY,
                        @Field("NAMA_BARANG") String NAMA_BARANG,
                        @Field("STATUS") String STATUS,
                        @Field("WARNA_BARANG") String WARNA_BARANG,
                        @Field("TIPE_BARANG") String TIPE_BARANG,
                        @Field("QRCODE") String QRCODE);

    @GET("kategoribarang")
    Call<List<KategoriBarang>> getKategoriBarang();
}
