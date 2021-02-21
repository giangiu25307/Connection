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
                .build();

        Request request = new Request.Builder()
                .url("www.connection.it" + "/login")
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

    public String register(String password,String username,String mail,String gender,String name,String surname,String country,String city,String birth,String number, String valletParameter) throws IOException {

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("mail", mail)
                .add("gender", gender)
                .add("name", name)
                .add("surname", surname)
                .add("country", country)
                .add("city", city)
                .add("birth", birth)
                .add("number", number)
                .add("yagosaf","yagosaf")
                .build();

        Request request = new Request.Builder()
                .url("www.connection.it" + "/register")
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

}
