package com.example.eksternal__Akmal;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "SampleFile.txt";
    private static final String FILE_PATH = "MyFileStorage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputText = findViewById(R.id.myInputText);
        TextView response = findViewById(R.id.response);
        Button saveButton = findViewById(R.id.saveExternalStorage);
        Button readButton = findViewById(R.id.getExternalStorage);

        File externalFile = new File(getExternalFilesDir(FILE_PATH), FILE_NAME);

        saveButton.setOnClickListener(v -> {
            try (FileOutputStream fos = new FileOutputStream(externalFile)) {
                fos.write(inputText.getText().toString().getBytes());
                inputText.setText("");
                response.setText(getString(R.string.save_success));
            } catch (IOException e) {
                response.setText(getString(R.string.save_failed));
                e.printStackTrace();
            }
        });

        readButton.setOnClickListener(v -> {
            StringBuilder dataBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(externalFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    dataBuilder.append(line).append("\n");
                }
                String result = dataBuilder.toString().trim();
                inputText.setText(result);
                response.setText(getString(R.string.read_success) + "\n\n" + result);
            } catch (IOException e) {
                response.setText(getString(R.string.read_failed));
                e.printStackTrace();
            }
        });

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            saveButton.setEnabled(false);
            response.setText(getString(R.string.storage_not_available));
        }
    }

    private boolean isExternalStorageReadOnly() {
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    private boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
