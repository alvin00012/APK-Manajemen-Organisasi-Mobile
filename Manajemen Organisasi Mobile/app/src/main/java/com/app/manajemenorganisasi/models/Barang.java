package com.app.manajemenorganisasi.models;

public class Barang {
    String nama, keterangan, tanggal, lokasi_gambar;

    public Barang() {
    }

    public Barang(String nama, String keterangan, String tanggal, String lokasi_gambar) {
        this.nama = nama;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
        this.lokasi_gambar = lokasi_gambar;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getLokasi_gambar() {
        return lokasi_gambar;
    }

    public void setLokasi_gambar(String lokasi_gambar) {
        this.lokasi_gambar = lokasi_gambar;
    }
}
