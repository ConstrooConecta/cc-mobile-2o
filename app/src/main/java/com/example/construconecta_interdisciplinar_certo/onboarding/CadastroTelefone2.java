package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityCadastroTelefone2Binding;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.example.construconecta_interdisciplinar_certo.utils.AnimationUtils;
import com.example.construconecta_interdisciplinar_certo.utils.ButtonUtils;
import com.example.construconecta_interdisciplinar_certo.utils.InputUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroTelefone2 extends BaseActivity {
    // Criando o objeto binding
    private ActivityCadastroTelefone2Binding binding;
    private boolean isUpdating;
    private String formatedPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializando o binding
        binding = ActivityCadastroTelefone2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ButtonUtils.disableButton(this, binding.nextButton, binding.progressBar);

        // Formatação e validação de Telefone em Tempo Real
        binding.telefoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhone(s.toString());
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

                binding.telefoneInput.setText(formatted.toString());
                // Verificação para garantir que a posição não exceda o comprimento do texto
                int selectionPosition = formatted.length();

                if (selectionPosition > binding.telefoneInput.getText().length()) {
                    selectionPosition = binding.telefoneInput.getText().length();
                }
                binding.telefoneInput.setSelection(selectionPosition); // Define a posição da seleção corretamente
                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                formatedPhone = formattingPhone(binding.telefoneInput);
            }
        });
        // Configurando o botão de voltar
        binding.backButton.setOnClickListener(this::backToPreviousScreen);
        // Configurando o botão de avançar
        binding.nextButton.setOnClickListener(v -> validatePhoneDataBase());
    }

    // Voltar para a tela anterior
    public void backToPreviousScreen(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // Animação de saída
    }

    // Avançar para a próxima página de cadastro
    public void nextPage() {
        Intent intent = new Intent(CadastroTelefone2.this, CadastroInfosPessoais3.class);
        // Criando Bundle
        Bundle bundle = new Bundle();

        // Verificando se o e-mail passado pela última tela está nulo
        String email = getIntent().getExtras() != null ? getIntent().getExtras().getString("email") : null;
        if (email != null) {
            bundle.putString("email", email);
            // Adicionando telefone ao bundle
            bundle.putString("telefone", formatedPhone);
            // Passando o Bundle para a próxima tela
            intent.putExtras(bundle);
            // Iniciando outra activity
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Animação de entrada
        } else {
            Toast.makeText(this, "Erro. E-mail não enviado.", Toast.LENGTH_SHORT).show();
        }
    }

    // Método de validação de telefone
    private void validatePhone(String telefone) {
        InputUtils.setNormal(this, binding.telefoneInputLayout, binding.telefoneInput, binding.telefoneErrorText);
        binding.DDDText.setTextColor(Color.parseColor("#B6B6B6"));
        if (telefone.length() == 15) {
            ButtonUtils.enableButton(this, binding.nextButton, binding.progressBar);
        } else {
            ButtonUtils.disableButton(this, binding.nextButton, binding.progressBar);
        }
    }

    // Método para validar a existência do telefone no banco de dados
    private void validatePhoneDataBase() {
        // Desabilitar o botão nextButton e o campo telefoneInput
        InputUtils.setNormal(this, binding.telefoneInputLayout, binding.telefoneInput, binding.telefoneErrorText);
        InputUtils.disableInput(binding.telefoneInput);
        ButtonUtils.disableButtonWithProgressBar(this, binding.nextButton, binding.progressBar);
        binding.DDDText.setTextColor(Color.parseColor("#B6B6B6"));

        String url = "https://cc-api-sql-qa.onrender.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<List<Usuario>> call = usuarioApi.findByTelefone(formatedPhone);

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                // Habilitar o botão nextButton e o campo telefoneInput após a resposta
                InputUtils.enableInput(binding.telefoneInput);
                ButtonUtils.enableButton(CadastroTelefone2.this, binding.nextButton, binding.progressBar);
                binding.telefoneErrorText.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    AnimationUtils.shakeAnimation(binding.telefoneInputLayout);
                    AnimationUtils.shakeAnimation(binding.telefoneErrorText);
                    AnimationUtils.shakeAnimation(binding.DDDText);
                    InputUtils.setError(CadastroTelefone2.this, binding.telefoneInputLayout, binding.telefoneInput, binding.telefoneErrorText, "");
                    binding.DDDText.setTextColor(Color.parseColor("#EC7979"));
                    if (response.body() != null && !response.body().isEmpty()) {
                        InputUtils.setError(CadastroTelefone2.this, binding.telefoneInputLayout, binding.telefoneInput, binding.telefoneErrorText, "Telefone já cadastrado. Faça login ou tente com um novo número.");
                    } else {
                        InputUtils.setError(CadastroTelefone2.this, binding.telefoneInputLayout, binding.telefoneInput, binding.telefoneErrorText, "Erro ao verificar o telefone. Tente novamente ou contate o suporte.");
                    }
                } else {
                    binding.telefoneErrorText.setVisibility(View.GONE);
                    nextPage();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable throwable) {
                // Habilitar o botão nextButton e o campo telefoneInput em caso de falha
                InputUtils.enableInput(binding.telefoneInput);
                ButtonUtils.enableButton(CadastroTelefone2.this, binding.nextButton, binding.progressBar);
                InputUtils.setError(CadastroTelefone2.this, binding.telefoneInputLayout, binding.telefoneInput, binding.telefoneErrorText, "");
                Toast.makeText(CadastroTelefone2.this, "Falha na conexão com o servidor.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Formatando telefone para envio à API
    private String formattingPhone(TextInputEditText telefone) {
        String formatedPhone = telefone.getText() != null ? telefone.getText().toString() : "";
        return formatedPhone.replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "");
    }
}
