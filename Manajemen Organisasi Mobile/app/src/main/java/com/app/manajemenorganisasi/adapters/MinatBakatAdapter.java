package com.app.manajemenorganisasi.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.app.manajemenorganisasi.R;
import com.app.manajemenorganisasi.interfaces.BeritaInterface;
import com.app.manajemenorganisasi.interfaces.MinatBakatInterface;
import com.app.manajemenorganisasi.models.Berita;
import com.app.manajemenorganisasi.models.MinatBakat;
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

import static com.app.manajemenorganisasi.utils.StringUtil.convertToSentenceCase;

public class MinatBakatAdapter extends RecyclerView.Adapter<MinatBakatAdapter.ViewHolder> {

    private List<MinatBakat> minatBakatList;
    private Context context;
    private boolean hanyaDataMhs = false;
    private MinatBakatInterface minatBakatInterface;

    public MinatBakatAdapter(List<MinatBakat> minatBakatList, Context context) {
        this.minatBakatList = minatBakatList;
        this.context = context;
    }

    public void setMinatBakatInterface(MinatBakatInterface minatBakatInterface) {
        this.minatBakatInterface = minatBakatInterface;
    }

    public void setHanyaDataMhs(boolean hanyaDataMhs) {
        this.hanyaDataMhs = hanyaDataMhs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.item_minat_bakat, parent, false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position > 0){
            MinatBakat minatBakat = minatBakatList.get(position);
            holder.tv_no.setText(String.valueOf(position));
            holder.tv_nama.setText(minatBakat.getNama());
            holder.tv_jurusan.setText(minatBakat.getJurusan());
            holder.tv_nim.setText(minatBakat.getNim());
            holder.tv_minat.setText(String.format("%s\n%s\n%s", minatBakat.getKesenian(), minatBakat.getOlahraga(), minatBakat.getKeteknikan()));
            holder.tv_alasan.setText(minatBakat.getAlasan());
            holder.btn_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(minatBakatInterface != null){
                        minatBakatInterface.onDelete(minatBakat);
                    }
                }
            });
            holder.tv_timestamp.setText(minatBakat.getTimestamp());
        }else{
            holder.btn_hapus.setVisibility(View.INVISIBLE);
        }

        if(hanyaDataMhs){
            holder.tv_minat.setVisibility(View.GONE);
            holder.tv_alasan.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return minatBakatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_no, tv_nama, tv_jurusan, tv_nim, tv_minat, tv_alasan, tv_timestamp;
        ImageButton btn_hapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_hapus   = itemView.findViewById(R.id.btn_minat_bakat_hapus);
            tv_no       = itemView.findViewById(R.id.tv_minat_bakat_no);
            tv_nama     = itemView.findViewById(R.id.tv_minat_bakat_nama);
            tv_jurusan  = itemView.findViewById(R.id.tv_minat_bakat_jurusan);
            tv_nim      = itemView.findViewById(R.id.tv_minat_bakat_nim);
            tv_minat    = itemView.findViewById(R.id.tv_minat_bakat_minat);
            tv_alasan   = itemView.findViewById(R.id.tv_minat_bakat_alasan);
            tv_timestamp= itemView.findViewById(R.id.tv_minat_bakat_datetime);
        }
    }

}
