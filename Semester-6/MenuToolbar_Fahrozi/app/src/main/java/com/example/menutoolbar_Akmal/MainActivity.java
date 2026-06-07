package com.example.menutoolbar_Akmal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        tvStatus = findViewById(R.id.tvStatus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; // menu ditampilkan
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            tvStatus.setText(R.string.status_home);
            return true;
        } else if (id == R.id.menu_profile) {
            tvStatus.setText(R.string.status_profile);
            return true;
        } else if (id == R.id.menu_settings) {
            tvStatus.setText(R.string.status_settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
