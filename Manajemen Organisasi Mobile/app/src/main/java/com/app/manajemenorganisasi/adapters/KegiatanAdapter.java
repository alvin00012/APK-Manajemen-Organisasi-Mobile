package com.app.manajemenorganisasi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.app.manajemenorganisasi.R;
import com.app.manajemenorganisasi.interfaces.BeritaInterface;
import com.app.manajemenorganisasi.interfaces.KegiatanInterface;
import com.app.manajemenorganisasi.models.Berita;
import com.app.manajemenorganisasi.models.Kegiatan;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.app.manajemenorganisasi.utils.DateUtil.dow_name;
import static com.app.manajemenorganisasi.utils.DateUtil.month_name;
import static com.app.manajemenorganisasi.utils.StringUtil.convertToSentenceCase;

public class KegiatanAdapter extends RecyclerView.Adapter<KegiatanAdapter.ViewHolder> {

    private List<Kegiatan> kegiatanList;
    private Context context;
    private KegiatanInterface kegiatanInterface;

    public KegiatanAdapter(List<Kegiatan> kegiatanList, Context context) {
        this.kegiatanList = kegiatanList;
        this.context = context;
    }

    public void setKegiatanInterface(KegiatanInterface kegiatanInterface) {
        this.kegiatanInterface = kegiatanInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.item_kegiatan, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Kegiatan kegiatan = kegiatanList.get(position);
        holder.tv_judul.setText(kegiatan.getNama());
        if(kegiatan.getColor() != null){
            holder.view_warna.setBackgroundColor(Color.parseColor(kegiatan.getColor()));
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(kegiatanInterface != null){
                    kegiatanInterface.onClick(kegiatan);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return kegiatanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_judul;
        View view_warna;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view_warna  = itemView.findViewById(R.id.view_warna_kegiatan);
            tv_judul    = itemView.findViewById(R.id.tv_item_judul_kegiatan);
            container   = itemView.findViewById(R.id.item_parent);
        }
    }
}
