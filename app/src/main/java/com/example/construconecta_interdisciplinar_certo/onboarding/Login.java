package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.example.construconecta_interdisciplinar_certo.Home;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityLoginBinding;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class Login extends BaseActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Verifica se os campos estão vazios inicialmente
        updateLoginButtonState();

        // Configuração do botão de voltar
        binding.backButton.setOnClickListener(this::backToMain);

        // Verificação de e-mail em tempo real
        binding.emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                // Proibir espaços
                String filtered = input.replace(" ", "");
                if (!input.equals(filtered)) {
                    binding.emailInput.setText(filtered);
                    binding.emailInput.setSelection(filtered.length());
                }
            }
        });

        binding.senhaInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btLogar.setOnClickListener(this::signIn);
    }

    public void signIn(View view) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btLogar.setText("");
        binding.btLogar.setEnabled(false);

        String email = Objects.requireNonNull(binding.emailInput.getText()).toString();
        String senha = Objects.requireNonNull(binding.senhaInput.getText()).toString();

        if (!email.isEmpty() && !senha.isEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(task -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btLogar.setText("Entrar");
                        binding.btLogar.setEnabled(true);

                        if (task.isSuccessful()) {
                            startActivity(new Intent(Login.this, Home.class));
                            finish();
                        } else {
                            showError();
                        }
                    });
        }
    }

    private void showError() {
        binding.emailInput.setBackgroundResource(R.drawable.error_input_design);
        binding.senhaInput.setBackgroundResource(R.drawable.error_input_design);
        binding.emailInput.setError("Email ou senha inválidos");
        binding.senhaInput.setError("Email ou senha inválidos");
    }

    // Método de validação de e-mail
    private boolean validateEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Método para atualizar o estado do botão de login
    private void updateLoginButtonState() {
        String email = Objects.requireNonNull(binding.emailInput.getText()).toString();
        String senha = Objects.requireNonNull(binding.senhaInput.getText()).toString();

        // Verifica se o email é válido e se a senha não está vazia
        boolean isEmailValid = validateEmail(email);
        boolean isSenhaNotEmpty = !senha.isEmpty();

        // Se ambos forem verdadeiros, o botão será ativado
        boolean isEnabled = isEmailValid && isSenhaNotEmpty;
        binding.btLogar.setEnabled(isEnabled);
        binding.btLogar.setBackgroundResource(isEnabled ? R.drawable.primary_button_design : R.drawable.disable_button_design);
    }

    public void backToMain(View view) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
