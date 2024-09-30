package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityCadastroEmail1Binding;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import java.util.Objects;

public class CadastroEmail1 extends BaseActivity {

    private ActivityCadastroEmail1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializando o binding
        binding = ActivityCadastroEmail1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurando o botão de avançar como desabilitado inicialmente
        binding.nextButton.setEnabled(false);
        binding.nextButton.setBackgroundResource(R.drawable.disable_button_design);

        // Verificação de e-mail em tempo real
        binding.emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                // Proibir espaços
                String filtered = input.replaceAll(" ", "");

                // Verifica se o texto foi alterado
                if (!input.equals(filtered)) {
                    // Atualiza o campo com o texto filtrado
                    binding.emailInput.setText(filtered);
                    binding.emailInput.setSelection(filtered.length());
                }
            }
        });

        // Configuração do botão de voltar
        binding.backButton.setOnClickListener(this::backToMain);

        // Configuração do botão de avançar
        binding.nextButton.setOnClickListener(this::nextPage);
    }

    // Método de validação de e-mail
    private void validateEmail(String email) {
        if (email.isEmpty()) {
            binding.emailErrorText.setVisibility(View.GONE); // Oculta o texto de erro se o campo estiver vazio
            binding.nextButton.setEnabled(false);
            binding.nextButton.setBackgroundResource(R.drawable.disable_button_design);
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailErrorText.setVisibility(View.GONE);
            binding.nextButton.setEnabled(true);
            binding.nextButton.setBackgroundResource(R.drawable.primary_button_design);
        } else {
            binding.emailErrorText.setVisibility(View.VISIBLE);
            binding.nextButton.setEnabled(false);
            binding.nextButton.setBackgroundResource(R.drawable.disable_button_design);
        }
    }

    // Método para voltar para ActivityMain
    public void backToMain(View view) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // Animação de saída
        finish();
    }

    // Método para avançar para a próxima página de cadastro
    public void nextPage(View view) {
        Intent intent = new Intent(CadastroEmail1.this, CadastroTelefone2.class);
        // Criando Bundle e passando o e-mail para a próxima página
        Bundle bundle = new Bundle();
        bundle.putString("email", Objects.requireNonNull(binding.emailInput.getText()).toString());
        intent.putExtras(bundle);
        // Iniciando outra activity
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Animação de entrada
    }
}
