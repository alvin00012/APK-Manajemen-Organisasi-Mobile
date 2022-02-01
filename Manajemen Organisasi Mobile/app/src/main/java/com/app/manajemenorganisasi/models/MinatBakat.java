package com.app.manajemenorganisasi.models;

public class MinatBakat {
    String nama, jurusan, nim, kesenian, keteknikan, olahraga, alasan, timestamp;

    public MinatBakat() {
    }

    public MinatBakat(String nama, String jurusan, String nim, String kesenian, String keteknikan, String olahraga, String alasan, String timestamp) {
        this.nama = nama;
        this.jurusan = jurusan;
        this.nim = nim;
        this.kesenian = kesenian;
        this.keteknikan = keteknikan;
        this.olahraga = olahraga;
        this.alasan = alasan;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getKesenian() {
        return kesenian;
    }

    public void setKesenian(String kesenian) {
        this.kesenian = kesenian;
    }

    public String getKeteknikan() {
        return keteknikan;
    }

    public void setKeteknikan(String keteknikan) {
        this.keteknikan = keteknikan;
    }

    public String getOlahraga() {
        return olahraga;
    }

    public void setOlahraga(String olahraga) {
        this.olahraga = olahraga;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }
}
