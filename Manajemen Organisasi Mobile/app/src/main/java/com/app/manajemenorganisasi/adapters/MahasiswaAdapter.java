package com.app.manajemenorganisasi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.manajemenorganisasi.R;
import com.app.manajemenorganisasi.interfaces.MahasiswaInterface;
import com.app.manajemenorganisasi.interfaces.MinatBakatInterface;
import com.app.manajemenorganisasi.models.Mahasiswa;
import com.app.manajemenorganisasi.models.MinatBakat;

import java.util.List;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.ViewHolder> {

    private List<Mahasiswa> mahasiswaList;
    private Context context;
    private MahasiswaInterface mahasiswaInterface;

    public MahasiswaAdapter(List<Mahasiswa> mahasiswaList, Context context) {
        this.mahasiswaList = mahasiswaList;
        this.context = context;
    }

    public void setMahasiswaInterface(MahasiswaInterface mahasiswaInterface) {
        this.mahasiswaInterface = mahasiswaInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.item_mahasiswa, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position > 0){
            Mahasiswa mahasiswa = mahasiswaList.get(position);
            holder.tv_no.setText(String.valueOf(position));
            holder.tv_nama.setText(mahasiswa.getNama());
            holder.tv_nomor.setText(mahasiswa.getNomor());
            holder.tv_jurusan.setText(mahasiswa.getJurusan());
            holder.tv_tanggal.setText(mahasiswa.getTanggal_ditambahkan());
            holder.tv_hapus.setText("Hapus");
            holder.tv_hapus.setTextColor(Color.BLUE);
            holder.tv_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mahasiswaInterface != null){
                        mahasiswaInterface.onDelete(mahasiswa);
                    }
                }
            });
            holder.tv_download.setText("Download");
            holder.tv_download.setTextColor(Color.BLUE);
            holder.tv_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mahasiswaInterface != null){
                        mahasiswaInterface.onDownload(mahasiswa);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mahasiswaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_no, tv_nama, tv_nomor, tv_jurusan, tv_tanggal, tv_download, tv_hapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_no = itemView.findViewById(R.id.tv_mahasiswa_no);
            tv_nama = itemView.findViewById(R.id.tv_mahasiswa_nama);
            tv_nomor = itemView.findViewById(R.id.tv_mahasiswa_nomor);
            tv_jurusan = itemView.findViewById(R.id.tv_mahasiswa_jurusan);
            tv_tanggal = itemView.findViewById(R.id.tv_mahasiswa_datetime);
            tv_download = itemView.findViewById(R.id.tv_mahasiswa_download);
            tv_hapus = itemView.findViewById(R.id.tv_mahasiswa_hapus);
        }

    }

}
