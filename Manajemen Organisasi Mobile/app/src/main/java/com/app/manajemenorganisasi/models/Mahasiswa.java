package com.app.manajemenorganisasi.models;

public class Mahasiswa {
    String nama, nomor, jurusan, tanggal_ditambahkan, lokasi_file;

    public Mahasiswa() {
    }

    public Mahasiswa(String nama, String nomor, String jurusan, String tanggal_ditambahkan, String lokasi_file) {
        this.nama = nama;
        this.nomor = nomor;
        this.jurusan = jurusan;
        this.tanggal_ditambahkan = tanggal_ditambahkan;
        this.lokasi_file = lokasi_file;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getTanggal_ditambahkan() {
        return tanggal_ditambahkan;
    }

    public void setTanggal_ditambahkan(String tanggal_ditambahkan) {
        this.tanggal_ditambahkan = tanggal_ditambahkan;
    }

    public String getLokasi_file() {
        return lokasi_file;
    }

    public void setLokasi_file(String lokasi_file) {
        this.lokasi_file = lokasi_file;
    }
}

