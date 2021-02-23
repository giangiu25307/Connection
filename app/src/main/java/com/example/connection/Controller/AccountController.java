package com.example.connection.Controller;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class AccountController {

    OkHttpClient client;
    public AccountController() {
         client= new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .build();
    }

    public String login(String username,String password) throws IOException {

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                //.add("yagosaf","yagosaf")
                .build();

        Request request = new Request.Builder()
                .url("https://connexionauth.herokuapp.com/auth/login/")
                .post(formBody)
                .build();


        Call call = client.newCall(request);
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String register(String password,String username,String mail,String gender,String name,String surname,String country,String city,String birth,String number,String profilePic) throws IOException {

        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("username", username)
                .add("password", password)
                .add("mail", mail)
                .add("gender", gender)
                .add("surname", surname)
                .add("country", country)
                .add("city", city)
                .add("birth", birth)
                .add("number", number)
                .add("profilePic",profilePic)
                //.add("yagosaf","yagosaf")
                .build();

        Request request = new Request.Builder()
                .url("https://connexionauth.herokuapp.com/auth/signup/")
                .post(formBody)
                .build();

        Call call = client.newCall(request);
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

}
