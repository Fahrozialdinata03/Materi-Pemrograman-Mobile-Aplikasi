package com.example.pretes_Akmal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import androidx.activity.EdgeToEdge; // Import ini diperlukan
import androidx.core.graphics.Insets; // Import ini diperlukan
import androidx.core.view.ViewCompat; // Import ini diperlukan
import androidx.core.view.WindowInsetsCompat; // Import ini diperlukan

import com.example.pretes_Akmal.adapter.MahasiswaAdapter;
import com.example.pretes_Akmal.db.DbHelper;
import com.example.pretes_Akmal.model.Mahasiswa;

import java.util.ArrayList;

public class ListMahasiswaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MahasiswaAdapter adapter;
    private ArrayList<Mahasiswa> studentsArrayList;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Mengaktifkan mode edge-to-edge
        setContentView(R.layout.activity_list_mahasiswa);

        // Menangani insets (padding untuk system bars seperti status bar dan navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.rview); // Casting tidak lagi diperlukan di Android modern
        adapter = new MahasiswaAdapter(this);
        dbHelper = new DbHelper(this);

        studentsArrayList = dbHelper.getAllUsers();
        adapter.setListStudents(studentsArrayList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListMahasiswaActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        studentsArrayList = dbHelper.getAllUsers();
        adapter.setListStudents(studentsArrayList);
        adapter.notifyDataSetChanged();
    }
}
