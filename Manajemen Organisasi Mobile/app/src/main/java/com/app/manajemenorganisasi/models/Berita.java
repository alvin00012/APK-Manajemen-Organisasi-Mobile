package com.app.manajemenorganisasi.models;

public class Berita {
    String judul, subjudul, isi, caption, lokasi_gambar, dipublikasikan;

    public Berita() {
    }

    public String getDipublikasikan() {
        return dipublikasikan;
    }

    public void setDipublikasikan(String dipublikasikan) {
        this.dipublikasikan = dipublikasikan;
    }

    public Berita(String judul, String subjudul, String isi, String caption, String lokasi_gambar) {
        this.judul = judul;
        this.subjudul = subjudul;
        this.isi = isi;
        this.caption = caption;
        this.lokasi_gambar = lokasi_gambar;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getSubjudul() {
        return subjudul;
    }

    public void setSubjudul(String subjudul) {
        this.subjudul = subjudul;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLokasi_gambar() {
        return lokasi_gambar;
    }

    public void setLokasi_gambar(String lokasi_gambar) {
        this.lokasi_gambar = lokasi_gambar;
    }
}
