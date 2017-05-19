package app.proyekta.app.project_lafic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ervina Aprilia S on 02/05/2017.
 */

public class Token {

    @SerializedName("token")
    private String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
