package com.app.manajemenorganisasi.models;

public class Surat {
    String judul, nomor, lokasi_surat, datetime;

    public Surat() {
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Surat(String judul, String nomor, String lokasi_surat) {
        this.judul = judul;
        this.nomor = nomor;
        this.lokasi_surat = lokasi_surat;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getLokasi_surat() {
        return lokasi_surat;
    }

    public void setLokasi_surat(String lokasi_surat) {
        this.lokasi_surat = lokasi_surat;
    }
}
