package com.example.connection.Model;

import java.net.InetAddress;

public class User {


    String idphone, username, password, mail, gender, name, surname, country, city;
    int number, age;
    InetAddress inetAddress;

    public User(String idphone, String username, String password, String mail, String gender, String name, String surname, String country, String city, int number, int age) {
        this.idphone = idphone;
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.gender = gender;
        this.name = name;
        this.surname = surname;
        this.country = country;
        this.city = city;
        this.number = number;
        this.age = age;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public String getAll(){
        return idphone+";"+mail+";"+gender+";"+name+";"+surname+";"+country+";"+city+";"+age+";"+inetAddress;
    }

    public String getIdphone() {
        return idphone;
    }

    public void setIdphone(String idphone) {
        this.idphone = idphone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
