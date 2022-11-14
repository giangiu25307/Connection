package com.ConnectionProject.connection.Model;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class UserPlus {

    private String id, mail, companyName, name, surname, country, city, street, number, SDI, VATNumber, promotionPage, promotionMessage;

    public UserPlus(String id, String mail, String companyName, String name, String surname, String country, String city, String street, String number, String SDI, String VATNumber, String promotionPage, String promotionMessage) {
        this.id = id;
        this.mail = mail;
        this.companyName = companyName;
        this.name = name;
        this.surname = surname;
        this.country = country;
        this.city = city;
        this.street = street;
        this.number = number;
        this.SDI = SDI;
        this.VATNumber = VATNumber;
        this.promotionPage = promotionPage;
        this.promotionMessage = promotionMessage;
    }

    public UserPlus() {
        this.id = "";
        this.mail = "";
        this.companyName = "";
        this.name = "";
        this.surname = "";
        this.country = "";
        this.city = "";
        this.street = "";
        this.number = "";
        this.SDI = "";
        this.VATNumber = "";
        this.promotionPage = "";
        this.promotionMessage = "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setSDI(String SDI) {
        this.SDI = SDI;
    }

    public void setVATNumber(String VATNumber) {
        this.VATNumber = VATNumber;
    }

    public void setPromotionPage(String promotionPage) {
        this.promotionPage = promotionPage;
    }

    public void setPromotionMessage(String promotionMessage) {
        this.promotionMessage = promotionMessage;
    }

    public String getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getSDI() {
        return SDI;
    }

    public String getVATNumber() {
        return VATNumber;
    }

    public String getPromotionPage(Context context) {
        InputStream inputStream=new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        try {
            inputStream =context.getAssets().open("www/index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            promotionPage=new String(IOUtils.toByteArray(inputStream),"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return promotionPage;
    }

    public String getPromotionMessage() {
        return promotionMessage;
    }

    public String getAll() {
        return id + "£€" + mail + "£€" + companyName + "£€" + name + "£€" + surname + "£€" + country + "£€" + city + "£€" + street + "£€" + number + "£€" + SDI + "£€" + VATNumber + "£€" + promotionPage + "£€" + promotionMessage;
    }

    @Override
    public String toString() {
        return "UserPlus{" +
                "id='" + id + '\'' +
                ", mail='" + mail + '\'' +
                ", companyName='" + companyName + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", SDI='" + SDI + '\'' +
                ", VATNumber='" + VATNumber + '\'' +
                ", promotionPage='" + promotionPage + '\'' +
                ", promotionMessage='" + promotionMessage + '\'' +
                '}';
    }
}
