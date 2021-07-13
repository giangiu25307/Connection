package com.example.connection.Model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

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
    String idUser;
    String password;
    String birth;
    String publicKey;
    InetAddress inetAddressWlan;
    InetAddress inetAddressP2P;

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
        this.profilePic = "";
        this.password = "";
        this.birth = "";
        this.publicKey = "";
    }

    public User(String idUser, String username, String mail, String gender, String name, String surname, String country, String city, String number, String birth, String profilePic) {
        this.idUser = idUser;
        this.username = username;
        this.mail = mail;
        this.gender = gender;
        this.name = name;
        this.surname = surname;
        this.country = country;
        this.city = city;
        this.number = number;
        this.birth = birth;
        this.profilePic = profilePic;
        this.password = "";
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public InetAddress getInetAddressWlan() {
        return inetAddressWlan;
    }

    public void setInetAddressWlan(String inetAddressWlan) throws UnknownHostException {
        this.inetAddressWlan = InetAddress.getByName(inetAddressWlan);
    }

    public InetAddress getInetAddressP2P() {
        return inetAddressP2P;
    }

    public void setInetAddressP2P(String inetAddressP2P) throws UnknownHostException {
        this.inetAddressP2P = InetAddress.getByName(inetAddressP2P);
    }

    public String getAllWlan() {
        return idUser + "£€" + inetAddressWlan.getHostAddress() + "£€" + username + "£€" + mail + "£€" + gender + "£€" + name + "£€" + surname + "£€" + country + "£€" + city + "£€" + birth + "£€" + profilePic + "£€" + publicKey;
    }

    public String getAllP2P() {
        return idUser + "£€" + inetAddressP2P.getHostAddress() + "£€" + username + "£€" + mail + "£€" + gender + "£€" + name + "£€" + surname + "£€" + country + "£€" + city + "£€" + birth + "£€" + profilePic + "£€" + publicKey;
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

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAge() {
        if(birth.isEmpty())return "";
        else{
            String age="";
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            try {
               age  = ""+Period.between(convertToLocalDateViaInstant(formatter.parse(birth)), LocalDateTime.now().toLocalDate()).getYears();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return age;
        }
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
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
                ", age='" + getAge() + '\'' +
                '}';
    }

}
