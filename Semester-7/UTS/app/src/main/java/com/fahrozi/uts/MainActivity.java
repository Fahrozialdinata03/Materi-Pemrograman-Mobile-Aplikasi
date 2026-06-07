package com.fahrozi.uts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText inputText;
    Button saveButton;
    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.inputText);
        saveButton = findViewById(R.id.saveButton);
        resultText = findViewById(R.id.resultText);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
        String savedData = sharedPreferences.getString("data", "");

        // Jika sudah ada data tersimpan
        if (!savedData.isEmpty()) {
            resultText.setText(getString(R.string.hasil_data, savedData));
        }

        // Simpan data baru
        saveButton.setOnClickListener(v -> {
            String input = inputText.getText().toString().trim();

            if (!input.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("data", input);
                editor.apply();

                resultText.setText(getString(R.string.hasil_data, input));
            }
        });
    }
}
