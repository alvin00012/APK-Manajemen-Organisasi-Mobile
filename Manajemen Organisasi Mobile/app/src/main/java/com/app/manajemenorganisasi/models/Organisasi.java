package com.app.manajemenorganisasi.models;

public class Organisasi {
    String pengertian, tanggal, deskripsi, lokasi_gambar;

    public Organisasi() {
    }

    public Organisasi(String pengertian, String tanggal, String deskripsi, String lokasi_gambar) {
        this.pengertian = pengertian;
        this.tanggal = tanggal;
        this.deskripsi = deskripsi;
        this.lokasi_gambar = lokasi_gambar;
    }

    public String getPengertian() {
        return pengertian;
    }

    public void setPengertian(String pengertian) {
        this.pengertian = pengertian;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLokasi_gambar() {
        return lokasi_gambar;
    }

    public void setLokasi_gambar(String lokasi_gambar) {
        this.lokasi_gambar = lokasi_gambar;
    }
}
