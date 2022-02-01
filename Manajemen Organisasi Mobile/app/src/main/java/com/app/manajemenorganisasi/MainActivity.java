package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.manajemenorganisasi.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText et_username, et_password;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<User> userList;
    private ProgressDialog pd;
    private boolean dataLoaded = false;
    private SharedPreferences sharedPreferences;
    private User currentsUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_username = findViewById(R.id. et_username);
        et_password = findViewById(R.id. et_password);

        pd = new ProgressDialog(this);
        pd.setMessage("Menyiapkan..");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user_data");

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        userList = new ArrayList<>();

        if(sharedPreferences.getBoolean("isLoggedIn", false)){
            String username = sharedPreferences.getString("username", "");
            String role_name = sharedPreferences.getString("role_name", "");
            String role_group = sharedPreferences.getString("role_group", "");

            if(!username.equals("") && !role_name.equals("") && !role_group.equals("")){
                switch (role_name) {
                    case "sdm":
                        startActivity(new Intent(MainActivity.this, SDMActivity.class));
                        finish();
                        break;
                    case "sekretaris":
                        startActivity(new Intent(MainActivity.this, SekretarisActivity.class));
                        finish();
                        break;
                    case "bendahara":
                        startActivity(new Intent(MainActivity.this, BendaharaActivity.class));
                        finish();
                        break;
                    case "medinfo":
                        startActivity(new Intent(MainActivity.this, MedinfoActivity.class));
                        finish();
                        break;
                    case "krt":
                        startActivity(new Intent(MainActivity.this, KRTActivity.class));
                        finish();
                        break;
                }
            }
        }else{
            pd.show();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userList.clear();
                    dataLoaded = true;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        userList.add(dataSnapshot.getValue(User.class));
                    }
                    if(pd.isShowing()){
                        pd.cancel();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if(pd.isShowing()){
                        pd.cancel();
                    }
                    Toast.makeText(MainActivity.this, "Gagal menyiapkan, periksa koneksi internet lalu mulai ulang aplikasi!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        /*
        userList.add(new User("Sdmdpmft", "10021", "dpm", "sdm"));
        userList.add(new User("Sekretarisdpmft", "10022", "dpm", "sekretaris"));
        userList.add(new User("Bendaharadpmft", "10023", "dpm", "bendahara"));
        userList.add(new User("Krtdpmft", "10024", "dpm", "krt"));
        userList.add(new User("Medinfodpmft", "10025", "dpm", "medinfo"));

        userList.add(new User("Sdmbemft", "20021", "bem", "sdm"));
        userList.add(new User("Sekretarisbemft", "20022", "bem", "sekretaris"));
        userList.add(new User("Bendaharabemft", "20023", "bem", "bendahara"));
        userList.add(new User("Krtbemft", "20024", "bem", "krt"));
        userList.add(new User("Medinfobemft", "20025", "bem", "medinfo"));

        userList.add(new User("Sdmhmsft", "30021", "hms", "sdm"));
        userList.add(new User("Sekretarishmsft", "30022", "hms", "sekretaris"));
        userList.add(new User("Bendaharahmsft", "30023", "hms", "bendahara"));
        userList.add(new User("Krthmsft", "30024", "hms", "krt"));
        userList.add(new User("Medinfohmsft", "30025", "hms", "medinfo"));

        userList.add(new User("Sdmhmmft", "40021", "hmm", "sdm"));
        userList.add(new User("Sekretarishmmft", "40022", "hmm", "sekretaris"));
        userList.add(new User("Bendaharahmmft", "40023", "hmm", "bendahara"));
        userList.add(new User("Krthmmft", "40024", "hmm", "krt"));
        userList.add(new User("Medinfohmmft", "40025", "hmm", "medinfo"));

        userList.add(new User("Sdmhmeft", "50021", "hme", "sdm"));
        userList.add(new User("Sekretarishmeft", "50022", "hme", "sekretaris"));
        userList.add(new User("Bendaharahmeft", "50023", "hme", "bendahara"));
        userList.add(new User("Krthmeft", "50024", "hme", "krt"));
        userList.add(new User("Medinfohmeft", "50025", "hme", "medinfo"));

        for (User user : userList) {
            Map<String, String> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("password", user.getPassword());
            data.put("role_name", user.getRole_name());
            data.put("role_group", user.getRole_group());

            databaseReference.child(user.getUsername()).setValue(data);
        }
        */
    }

    public void admin(View view) {
        String username = et_username.getText().toString().toUpperCase();
        String password = et_password.getText().toString();

        if(!username.isEmpty() && !password.isEmpty()){
            login(username, password);
        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }

    }

    private void login(String username, String password){
        if(dataLoaded){
            boolean userExist = false;
            for (User user:userList) {
                if(user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)){
                    userExist = true;
                    sharedPreferences.edit().putString("username", user.getUsername()).apply();
                    sharedPreferences.edit().putString("role_name", user.getRole_name()).apply();
                    sharedPreferences.edit().putString("role_group", user.getRole_group()).apply();
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                    currentsUser = user;
                }
            }
            if(userExist){
                switch (currentsUser.getRole_name()) {
                    case "sdm":
                        startActivity(new Intent(MainActivity.this, SDMActivity.class));
                        finish();
                        break;
                    case "sekretaris":
                        startActivity(new Intent(MainActivity.this, SekretarisActivity.class));
                        finish();
                        break;
                    case "bendahara":
                        startActivity(new Intent(MainActivity.this, BendaharaActivity.class));
                        finish();
                        break;
                    case "medinfo":
                        startActivity(new Intent(MainActivity.this, MedinfoActivity.class));
                        finish();
                        break;
                    case "krt":
                        startActivity(new Intent(MainActivity.this, KRTActivity.class));
                        finish();
                        break;
                }
            }else {
                Toast.makeText(MainActivity.this, "Username atau Password salah!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Gagal menyiapkan, periksa koneksi internet lalu mulai ulang aplikasi!", Toast.LENGTH_SHORT).show();
        }
    }

    public void bukan_admin(View view) {
        if(dataLoaded){
            startActivity(new Intent(MainActivity.this, OrganisasiActivity.class));
            finish();
        }else{
            Toast.makeText(this, "Gagal menyiapkan, periksa koneksi internet lalu mulai ulang aplikasi!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
