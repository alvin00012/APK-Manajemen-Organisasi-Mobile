package com.app.manajemenorganisasi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.app.manajemenorganisasi.R;
import com.app.manajemenorganisasi.interfaces.BarangInterface;
import com.app.manajemenorganisasi.models.Barang;
import com.app.manajemenorganisasi.models.Surat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {

    private List<Barang> barangList;
    private Context context;
    private BarangInterface barangInterface;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    public void setBarangInterface(BarangInterface barangInterface) {
        this.barangInterface = barangInterface;
    }

    public BarangAdapter(List<Barang> barangList, Context context) {
        this.barangList = barangList;
        this.context = context;
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.item_barang, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position > 0){
            Barang barang = barangList.get(position);
            holder.tv_no.setText(String.valueOf(position));
            holder.tv_nama.setText(barang.getNama());
            holder.tv_keterangan.setText(barang.getKeterangan());
            holder.tv_tanggal.setText(barang.getTanggal());
            holder.tv_head_gambar.setVisibility(View.GONE);
            holder.iv_gambar.setVisibility(View.VISIBLE);
            holder.tv_hapus.setText("Hapus");
            holder.tv_hapus.setTextColor(Color.BLUE);
            holder.tv_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(barangInterface != null){
                        barangInterface.onDelete(barang);
                    }
                }
            });

            storageReference.child("images/" + barang.getLokasi_gambar()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    holder.iv_gambar.setVisibility(View.VISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    holder.iv_gambar.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            })
                            .into(holder.iv_gambar);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_no, tv_nama, tv_keterangan, tv_tanggal, tv_head_gambar, tv_hapus;

        ImageView iv_gambar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_no           = itemView.findViewById(R.id.tv_barang_no);
            tv_tanggal      = itemView.findViewById(R.id.tv_barang_tanggal);
            tv_nama         = itemView.findViewById(R.id.tv_barang_nama);
            tv_keterangan   = itemView.findViewById(R.id.tv_barang_keterangan);
            tv_head_gambar  = itemView.findViewById(R.id.tv_barang_gambar_head);
            tv_hapus        = itemView.findViewById(R.id.tv_barang_hapus);

            iv_gambar       = itemView.findViewById(R.id.iv_barang_gambar);
        }
    }

}
