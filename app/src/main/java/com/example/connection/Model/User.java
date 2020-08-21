package com.example.connection.Model;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class User {

    String username;
    String mail;
    String gender;
    String name;
    String surname;
    String country;
    String city;
    String profilePic;
    String number;
    String age;
    String idUser;
    String password;
    InetAddress inetAddress;

    public User() {
        this.idUser = "";
        this.username = "";
        this.mail = "";
        this.gender = "";
        this.name = "";
        this.surname = "";
        this.country = "";
        this.city = "";
        this.number = "";
        this.age = "";
        this.profilePic = "";
        this.password = "";
    }

    public User(String idUser, String username, String mail, String gender, String name, String surname, String country, String city, String number, String age, String profilePic) {
        this.idUser = idUser;
        this.username = username;
        this.mail = mail;
        this.gender = gender;
        this.name = name;
        this.surname = surname;
        this.country = country;
        this.city = city;
        this.number = number;
        this.age = age;
        this.profilePic = profilePic;
        this.password = "";
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(String inetAddress) throws UnknownHostException {
        this.inetAddress = InetAddress.getByName(inetAddress);
    }

    public String getAll() {
        return idUser + "£€" + inetAddress + "£€" + username + "£€" + mail + "£€" + gender + "£€" + name + "£€" + surname + "£€" + country + "£€" + city + "£€" + age + "£€" + profilePic;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
