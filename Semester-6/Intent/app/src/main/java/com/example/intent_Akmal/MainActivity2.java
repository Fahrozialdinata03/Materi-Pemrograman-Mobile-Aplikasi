package com.example.intent_Akmal;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView txtUsername = findViewById(R.id.txtUsername);
        TextView txtPassword = findViewById(R.id.txtPassword);

        // Ambil data dari Intent
        String username = getIntent().getStringExtra("USERNAME");
        String password = getIntent().getStringExtra("PASSWORD");

        // Set default jika null
        if (username == null) username = "Unknown User";
        if (password == null) password = "No Password";

        // Menggunakan string resource dengan placeholder
        txtUsername.setText(getString(R.string.username_label, username));
        txtPassword.setText(getString(R.string.password_label, password));
    }
}
