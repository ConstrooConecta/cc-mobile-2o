package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;

public class ValidacaoCodigo extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validacao_codigo);

        EditText etDigit1 = findViewById(R.id.et_digit1);
        EditText etDigit2 = findViewById(R.id.et_digit2);
        EditText etDigit3 = findViewById(R.id.et_digit3);
        EditText etDigit4 = findViewById(R.id.et_digit4);
        EditText etDigit5 = findViewById(R.id.et_digit5);
        EditText etDigit6 = findViewById(R.id.et_digit6);

        // Configura os listeners para mover o foco
        setupEditText(etDigit1, etDigit2);
        setupEditText(etDigit2, etDigit3);
        setupEditText(etDigit3, etDigit4);
        setupEditText(etDigit4, etDigit5);
        setupEditText(etDigit5, etDigit6);
    }


    private void setupEditText(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}