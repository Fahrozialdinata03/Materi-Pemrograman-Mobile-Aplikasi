package com.example.Lokasiku_Fahrozi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    TextView tvLatitude, tvLongitude, tvAlamat;
    Button btnLokasi, btnSimpan;
    FusedLocationProviderClient fusedLocationClient;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi view
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvAlamat = findViewById(R.id.tvAlamat);
        btnLokasi = findViewById(R.id.btnLokasi);
        btnSimpan = findViewById(R.id.btnSimpan);

        // Inisialisasi FusedLocationProviderClient dan SharedPreferences
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences("LokasiPrefs", Context.MODE_PRIVATE);

        // Load lokasi yang tersimpan
        loadSavedLocation();

        // Button listeners
        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLocation();
            }
        });
    }

    private void getCurrentLocation() {
        // Cek permission lokasi
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permission jika belum ada
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        // Mendapatkan lokasi terakhir
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Update TextView dengan koordinat
                    tvLatitude.setText("Latitude: " + latitude);
                    tvLongitude.setText("Longitude: " + longitude);

                    // Geocoder untuk mendapatkan alamat dari koordinat
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            String address = addresses.get(0).getAddressLine(0);
                            tvAlamat.setText("Alamat: " + address);
                        } else {
                            tvAlamat.setText("Alamat: Tidak ditemukan");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        tvAlamat.setText("Alamat: Gagal mendapatkan alamat");
                    }

                    // Simpan otomatis ke SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("latitude", String.valueOf(latitude));
                    editor.putString("longitude", String.valueOf(longitude));
                    editor.putString("alamat", tvAlamat.getText().toString());
                    editor.apply();

                } else {
                    Toast.makeText(MainActivity.this, "Lokasi tidak ditemukan!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveLocation() {
        String lat = tvLatitude.getText().toString();
        String lon = tvLongitude.getText().toString();
        String alamat = tvAlamat.getText().toString();

        // Validasi apakah lokasi sudah didapatkan
        if (!lat.contains(":") || lat.equals("Latitude: -")) {
            Toast.makeText(this, "Silakan dapatkan lokasi terlebih dahulu!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan ke SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latitude", lat);
        editor.putString("longitude", lon);
        editor.putString("alamat", alamat);
        editor.apply();

        Toast.makeText(this, "Lokasi disimpan!", Toast.LENGTH_SHORT).show();
    }

    private void loadSavedLocation() {
        // Load data dari SharedPreferences
        String lat = sharedPreferences.getString("latitude", "Latitude: -");
        String lon = sharedPreferences.getString("longitude", "Longitude: -");
        String alamat = sharedPreferences.getString("alamat", "Alamat: -");

        // Set data ke TextView
        tvLatitude.setText(lat);
        tvLongitude.setText(lon);
        tvAlamat.setText(alamat);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Izin lokasi ditolak!", Toast.LENGTH_SHORT).show();
        }
    }
}