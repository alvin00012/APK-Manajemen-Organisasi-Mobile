package com.app.manajemenorganisasi.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.app.manajemenorganisasi.R;
import com.app.manajemenorganisasi.interfaces.BeritaInterface;
import com.app.manajemenorganisasi.models.Berita;
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

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.app.manajemenorganisasi.utils.DateUtil.dow_name;
import static com.app.manajemenorganisasi.utils.DateUtil.month_name;
import static com.app.manajemenorganisasi.utils.StringUtil.convertToSentenceCase;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.ViewHolder> {

    private List<Berita> beritaList;
    private Context context;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private BeritaInterface beritaInterface;

    public BeritaAdapter(List<Berita> beritaList, Context context) {
        this.beritaList = beritaList;
        this.context = context;
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void setBeritaInterface(BeritaInterface beritaInterface) {
        this.beritaInterface = beritaInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.item_berita, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Berita berita = beritaList.get(position);
        String judul = convertToSentenceCase(berita.getJudul());
        holder.tv_judul.setText(judul);

        // Jum'at, 04 Juni 2021 09:50
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        try {
            Date date = sdf.parse(berita.getDipublikasikan());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String dow = dow_name[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            int dom = calendar.get(Calendar.DAY_OF_MONTH);

            String month = month_name[calendar.get(Calendar.MONTH) - 1];
            int year = calendar.get(Calendar.YEAR);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            holder.tv_dipublikasikan.setText(String.format("%s, %02d %s %04d %02d:%02d", dow, dom, month, year, hour, minute));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.shimmerFrameLayout.startShimmerAnimation();
        storageReference.child("images/" + berita.getLokasi_gambar()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.shimmerFrameLayout.stopShimmerAnimation();
                                holder.iv_gambar.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.shimmerFrameLayout.stopShimmerAnimation();
                                holder.iv_gambar.setVisibility(View.VISIBLE);
                                return false;
                            }
                        })
                        .into(holder.iv_gambar);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.shimmerFrameLayout.stopShimmerAnimation();
            }
        });

        holder.itemParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(beritaInterface != null){
                    beritaInterface.onClick(berita);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return beritaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_judul, tv_dipublikasikan;
        ImageView iv_gambar;
        ShimmerFrameLayout shimmerFrameLayout;
        LinearLayout itemParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_judul            = itemView.findViewById(R.id.tv_item_judul);
            iv_gambar           = itemView.findViewById(R.id.iv_item_gambar);
            tv_dipublikasikan   = itemView.findViewById(R.id.tv_item_dipublikasikan);
            shimmerFrameLayout  = itemView.findViewById(R.id.shimmer_layout);
            itemParent          = itemView.findViewById(R.id.item_parent);
        }
    }

}
