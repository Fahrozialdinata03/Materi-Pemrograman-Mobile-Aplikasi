package com.example.list_view_Akmal;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list);
        String[] tutorials = {
                "Algoritma", "Struktur Data", "Basis Data",
                "Pemrograman Berbasis Mobile 2", "Web 2", "Metodologi Penelitian",
                "Pemrograman Berbasis Mobile 1", "Web 1", "Bahasa Pemrograman 1"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tutorials
        );

        listView.setAdapter(adapter);
    }
}
