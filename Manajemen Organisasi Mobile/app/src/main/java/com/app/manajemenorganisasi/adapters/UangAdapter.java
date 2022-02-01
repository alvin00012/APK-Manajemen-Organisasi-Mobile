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
import com.app.manajemenorganisasi.interfaces.UangInterface;
import com.app.manajemenorganisasi.models.Surat;
import com.app.manajemenorganisasi.models.Uang;

import java.util.List;

public class UangAdapter extends RecyclerView.Adapter<UangAdapter.ViewHolder> {

    private List<Uang> uangList;
    private Context context;
    private UangInterface uangInterface;

    public UangAdapter(List<Uang> uangList, Context context) {
        this.uangList = uangList;
        this.context = context;
    }

    public void setUangInterface(UangInterface uangInterface) {
        this.uangInterface = uangInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.item_uang, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position > 0){
            Uang uang = uangList.get(position);
            holder.tv_no.setText(String.valueOf(position));
            holder.tv_keterangan.setText(uang.getKeterangan());
            holder.tv_jumlah.setText("Rp." + uang.getJumlah());
            holder.tv_datetime.setText(uang.getTimestamp());
            holder.tv_hapus.setTextColor(Color.BLUE);
            holder.tv_hapus.setText("Hapus");
            holder.tv_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(uangInterface != null){
                        uangInterface.onDelete(uang);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return uangList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_no, tv_keterangan, tv_jumlah, tv_datetime, tv_hapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_no           = itemView.findViewById(R.id.tv_uang_no);
            tv_keterangan   = itemView.findViewById(R.id.tv_uang_keterangan);
            tv_jumlah       = itemView.findViewById(R.id.tv_uang_jumlah);
            tv_datetime     = itemView.findViewById(R.id.tv_uang_timestamp);
            tv_hapus        = itemView.findViewById(R.id.tv_uang_hapus);
        }
    }

}
