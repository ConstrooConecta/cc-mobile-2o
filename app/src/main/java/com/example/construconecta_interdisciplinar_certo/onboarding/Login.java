package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityLoginBinding;
import com.example.construconecta_interdisciplinar_certo.shop.Home;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.example.construconecta_interdisciplinar_certo.utils.AnimationUtils;
import com.example.construconecta_interdisciplinar_certo.utils.ButtonUtils;
import com.example.construconecta_interdisciplinar_certo.utils.InputUtils;
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.btLogar.setOnClickListener(this::signIn);
    }

    public void signIn(View view) {
        ButtonUtils.disableButtonWithProgressBar(this, binding.btLogar, binding.progressBar);
        InputUtils.setNormal(this, binding.emailInputLayout, binding.emailInput, null);
        InputUtils.setNormal(this, binding.senhaInputLayout, binding.senhaInput, null);

        String email = Objects.requireNonNull(binding.emailInput.getText()).toString();
        String senha = Objects.requireNonNull(binding.senhaInput.getText()).toString();

        if (!email.isEmpty() && !senha.isEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(task -> {
                        ButtonUtils.enableButton(this, binding.btLogar, binding.progressBar);

                        if (task.isSuccessful()) {
                            startActivity(new Intent(Login.this, Home.class));
                            finish();
                        } else {
                            InputUtils.setError(this, binding.emailInputLayout, binding.emailInput, null, null);
                            InputUtils.setError(this, binding.senhaInputLayout, binding.senhaInput, null, null);
                            AnimationUtils.shakeAnimation(binding.senhaInputLayout);
                            AnimationUtils.shakeAnimation(binding.emailInputLayout);
                            binding.emailInput.setError("Email ou senha inválidos");
                            binding.senhaInput.setError("Email ou senha inválidos", null);
                        }
                    });
        }
    }

    // Método para atualizar o estado do botão de login
    private void updateLoginButtonState() {
        String email = Objects.requireNonNull(binding.emailInput.getText()).toString();
        String senha = Objects.requireNonNull(binding.senhaInput.getText()).toString();

        if (!senha.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ButtonUtils.enableButton(this, binding.btLogar, binding.progressBar);
        } else {
            ButtonUtils.disableButton(this, binding.btLogar, binding.progressBar);
        }
    }

    public void backToMain(View view) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
