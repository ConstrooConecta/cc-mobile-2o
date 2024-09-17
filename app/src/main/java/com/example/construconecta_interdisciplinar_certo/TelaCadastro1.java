package com.example.construconecta_interdisciplinar_certo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
// Importações necessárias...
public class TelaCadastro1 extends AppCompatActivity {
    private TextInputEditText telefone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro1);
        telefone = findViewById(R.id.telefone);
        telefone.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não é necessário manipular antes da mudança de texto
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    return;
                }
                isUpdating = true;
                String unformatted = s.toString().replaceAll("[^\\d]", ""); // Remove tudo que não for número
                if (unformatted.length() > 11) {
                    unformatted = unformatted.substring(0, 11); // Limita a 11 dígitos
                }
                StringBuilder formatted = new StringBuilder();
                int length = unformatted.length();
                if (length > 0) {
                    formatted.append("(");
                    formatted.append(unformatted.substring(0, Math.min(length, 2))); // DDD
                    if (length >= 3) {
                        formatted.append(") ");
                        formatted.append(unformatted.substring(2, Math.min(length, 7))); // Primeira parte do número
                        if (length >= 8) {
                            formatted.append("-");
                            formatted.append(unformatted.substring(7)); // Segunda parte do número
                        }
                    }
                }
                telefone.setText(formatted.toString());
                // Verificação para garantir que a posição não exceda o comprimento do texto
                int selectionPosition = formatted.length();
                if (selectionPosition > telefone.getText().length()) {
                    selectionPosition = telefone.getText().length();
                }
                telefone.setSelection(selectionPosition); // Define a posição da seleção corretamente
                isUpdating = false;
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Não é necessário manipular após a mudança de texto
            }
        });
    }
    public void voltarMain(View view) {
        finish();
    }
}