package com.app.manajemenorganisasi.interfaces;

import com.app.manajemenorganisasi.models.Berita;
import com.app.manajemenorganisasi.models.Surat;

public interface SuratInterface {
    void onDownload(Surat surat);
    void onDelete(Surat surat);
}
