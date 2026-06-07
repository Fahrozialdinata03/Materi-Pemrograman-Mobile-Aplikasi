package com.example.kalkulator_Fahrozi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private String currentInput = "";
    private String previousInput = "";
    private String operator = "";
    private boolean isNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        // Tombol angka
        Button btn0 = findViewById(R.id.btn0);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);

        // Tombol operator
        Button btnAdd = findViewById(R.id.btnTambah);
        Button btnSubtract = findViewById(R.id.btnKurang);
        Button btnMultiply = findViewById(R.id.btnKali);
        Button btnDivide = findViewById(R.id.btnBagi);

        // Tombol lainnya
        Button btnC = findViewById(R.id.btnC);
        Button btnDelete = findViewById(R.id.btnDel);
        Button btnEqual = findViewById(R.id.btnSamaDengan);
        Button btnPercent = findViewById(R.id.btnPersen);
        Button btnRoot = findViewById(R.id.btnAkar);

        // Listener tombol angka
        View.OnClickListener numberClickListener = v -> {
            Button button = (Button) v;
            if (isNewInput) {
                currentInput = button.getText().toString();
                isNewInput = false;
            } else {
                currentInput += button.getText().toString();
            }
            display.setText(currentInput);
        };

        btn0.setOnClickListener(numberClickListener);
        btn1.setOnClickListener(numberClickListener);
        btn2.setOnClickListener(numberClickListener);
        btn3.setOnClickListener(numberClickListener);
        btn4.setOnClickListener(numberClickListener);
        btn5.setOnClickListener(numberClickListener);
        btn6.setOnClickListener(numberClickListener);
        btn7.setOnClickListener(numberClickListener);
        btn8.setOnClickListener(numberClickListener);
        btn9.setOnClickListener(numberClickListener);

        // Listener tombol operator
        View.OnClickListener operatorClickListener = v -> {
            Button button = (Button) v;
            if (!currentInput.isEmpty()) {
                previousInput = currentInput;
                currentInput = "";
                operator = button.getText().toString();
                display.setText(getString(R.string.operator_display, previousInput, operator));
            }
        };

        btnAdd.setOnClickListener(operatorClickListener);
        btnSubtract.setOnClickListener(operatorClickListener);
        btnMultiply.setOnClickListener(operatorClickListener);
        btnDivide.setOnClickListener(operatorClickListener);

        // Tombol reset
        btnC.setOnClickListener(v -> {
            currentInput = "";
            previousInput = "";
            operator = "";
            display.setText(getString(R.string.display_default));
            isNewInput = true;
        });

        // Tombol hapus satu karakter
        btnDelete.setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                display.setText(currentInput);
            }
        });

        // Tombol persen
        btnPercent.setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                double value = Double.parseDouble(currentInput);
                currentInput = String.valueOf(value / 100);
                display.setText(currentInput);
            }
        });

        // Tombol akar
        btnRoot.setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                double value = Double.parseDouble(currentInput);
                currentInput = String.valueOf(Math.sqrt(value));
                display.setText(currentInput);
            }
        });

        // Tombol sama dengan
        btnEqual.setOnClickListener(v -> {
            if (!currentInput.isEmpty() && !previousInput.isEmpty()) {
                double num1 = Double.parseDouble(previousInput);
                double num2 = Double.parseDouble(currentInput);
                double result = 0;
                boolean valid = true;

                switch (operator) {
                    case "+":
                        result = num1 + num2;
                        break;
                    case "-":
                        result = num1 - num2;
                        break;
                    case "x":
                        result = num1 * num2;
                        break;
                    case "÷":
                        if (num2 != 0) {
                            result = num1 / num2;
                        } else {
                            display.setText(getString(R.string.error_divide_by_zero));
                            valid = false;
                        }
                        break;
                }

                if (valid) {
                    String formattedResult = formatResult(result);
                    display.setText(getString(R.string.operation_result_format,
                            previousInput, operator, currentInput, formattedResult));
                    currentInput = formattedResult;
                    previousInput = "";
                    operator = "";
                    isNewInput = true;
                }
            }
        });
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            return String.format("%s", result);
        }
    }
}
