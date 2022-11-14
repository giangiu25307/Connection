package com.ConnectionProject.connection.Controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
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

    /**
     * Check on the server if my account exist
     *
     * @param email    email to check
     * @param password password to check
     */
    public Response login(String email,String password) throws IOException {

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
                .url("https://isconnection.herokuapp.com/auth/login/")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sign my account on the server
     *
     * @param password   password to be signed
     * @param username   username to be signed
     * @param mail       mail to be signed
     * @param gender     gender to be signed
     * @param name       name to be signed
     * @param surname    surname to be signed
     * @param country    country to be signed
     * @param city       city to be signed
     * @param birth      birth to be signed
     * @param number     number to be signed
     * @param profilePic picture to be signed
     */
    public Response register(String password,String username,String mail,String gender,String name,String surname,String country,String city,String birth,String number,String profilePic) throws IOException {

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
            jsonObject.put("city", city);
            jsonObject.put("birthday", birth);
            jsonObject.put("phoneNumber", number);
            jsonObject.put("phoneNumber", number);
            jsonObject.put("profilePic",profilePic);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(),mediaType);

        Request request = new Request.Builder()
                .url("https://isconnection.herokuapp.com/auth/signup/")
                .post(body)
                .build();

        Call call = client.newCall(request);
        try {
            Response response = client.newCall(request).execute();
            return response;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Request to delete my account on the server
     *
     * @param id id of the account to be deleted
     */
    public Response deleteAccount(String id) throws IOException {

        // create your json here
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deleteId", id);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(),mediaType);

        Request request = new Request.Builder()
                .url("https://isconnection.herokuapp.com/auth/delete/")
                .post(body)
                .build();

        Call call = client.newCall(request);
        try {
            Response response = client.newCall(request).execute();
            return response;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
