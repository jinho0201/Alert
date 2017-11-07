package com.management.location.adapter;


public class item {

    String name;
    String bell;
    String wifi;


    public item(String name, String bell, String wifi) {
        this.name = name;

        this.bell = bell;
        this.wifi = wifi;
    }



    public void setName(String name) {
        this.name = name;
    }


    public void setBell(String bell) {
        this.bell = bell;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getName() {
        return name;
    }


    public String getBell() {
        return bell;
    }

    public String getWifi() {
        return wifi;
    }
}
