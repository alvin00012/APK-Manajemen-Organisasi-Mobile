package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.manajemenorganisasi.adapters.KegiatanAdapter;
import com.app.manajemenorganisasi.interfaces.KegiatanInterface;
import com.app.manajemenorganisasi.models.Kegiatan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.model.ScrollMode;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.app.manajemenorganisasi.utils.DateUtil.month_name;

public class ListJadwalActivity extends AppCompatActivity {

    private CalendarView calendarView;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private List<Kegiatan> kegiatanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_aja);

        kegiatanList = new ArrayList<>();
        calendarView = findViewById(R.id.calendarView);

        setHeaderBinder();
        setFooterBinder();
        setDayBinder();

        calendarView.setup(
                (YearMonth.now()).minusMonths(10),
                (YearMonth.now()).plusMonths(10),
                WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());

        calendarView.setScrollMode(ScrollMode.PAGED);
        calendarView.scrollToMonth(YearMonth.now());

        mDatabase = FirebaseDatabase.getInstance();

        /*
        if(getIntent().hasExtra("group")){
            String group = getIntent().getStringExtra("group");
            mRef = mDatabase.getReference(group + "/kegiatan");
        }else{
            startActivity(new Intent(jadwal_aja.this, MainActivity.class));
            finish();
        }
        */
        mRef = mDatabase.getReference( "kegiatan");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kegiatanList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Kegiatan kegiatan = snapshot1.getValue(Kegiatan.class);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(kegiatan.getTanggalInDate());
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    int color = 0x100000 + (dayOfMonth * 0xffffff / 100);
                    String colorString = "#" + Integer.toHexString(color);
                    colorString = colorString.toUpperCase();
                    kegiatan.setColor(colorString);

                    kegiatanList.add(kegiatan);
                }
                setDayBinder();
                setFooterBinder();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFooterBinder(){
        calendarView.setMonthFooterBinder(new MonthHeaderFooterBinder<ViewContainer>() {
            @Override
            public ViewContainer create(View view) {
                return new ViewContainer(view);
            }

            @Override
            public void bind(ViewContainer viewContainer, CalendarMonth calendarMonth) {
                List<Kegiatan> kegiatanList1 = new ArrayList<>();

                for (Kegiatan kegiatan:kegiatanList) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(kegiatan.getTanggalInDate());
                    int month = calendar.get(Calendar.MONTH) + 1;

                    if(calendarMonth.getMonth() == month){
                        kegiatanList1.add(kegiatan);
                    }
                }

                RecyclerView container = viewContainer.getView().findViewById(R.id.calendarContainerEvent);
                TextView tv_keterangan = viewContainer.getView().findViewById(R.id.footerKeterangan);

                KegiatanAdapter kegiatanAdapter = new KegiatanAdapter(kegiatanList1, ListJadwalActivity.this);
                kegiatanAdapter.setKegiatanInterface(new KegiatanInterface() {
                    @Override
                    public void onClick(Kegiatan kegiatan) {
                        Intent intent = new Intent(ListJadwalActivity.this, JadwalActivity.class);
                        intent.putExtra("deskripsi", kegiatan.getDeskripsi());
                        intent.putExtra("nama", kegiatan.getNama());
                        intent.putExtra("tanggal", kegiatan.getTanggal());
                        startActivity(intent);
                    }
                });
                container.setLayoutManager(new LinearLayoutManager(ListJadwalActivity.this));
                container.setAdapter(kegiatanAdapter);

                if(kegiatanList1.size() == 0){
                    tv_keterangan.setText("Tidak Ada Kegiatan");
                }else{
                    tv_keterangan.setText("Keterangan");
                }
            }
        });
    }
    private void setDayBinder(){
        calendarView.setDayBinder(new DayBinder<ViewContainer>() {
            @Override
            public ViewContainer create(View view) {
                return new ViewContainer(view);
            }

            @Override
            public void bind(ViewContainer viewContainer, CalendarDay calendarDay) {
                TextView tv_day             = viewContainer.getView().findViewById(R.id.calendarDayText);

                View mask   = viewContainer.getView().findViewById(R.id.mask);
                tv_day.setText("" + calendarDay.getDate().getDayOfMonth());

                for (Kegiatan kegiatan : kegiatanList) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(kegiatan.getTanggalInDate());
                    int dayOfMonth, month, year;

                    dayOfMonth  = calendar.get(Calendar.DAY_OF_MONTH);
                    month       = calendar.get(Calendar.MONTH) + 1;
                    year        = calendar.get(Calendar.YEAR);

                    if(dayOfMonth == calendarDay.getDate().getDayOfMonth() && month == calendarDay.getDate().getMonth().getValue() && year == calendarDay.getDate().getYear()){
                        mask.setBackgroundColor(Color.parseColor(kegiatan.getColor()));
                        if(calendarDay.getOwner() == DayOwner.THIS_MONTH){
                            tv_day.setBackground(getResources().getDrawable(R.drawable.bg_border_cell));
                        }
                    }
                }

                if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {
                    tv_day.setTextColor(Color.BLACK);
                    mask.setVisibility(View.VISIBLE);
                } else {
                    tv_day.setTextColor(Color.GRAY);
                    mask.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setHeaderBinder(){
        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<ViewContainer>() {
            @Override
            public ViewContainer create(View view) {
                return new ViewContainer(view);
            }

            @Override
            public void bind(ViewContainer viewContainer, CalendarMonth calendarMonth) {
                TextView tv_month = viewContainer.getView().findViewById(R.id.calendarMonthText);
                String month = month_name[calendarMonth.getMonth()-1];
                tv_month.setText(month);
            }
        });
    }
}