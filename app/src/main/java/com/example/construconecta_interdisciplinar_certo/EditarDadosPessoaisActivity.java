package com.example.construconecta_interdisciplinar_certo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityEditarDadosPessoaisBinding;
import com.example.construconecta_interdisciplinar_certo.fragments.ContaFragment;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.utils.InputUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarDadosPessoaisActivity extends AppCompatActivity {
    private ActivityEditarDadosPessoaisBinding binding;
    private Integer generoPosition;
    private Usuario usuario;
    String dataNasc;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditarDadosPessoaisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar os campos de entrada usando o binding
        buscarUsuarioPorUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Configuração do campo de gênero
        String[] generos = new String[]{"Feminino", "Masculino", "Outro", "Prefiro não dizer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item,
                R.id.dropdownText,
                generos
        );
        binding.spnGeneroInput.setAdapter(adapter);
        binding.imageButton4.setOnClickListener(v -> finish());

        // Listener para abrir o dropdown
        binding.spnGeneroInput.setOnClickListener(v -> binding.spnGeneroInput.showDropDown());
        binding.spnGeneroInput.setOnItemClickListener((parent, view, position, id) -> {
            generoPosition = position + 1;
        });

        binding.edtApelidoInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateUsername(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                // Permitir letras (sem acentos), pontos finais e underlines, e remover caracteres não permitidos
                String filtered = input.replaceAll("[^a-zA-Z0-9._]", "");

                // Verifica se o texto foi alterado
                if (!input.equals(filtered)) {
                    // Atualiza o campo com o texto filtrado
                    binding.edtApelidoInput.setText(filtered);
                    // Define a posição do cursor no final do texto
                    binding.edtApelidoInput.setSelection(filtered.length());
                }
            }
        });


        binding.edtDataNascimentoInput.setOnClickListener(v -> showDatePickerDialog(binding.edtDataNascimentoInput));
        binding.calendarButton1.setOnClickListener(v -> showDatePickerDialog(binding.edtDataNascimentoInput));

        binding.btnAtualizar.setOnClickListener(v -> {
            // Verifique se os campos de entrada estão preenchidos corretamente
            String nome = binding.edtNomeInput.getText().toString().trim();
            String apelido = binding.edtApelidoInput.getText().toString().trim();
            String telefone = binding.edtTelefoneInput.getText().toString().trim();
            String dataNascimento = binding.edtDataNascimentoInput.getText().toString().trim();

            if (nome.isEmpty() || apelido.isEmpty() || telefone.isEmpty() || dataNascimento.isEmpty() || generoPosition == null) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifique se o nome de usuário contém espaços
            if (apelido.contains(" ")) {
                Toast.makeText(this, "O nome de usuário não pode conter espaços", Toast.LENGTH_SHORT).show();
                return;
            }

            // Criar um novo usuário com os dados atualizados
            Usuario user = new Usuario(
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    Objects.requireNonNull(usuario.getCpf()),
                    nome,
                    apelido,
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    Objects.requireNonNull(usuario.getSenha()),
                    telefone,
                    generoPosition,
                    dataNascimento
            );
            updateUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), user);
        });

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

                    // Para obter uma mensagem de erro detalhada, tente ler o corpo do erro
                    try {
                        if (response.errorBody() != null) {
                            String errorMessage = response.errorBody().string();
                            Log.d("TAG", "onResponse: Mensagem de erro: " + errorMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
                    Log.d("TAG", "onResponse: " + usuario.getNomeCompleto());

                    binding.edtNomeInput.setText(usuario.getNomeCompleto());
                    binding.edtApelidoInput.setText(usuario.getNomeUsuario());
                    binding.edtDataNascimentoInput.setText(usuario.getDataNascimento());
                    binding.edtTelefoneInput.setText(usuario.getTelefone());

                    Integer generoUsuario = usuario.getGenero();
                    if (generoUsuario != null) {
                        switch (generoUsuario) {
                            case 1:
                                binding.spnGeneroInput.setText("Feminino", false);
                                generoPosition = 1;
                                break;
                            case 2:
                                binding.spnGeneroInput.setText("Masculino", false);
                                generoPosition = 2;
                                break;
                            case 3:
                                binding.spnGeneroInput.setText("Outro", false);
                                generoPosition = 3;
                                break;
                            case 4:
                                binding.spnGeneroInput.setText("Prefiro não dizer", false);
                                generoPosition = 4;
                                break;
                        }
                    }
                } else {
                    Toast.makeText(EditarDadosPessoaisActivity.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(EditarDadosPessoaisActivity.this, "Erro na chamada de API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void validateUsername(String username) {
        if (username.isEmpty()) {
            InputUtils.setNormal(this, binding.edtApelidoInputLayout, binding.edtApelidoInput, null);
            binding.usernameStatus1.setVisibility(View.GONE);
            binding.indiceNomeUsuario.setTextColor(Color.parseColor("#B6B6B6"));
        } else if (username.length() >= 5) {
            InputUtils.setNormal(this, binding.edtApelidoInputLayout, binding.edtApelidoInput, null);
            binding.usernameStatus1.setVisibility(View.VISIBLE);
            binding.usernameStatus1.setImageResource(R.drawable.check);
            binding.indiceNomeUsuario.setTextColor(Color.parseColor("#B6B6B6"));
        } else {
            InputUtils.setError(this, binding.edtApelidoInputLayout, binding.edtApelidoInput, null, null);
            binding.edtApelidoInput.setError("Username deve ter pelo menos 5 caracteres. Pode utilizar-se dos seguintes caracteres: . e _.", null);
            binding.usernameStatus1.setVisibility(View.VISIBLE);
            binding.usernameStatus1.setImageResource(R.drawable.error_x);
            binding.indiceNomeUsuario.setTextColor(Color.parseColor("#EC7979"));
        }
    }
    private void showDatePickerDialog(TextInputEditText input) {
        // Verifica se o DatePickerDialog já está aberto
        if (datePickerDialog != null) {
            return; // Se já estiver aberto, não faz nada
        }

        // Obter a data atual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.set(Calendar.YEAR, year - 16);

        // Criar o DatePickerDialog
        datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Atualizar o campo de data com a data selecionada
                    input.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
                    // Atualizar a data de nascimento no formato adequado
                    dataNasc = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    datePickerDialog = null; // Resetar a variável após fechar o diálogo
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());

        // Exibir o DatePickerDialog
        datePickerDialog.show();

        // Adicionar um listener para detectar quando o diálogo é fechado
        datePickerDialog.setOnDismissListener(dialog -> {
            datePickerDialog = null; // Resetar a variável ao fechar o diálogo
        });
    }

}
