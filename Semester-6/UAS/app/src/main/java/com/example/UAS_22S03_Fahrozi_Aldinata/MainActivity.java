package com.example.UAS_22S03_Fahrozi_Aldinata;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editUsername, editPassword;
    Button btnSimpan;
    TextView txtUsername, txtPassword;

    public static final String SHARED_PREFS = "uas";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnSimpan = findViewById(R.id.btnSimpan);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");

        txtUsername.setText(getString(R.string.username_anda) + " " + savedUsername);
        txtPassword.setText(getString(R.string.password_anda) + " " + savedPassword);
        TextView txtInfo = findViewById(R.id.txtInfo);
        btnSimpan.setOnClickListener(v -> {
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, password);
            editor.apply();

            txtUsername.setText(getString(R.string.username_anda) + " " + username);
            txtPassword.setText(getString(R.string.password_anda) + " " + password);

            txtInfo.setText("Data telah disimpan");
        });
    }
}
