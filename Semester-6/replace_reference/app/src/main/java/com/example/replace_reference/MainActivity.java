package com.example.replace_reference;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText Masukan;
    private TextView Hasil;
    private Button Eksekusi;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "Belajar_SharedPreferences";
    private static final String KEY_NAMA = "nama_pengguna";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Masukan = findViewById(R.id.input);
        Hasil = findViewById(R.id.output);
        Eksekusi = findViewById(R.id.save);

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Tampilkan data tersimpan saat aplikasi dibuka kembali
        String savedName = preferences.getString(KEY_NAMA, getString(R.string.output_data));
        Hasil.setText(getString(R.string.output_format, savedName));

        Eksekusi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = Masukan.getText().toString().trim();

                if (!input.isEmpty()) {
                    editor.putString(KEY_NAMA, input);
                    editor.apply();

                    Hasil.setText(getString(R.string.output_format, input));
                    Toast.makeText(MainActivity.this, "Data disimpan!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Masukan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
