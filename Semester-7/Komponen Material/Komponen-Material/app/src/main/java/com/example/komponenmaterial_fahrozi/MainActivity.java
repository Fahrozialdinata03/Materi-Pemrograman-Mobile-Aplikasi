package com.example.komponenmaterial_fahrozi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etNama;
    MaterialButton btnSubmit;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNama = findViewById(R.id.etNama);
        btnSubmit = findViewById(R.id.btnSubmit);
        fab = findViewById(R.id.fab);

        // Tombol Submit
        btnSubmit.setOnClickListener(v -> {
            String nama = etNama.getText().toString();
            if (nama.isEmpty()) {
                Snackbar.make(v, "Nama belum diisi!", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(v, "Halo, " + nama + "!", Snackbar.LENGTH_LONG).show();
            }
        });

        // Floating Action Button
        fab.setOnClickListener(v ->
                Snackbar.make(v, "FAB ditekan!", Snackbar.LENGTH_SHORT).show()
        );
    }
}
