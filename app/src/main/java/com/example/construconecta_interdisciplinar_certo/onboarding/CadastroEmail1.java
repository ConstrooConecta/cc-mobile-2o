package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityCadastroEmail1Binding;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.example.construconecta_interdisciplinar_certo.utils.AnimationUtils;
import com.example.construconecta_interdisciplinar_certo.utils.ButtonUtils;
import com.example.construconecta_interdisciplinar_certo.utils.InputUtils;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class CadastroEmail1 extends BaseActivity {

    private ActivityCadastroEmail1Binding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroEmail1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // Configurando o botão de avançar como desabilitado inicialmente
        ButtonUtils.disableButton(this, binding.nextButton, binding.progressBar);

        // Verificação de e-mail em tempo real
        binding.emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { validateEmail(s.toString()); }

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

        // Configuração do botão de voltar
        binding.backButton.setOnClickListener(this::backToMain);

        // Configuração do botão de avançar
        binding.nextButton.setOnClickListener(this::checkEmailFirebase);
    }

    // Método de validação de e-mail
    private void validateEmail(String email) {
        if (email.isEmpty()) {
            ButtonUtils.disableButton(this, binding.nextButton, binding.progressBar);
            InputUtils.setNormal(this, binding.emailInputLayout, binding.emailInput, binding.emailErrorText);
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ButtonUtils.enableButton(this, binding.nextButton, binding.progressBar);
            InputUtils.setNormal(this, binding.emailInputLayout, binding.emailInput, binding.emailErrorText);
        } else {
            ButtonUtils.disableButton(this, binding.nextButton, binding.progressBar);
            InputUtils.setError(this, binding.emailInputLayout, binding.emailInput, binding.emailErrorText, "E-mail inválido.");
        }
    }

    // Método para criar um usuário temporário e verificar se o e-mail já existe
    private void checkEmailFirebase(View view) {
        // Desabilitar input e botão "Avançar"
        InputUtils.setNormal(this, binding.emailInputLayout, binding.emailInput, binding.emailErrorText);
        InputUtils.disableInput(binding.emailInput);
        ButtonUtils.disableButtonWithProgressBar(this, binding.nextButton, binding.progressBar);

        String email = Objects.requireNonNull(binding.emailInput.getText()).toString();
        String mockPassword = "SenhaMockada123";

        // Criar o usuário
        auth.createUserWithEmailAndPassword(email, mockPassword)
                .addOnCompleteListener(this, task -> {
                    // Reabilitar o input e botão após a operação
                    InputUtils.enableInput(binding.emailInput);
                    ButtonUtils.enableButton(this, binding.nextButton, binding.progressBar);

                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            user.delete().addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    ButtonUtils.disableButton(this, binding.nextButton, binding.progressBar);
                                    nextPage();
                                } else {
                                    Toast.makeText(CadastroEmail1.this, "Erro. Tente novamente ou contate o suporte.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        handleAuthErrors(task.getException());
                    }
                });
    }

    private void handleAuthErrors(Exception exception) {
        InputUtils.setError(this, binding.emailInputLayout, binding.emailInput, binding.emailErrorText, "E-mail inválido.");
        AnimationUtils.shakeAnimation(binding.emailInputLayout); // Adiciona a animação de erro
        AnimationUtils.shakeAnimation(binding.emailErrorText); // Adiciona a animação de erro
        if (exception instanceof FirebaseAuthUserCollisionException) {
            InputUtils.setError(this, binding.emailInputLayout, binding.emailInput, binding.emailErrorText, "E-mail já cadastrado.");
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            InputUtils.setError(this, binding.emailInputLayout, binding.emailInput, binding.emailErrorText, "E-mail inválido.");
        } else if (exception instanceof FirebaseNetworkException) {
            Toast.makeText(this, "Erro de rede. Tente novamente.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erro ao validar e-mail: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void nextPage() {
        ButtonUtils.disableButton(this, binding.nextButton, binding.progressBar);
        Intent intent = new Intent(CadastroEmail1.this, CadastroTelefone2.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", Objects.requireNonNull(binding.emailInput.getText()).toString());
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // Método para voltar para ActivityMain
    public void backToMain(View view) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
