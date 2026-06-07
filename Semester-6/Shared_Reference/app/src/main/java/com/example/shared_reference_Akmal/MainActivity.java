package com.example.shared_reference_Akmal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shared_reference_Akmal.R;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_NAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Dapatkan referensi view
        EditText editName = findViewById(R.id.editName);
        Button btnSave = findViewById(R.id.btnSave);
        TextView textOutput = findViewById(R.id.textOutput);

        SharedPreferences sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedName = sharedPref.getString(KEY_NAME, getString(R.string.no_name));

        // Gunakan string dengan placeholder
        textOutput.setText(getString(R.string.hello_name, savedName));

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString();
            sharedPref.edit().putString(KEY_NAME, name).apply();
            textOutput.setText(getString(R.string.hello_name, name));
        });
    }
}
