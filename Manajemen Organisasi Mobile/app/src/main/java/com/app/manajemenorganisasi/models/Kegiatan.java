package com.app.manajemenorganisasi.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.ChronoLocalDate;
import java.util.Date;

public class Kegiatan {
    String deskripsi, nama, tanggal;
    String color;

    public Kegiatan() {
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public Kegiatan(String deskripsi, String nama, String tanggal) {
        this.deskripsi = deskripsi;
        this.nama = nama;
        this.tanggal = tanggal;
    }

    public Date getTanggalInDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return sdf.parse(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
