package com.example.construconecta_interdisciplinar_certo.onboarding;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.construconecta_interdisciplinar_certo.shop.Home;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityCadastroInfosSeguranca4Binding;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.example.construconecta_interdisciplinar_certo.utils.AnimationUtils;
import com.example.construconecta_interdisciplinar_certo.utils.ButtonUtils;
import com.example.construconecta_interdisciplinar_certo.utils.InputUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroInfosSeguranca4 extends BaseActivity {
    private ActivityCadastroInfosSeguranca4Binding binding;
    private boolean isMinStatusOk = false;
    private boolean isNumberStatusOk = false;
    private boolean isUpperLetterStatusOk = false;
    private boolean isSmallLetterStatusOk = false;
    private boolean isSpecialCharStatusOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializa o View Binding
        binding = ActivityCadastroInfosSeguranca4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.scrollView.post(() -> binding.scrollView.fullScroll(View.FOCUS_DOWN));

        // Adiciona TextWatcher para o campo de senha
        binding.senhaInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não é necessário implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Não é necessário implementar
            }
        });

        // Adiciona TextWatcher para o campo de CPF
        binding.cpfInput.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não é necessário implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.scrollView.post(() -> binding.scrollView.fullScroll(View.FOCUS_DOWN));
                InputUtils.setNormal(CadastroInfosSeguranca4.this, binding.cpfInputLayout, binding.cpfInput, binding.cpfError);

                if (isUpdating) {
                    return; // Evita loops infinitos
                }

                isUpdating = true; // Indica que estamos atualizando o texto
                String unformatted = s.toString().replaceAll("[^\\d]", ""); // Remove tudo que não for número

                if (unformatted.length() > 11) {
                    unformatted = unformatted.substring(0, 11); // Limita a 11 dígitos
                }

                StringBuilder formatted = new StringBuilder();
                int length = unformatted.length();

                if (length > 0) {
                    formatted.append(unformatted.substring(0, Math.min(length, 3))); // Primeiros 3 dígitos
                    if (length > 3) {
                        formatted.append(".");
                        formatted.append(unformatted.substring(3, Math.min(length, 6))); // Próximos 3 dígitos
                        if (length > 6) {
                            formatted.append(".");
                            formatted.append(unformatted.substring(6, Math.min(length, 9))); // Próximos 3 dígitos
                            if (length > 9) {
                                formatted.append("-");
                                formatted.append(unformatted.substring(9, Math.min(length, 11))); // Últimos 2 dígitos
                            }
                        }
                    }
                }

                binding.cpfInput.setText(formatted.toString());
                binding.cpfInput.setSelection(formatted.length()); // Define a posição da seleção corretamente
                binding.cpfInput.requestFocus(); // Manter o foco no campo de CPF
                validateCpf(unformatted); // Valida o CPF formatado
                isUpdating = false; // Reseta a flag
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Não é necessário implementar
            }
        });

        binding.nextButton.setOnClickListener(v -> validateCpfDataBase());

        updateButtonStatus();
    }

    private void validatePassword(String senha) {
        isMinStatusOk = senha.length() >= 8;
        isNumberStatusOk = senha.matches(".*\\d.*");
        isUpperLetterStatusOk = senha.matches(".*[A-Z].*");
        isSmallLetterStatusOk = senha.matches(".*[a-z].*");
        isSpecialCharStatusOk = senha.matches(".*[^a-zA-Z0-9].*");

        // Atualiza os ícones de status
        binding.minStatus.setImageResource(isMinStatusOk ? R.drawable.check : R.drawable.error_x);
        binding.oneNumber.setImageResource(isNumberStatusOk ? R.drawable.check : R.drawable.error_x);
        binding.oneUpperLetter.setImageResource(isUpperLetterStatusOk ? R.drawable.check : R.drawable.error_x);
        binding.oneSmallLetter.setImageResource(isSmallLetterStatusOk ? R.drawable.check : R.drawable.error_x);
        binding.oneSpecialCaract.setImageResource(isSpecialCharStatusOk ? R.drawable.check : R.drawable.error_x);
        updateButtonStatus();
    }

    private void validateCpf(String cpf) {
        if (cpf.isEmpty()) {
            InputUtils.setNormal(this, binding.cpfInputLayout, binding.cpfInput, binding.cpfError);
        } else if (isCPFValido(cpf)) {
            InputUtils.setNormal(this, binding.cpfInputLayout, binding.cpfInput, binding.cpfError);
        } else {
            binding.cpfError.setText("", null);
            InputUtils.setError(this, binding.cpfInputLayout, binding.cpfInput, binding.cpfError, "CPF Inválido.");
        }

        updateButtonStatus();
    }

    private void validateCpfDataBase() {
        ButtonUtils.enableButtonWithProgressBar(this, binding.nextButton, binding.progressBar);
        InputUtils.setNormal(this, binding.cpfInputLayout, binding.cpfInput, binding.cpfError);
        InputUtils.disableInput(binding.cpfInput);
        InputUtils.disableInput(binding.senhaInput);

        String url = "https://cc-api-sql-qa.onrender.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<List<Usuario>> call = usuarioApi.findByCpf(binding.cpfInput.getText().toString().replaceAll("[^\\d]", ""));
        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                ButtonUtils.enableButton(CadastroInfosSeguranca4.this, binding.nextButton, binding.progressBar);
                InputUtils.enableInput(binding.cpfInput);
                InputUtils.enableInput(binding.senhaInput);

                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        InputUtils.setError(CadastroInfosSeguranca4.this, binding.cpfInputLayout, binding.cpfInput, binding.cpfError, "Já existe alguém com este CPF cadastrado.");
                        AnimationUtils.shakeAnimation(binding.cpfInput);
                        AnimationUtils.shakeAnimation(binding.cpfError);

                        // Role o ScrollView para o final ao exibir o erro
                        binding.scrollView.post(() -> binding.scrollView.fullScroll(View.FOCUS_DOWN));

                    } else {
                        binding.cpfError.setText("Erro ao verificar o nome de usuário. Tente novamente ou contate o suporte.", null);
                    }
                } else {
                    nextPage();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable throwable) {
                // Trate a falha de rede aqui, se necessário
            }
        });
    }

    private boolean isCPFValido(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;

        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;

        // Validação do dígito verificador
        try {
            int[] digitos = new int[11];
            for (int i = 0; i < 11; i++) {
                digitos[i] = Integer.parseInt(cpf.substring(i, i + 1));
            }

            int soma1 = 0;
            int soma2 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += digitos[i] * (10 - i);
                soma2 += digitos[i] * (11 - i);
            }

            int dv1 = (soma1 * 10) % 11;
            int dv2 = ((soma2 + (dv1 * 2)) * 10) % 11;

            if (dv1 == 10) dv1 = 0;
            if (dv2 == 10) dv2 = 0;

            return (dv1 == digitos[9] && dv2 == digitos[10]);
        } catch (Exception e) {
            return false;
        }
    }

    public void nextPage() {
        Intent intent = new Intent(CadastroInfosSeguranca4.this, Home.class);
        Bundle bundle = new Bundle();

        String email = getIntent().getExtras() != null ? getIntent().getExtras().getString("email") : null;
        String telefone = getIntent().getExtras() != null ? getIntent().getExtras().getString("telefone") : null;
        String nomeCompleto = getIntent().getExtras() != null ? getIntent().getExtras().getString("nomeCompleto") : null;
        String username = getIntent().getExtras() != null ? getIntent().getExtras().getString("username") : null;
        String dtNascimento = getIntent().getExtras() != null ? getIntent().getExtras().getString("dtNascimento") : null;
        Integer genero = getIntent().getExtras() != null ? getIntent().getExtras().getInt("genero") : null;

        if (email != null && telefone != null && nomeCompleto != null && username != null && dtNascimento != null && genero != null) {
            String cpfFormated = Objects.requireNonNull(binding.cpfInput.getText()).toString().replaceAll("[^\\d]", "");

            FirebaseAuth autenticador = FirebaseAuth.getInstance();
            autenticador.createUserWithEmailAndPassword(email, Objects.requireNonNull(binding.senhaInput.getText()).toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = autenticador.getCurrentUser();
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nomeCompleto)
                                    .setPhotoUri(Uri.parse("Imagem"))
                                    .build();
                            user.updateProfile(profile).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Usuario usuario = new Usuario(
                                            user.getUid(),
                                            cpfFormated,
                                            nomeCompleto,
                                            username,
                                            email,
                                            binding.senhaInput.getText().toString(),
                                            telefone,
                                            genero,
                                            dtNascimento
                                    );

                                    // Envia os dados para o banco e só navega ao receber sucesso
                                    sendUserToDatabase(usuario, intent, bundle);
                                } else {
                                    Toast.makeText(CadastroInfosSeguranca4.this, "Erro ao criar conta.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        }
    }

    // Voltar para a tela anterior
    public void backToPreviousScreen(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // Animação de saída
    }

    private void sendUserToDatabase(Usuario usuario, Intent intent, Bundle bundle) {
        ButtonUtils.enableButtonWithProgressBar(this, binding.nextButton, binding.progressBar);

        String url = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi api = retrofit.create(UsuarioApi.class);
        Call<Usuario> call = api.createUser(usuario);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()) {
                    Usuario createdUser = response.body();
                    Log.d("POST_SUCCESS", "Usuário criado: " + createdUser.getNomeUsuario());

                    // Após sucesso no banco de dados, adiciona no Firebase
                    addToFirebase(usuario, intent, bundle);
                } else {
                    // Tratar erro ao adicionar no banco
                    ButtonUtils.enableButton(CadastroInfosSeguranca4.this, binding.nextButton, binding.progressBar);
                    binding.nextButton.setText("Criar Conta");
                    Toast.makeText(CadastroInfosSeguranca4.this, "Erro ao salvar no banco de dados.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE); // Oculta ProgressBar
                binding.nextButton.setEnabled(true); // Reabilita o botão
                binding.nextButton.setText("Avançar"); // Mostra o texto novamente
                Toast.makeText(CadastroInfosSeguranca4.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToFirebase(Usuario usuario, Intent intent, Bundle bundle) {
        FirebaseAuth autenticador = FirebaseAuth.getInstance();
        FirebaseUser user = autenticador.getCurrentUser();

        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setDisplayName(usuario.getNomeCompleto())
                .setPhotoUri(Uri.parse("Imagem"))
                .build();

        user.updateProfile(profile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sucesso no Firebase, agora pode ocultar o ProgressBar e ir para a próxima página
                binding.progressBar.setVisibility(View.GONE); // Oculta ProgressBar
                binding.nextButton.setEnabled(true); // Reabilita o botão, caso precise ser reutilizado

                // Prossiga para a próxima tela
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                // Erro no Firebase
                binding.progressBar.setVisibility(View.GONE); // Oculta ProgressBar
                binding.nextButton.setEnabled(true); // Reabilita o botão
                binding.nextButton.setText("Avançar");
                Toast.makeText(CadastroInfosSeguranca4.this, "Erro ao atualizar perfil no Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtonStatus() {
        String cpf = Objects.requireNonNull(binding.cpfInput.getText()).toString().replaceAll("[^\\d]", "");
        boolean cpfValido = isCPFValido(cpf);

        // Verifica o status das senhas com as variáveis booleanas
        boolean todosStatusSenhaOk = isMinStatusOk && isNumberStatusOk && isUpperLetterStatusOk && isSmallLetterStatusOk && isSpecialCharStatusOk;

        // Atualiza o estado do botão
        if (cpfValido && todosStatusSenhaOk) {
            binding.nextButton.setEnabled(true);
            binding.nextButton.setBackgroundResource(R.drawable.primary_button_design);
        } else {
            binding.nextButton.setEnabled(false);
            binding.nextButton.setBackgroundResource(R.drawable.disable_button_design);
        }
    }
}
