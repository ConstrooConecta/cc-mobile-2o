package com.example.construconecta_interdisciplinar_certo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityEditarDadosSegurancaBinding;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.InputMismatchException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarDadosSeguranca extends AppCompatActivity {
    private ActivityEditarDadosSegurancaBinding binding;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditarDadosSegurancaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        buscarUsuarioPorUid(FirebaseAuth.getInstance().getCurrentUser().getUid());


        binding.btnClose.setOnClickListener(v -> finish());
        // Configurando o listener para o botão Salvar
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Cria um novo objeto de Usuário com as novas informações
                    Usuario user = new Usuario(
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            Objects.requireNonNull(binding.etCpf.getText()).toString(),
                            usuario.getNomeCompleto(),
                            usuario.getNomeUsuario(),
                            Objects.requireNonNull(binding.etEmail.getText()).toString(),
                            Objects.requireNonNull(binding.etNewPassword.getText()).toString(),
                            usuario.getTelefone(),
                            usuario.getGenero(),
                            usuario.getDataNascimento()
                    );


                    // Chame o método para salvar as alterações
                    updateUserInFirebaseAndApi(FirebaseAuth.getInstance().getCurrentUser().getUid(), user);
                }
            }
        });
    }

    // Método para validar os campos
    private boolean validateFields() {
        String email = binding.etEmail.getText().toString().trim();
        String cpf = binding.etCpf.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        String currentPassword = binding.etCurrentPassword.getText().toString().trim();

        // Verificação do campo de email
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Por favor, insira o email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Email inválido");
            return false;
        }

        // Verificação do campo de CPF
        if (TextUtils.isEmpty(cpf)) {
            binding.etCpf.setError("Por favor, insira o CPF");
            return false;
        }
        if (!isValidCPF(cpf)) {
            binding.etCpf.setError("CPF inválido");
            return false;
        }

        // Verificação dos campos de senha
        if (TextUtils.isEmpty(newPassword)) {
            binding.etNewPassword.setError("Por favor, insira a nova senha");
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.etConfirmPassword.setError("Por favor, confirme a nova senha");
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("As senhas não coincidem");
            return false;
        }
        if (!currentPassword.equals(usuario.getSenha())){
            binding.etCurrentPassword.setError("Senha incorreta");
            Toast.makeText(this, "certo:"+ usuario.getSenha(), Toast.LENGTH_SHORT).show();
            return false;
        }


        // Se todas as verificações passarem, retorna verdadeiro
        return true;
    }

    // Método para validar o CPF
    private boolean isValidCPF(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verifica se o CPF tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Validação do CPF com base nos dígitos verificadores
        try {
            int digito1 = 0, digito2 = 0;
            for (int i = 0; i < 9; i++) {
                digito1 += (10 - i) * Character.getNumericValue(cpf.charAt(i));
                digito2 += (11 - i) * Character.getNumericValue(cpf.charAt(i));
            }
            digito1 = 11 - (digito1 % 11);
            if (digito1 >= 10) digito1 = 0;

            digito2 += 2 * digito1;
            digito2 = 11 - (digito2 % 11);
            if (digito2 >= 10) digito2 = 0;

            return cpf.endsWith(digito1 + "" + digito2);
        } catch (InputMismatchException | NumberFormatException e) {
            return false;
        }
    }





    private void updateUser(String uid, Usuario user) {
        String url = "https://cc-api-sql-qa.onrender.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<Usuario> call = usuarioApi.updateUser(uid, user);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "Usuário atualizado com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("TAG", "onResponse: Código de erro: " + response.code());
                    Log.d("TAG", "onResponse: Código de erro: " + response.errorBody().toString());
                    Toast.makeText(getApplicationContext(), "Erro ao atualizar o usuário", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Usuario atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUserInFirebaseAndApi(String uid, Usuario user) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // Obtenha a credencial atual do usuário para reautenticação
            AuthCredential credential = EmailAuthProvider.getCredential(
                    firebaseUser.getEmail(),
                    Objects.requireNonNull(binding.etCurrentPassword.getText()).toString()
            );

            // Reautentica o usuário
            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Atualiza a senha no Firebase Authentication
                            firebaseUser.updatePassword(user.getSenha())
                                    .addOnCompleteListener(passwordUpdateTask -> {
                                        if (passwordUpdateTask.isSuccessful()) {
                                            // Senha atualizada com sucesso no Firebase, agora atualize na API
                                            updateUser(uid, user);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Erro ao atualizar a senha no Firebase", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Falha na reautenticação. Verifique a senha atual.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void buscarUsuarioPorUid(String uid) {
        String apiUrl = "https://cc-api-sql-qa.onrender.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<Usuario> call = usuarioApi.findByUid(uid);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuario = response.body();

                    binding.etEmail.setText(usuario.getEmail());
                    binding.etCpf.setText(usuario.getCpf());


                } else {
                    Toast.makeText(EditarDadosSeguranca.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(EditarDadosSeguranca.this, "Erro na chamada de API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
