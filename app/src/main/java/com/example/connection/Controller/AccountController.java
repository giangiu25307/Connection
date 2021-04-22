package com.example.connection.Controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.MediaType;
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

    public String login(String email,String password) throws IOException {

        // create your json here
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(),mediaType);

        Request request = new Request.Builder()
                .url("https://connexionauth.herokuapp.com/auth/login/")
                .post(body)
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

        // create your json here
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("email", mail);
            jsonObject.put("gender", gender);
            jsonObject.put("surname", surname);
            jsonObject.put("country", country);
           // jsonObject.put("city", city);
            jsonObject.put("birth", birth);
            jsonObject.put("number", number);
            jsonObject.put("porfilePic",profilePic);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(),mediaType);

        Request request = new Request.Builder()
                .url("https://connexionauth.herokuapp.com/auth/signup/")
                .post(body)
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
