package com.app.manajemenorganisasi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.manajemenorganisasi.R;
import com.app.manajemenorganisasi.interfaces.SuratInterface;
import com.app.manajemenorganisasi.models.MinatBakat;
import com.app.manajemenorganisasi.models.Surat;

import java.util.List;

public class SuratAdapter extends RecyclerView.Adapter<SuratAdapter.ViewHolder> {

    private List<Surat> suratList;
    private Context context;
    private SuratInterface suratInterface;

    public SuratAdapter(List<Surat> suratList, Context context) {
        this.suratList = suratList;
        this.context = context;
    }

    public void setSuratInterface(SuratInterface suratInterface) {
        this.suratInterface = suratInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.item_surat, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position > 0){
            Surat surat = suratList.get(position);
            holder.tv_no.setText(String.valueOf(position));
            holder.tv_judul.setText(surat.getJudul());
            holder.tv_nomor.setText(surat.getNomor());
            holder.tv_datetime.setText(surat.getDatetime());
            holder.tv_download.setText("Download");
            holder.tv_download.setTextColor(Color.BLUE);
            holder.tv_download.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            holder.tv_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(suratInterface != null){
                        suratInterface.onDownload(surat);
                    }
                }
            });

            holder.tv_hapus.setText("Hapus");
            holder.tv_hapus.setTextColor(Color.BLUE);
            holder.tv_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(suratInterface != null){
                        suratInterface.onDelete(surat);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return suratList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_no, tv_judul, tv_nomor, tv_datetime, tv_download, tv_hapus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_no       = itemView.findViewById(R.id.tv_surat_no);
            tv_judul    = itemView.findViewById(R.id.tv_surat_judul);
            tv_nomor    = itemView.findViewById(R.id.tv_surat_nomor);
            tv_datetime = itemView.findViewById(R.id.tv_surat_waktu);
            tv_download = itemView.findViewById(R.id.tv_surat_download);
            tv_hapus    = itemView.findViewById(R.id.tv_surat_hapus);
        }
    }

}
