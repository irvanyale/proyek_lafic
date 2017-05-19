package app.proyekta.app.project_lafic.api;

import app.proyekta.app.project_lafic.model.Login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Ervina Aprilia S on 13/05/2017.
 */

public interface AuthClient {

    @POST("auth/token")
    @FormUrlEncoded
    Call<Login> createToken(@Field("username") String username, @Field("password") String password);
}
