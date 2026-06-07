package com.example.pretes_Akmal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import com.example.pretes_Akmal.db.DbHelper;

public class MainActivity extends AppCompatActivity {

    DbHelper dbHelper;
    private EditText etName, etNim;
    private Button btnSave, btnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);
        etName = findViewById(R.id.edt_name);
        etNim = findViewById(R.id.edt_nim);
        btnSave = findViewById(R.id.btn_submit);
        btnList = findViewById(R.id.btn_list);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNim.getText().toString().isEmpty()) {
                    // Menggunakan string resource
                    Toast.makeText(MainActivity.this, R.string.error_nim_empty, Toast.LENGTH_SHORT).show();
                } else if (etName.getText().toString().isEmpty()) {
                    // Menggunakan string resource
                    Toast.makeText(MainActivity.this, R.string.error_name_empty, Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addUserDetail(etNim.getText().toString(), etName.getText().toString());
                    etName.setText("");
                    etNim.setText("");
                    // Menggunakan string resource
                    Toast.makeText(MainActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListMahasiswaActivity.class);
                startActivity(intent);
            }
        });
    }
}
