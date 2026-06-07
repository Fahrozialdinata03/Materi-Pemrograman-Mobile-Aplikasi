package com.example.pretes_Akmal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pretes_Akmal.db.DbHelper;
import com.example.pretes_Akmal.model.Mahasiswa;

public class UpdateActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    private EditText etName, etNim;
    private Button btnSave;
    private Mahasiswa student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        dbHelper = new DbHelper(this);
        etName = findViewById(R.id.edt_name);
        etNim = findViewById(R.id.edt_nim);
        btnSave = findViewById(R.id.btn_submit);

        Intent intent = getIntent();
        student = (Mahasiswa) intent.getSerializableExtra("user");
        etName.setText(student.getName());
        etNim.setText(student.getNim());

        btnSave.setOnClickListener((View v) -> {
            dbHelper.updateUser(student.getId(), etNim.getText().toString(), etName.getText().toString());
            // Menggunakan string resource
            Toast.makeText(UpdateActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
