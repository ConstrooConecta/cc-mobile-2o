package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityCadastroInfosPessoais3Binding;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.example.construconecta_interdisciplinar_certo.utils.AnimationUtils;
import com.example.construconecta_interdisciplinar_certo.utils.ButtonUtils;
import com.example.construconecta_interdisciplinar_certo.utils.InputUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroInfosPessoais3 extends BaseActivity {

    private ActivityCadastroInfosPessoais3Binding binding;
    String dataNasc;
    private Integer generoPosition;
    private DatePickerDialog datePickerDialog; // Variável para armazenar o DatePickerDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializa o View Binding
        binding = ActivityCadastroInfosPessoais3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Adicionando TextWatchers a todos os campos de entrada
        addTextWatchers();

        // Desabilitando botão de avançar
        ButtonUtils.disableButton(this, binding.nextButton, binding.progressBar);

        // Configuração do campo de gênero
        String[] generos = new String[]{"Feminino", "Masculino", "Outro", "Prefiro não dizer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item, // Certifique-se de criar este layout se ainda não existir
                R.id.dropdownText,       // Referência ao TextView dentro do item
                generos
        );
        // Atribuição do adapter ao campo de gênero usando binding
        binding.generoInput.setAdapter(adapter);

        // Listener para abrir o dropdown
        binding.generoInput.setOnClickListener(v -> binding.generoInput.showDropDown()); // Mostra o dropdown

        // Listener para tratar a seleção de um item
        binding.generoInput.setOnItemClickListener((parent, view, position, id) -> {
            generoPosition = position + 1; // Atualiza a posição selecionada
            updateNextButtonState(); // Verifica o estado do botão após seleção
        });

        // Listener para abrir o DatePicker ao clicar no input de data de nascimento
        binding.dtNascimentoInput.setOnClickListener(v -> showDatePickerDialog(binding.dtNascimentoInput));

        // Listener para abrir o DatePicker ao clicar no botão de calendário
        binding.calendarButton.setOnClickListener(v -> showDatePickerDialog(binding.dtNascimentoInput));
        binding.usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
                    binding.usernameInput.setText(filtered);
                    // Define a posição do cursor no final do texto
                    binding.usernameInput.setSelection(filtered.length());
                }
            }
        });
        // Nome
        binding.nomeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                // Permitir apenas letras com acentos e espaços
                String filtered = input.replaceAll("[^\\p{L}\\s]", ""); // \p{L} permite letras em qualquer idioma, \s permite espaços

                // Capitalizar a primeira letra de cada palavra
                String capitalized = capitalizeWords(filtered);

                // Verifica se o texto foi alterado
                if (!input.equals(capitalized)) {
                    // Atualiza o campo com o texto filtrado e capitalizado
                    binding.nomeInput.setText(capitalized);
                    // Define a posição do cursor no final do texto
                    binding.nomeInput.setSelection(capitalized.length());
                }
            }
        });
        // Sobrenome
        binding.sobrenomeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                // Permitir apenas letras com acentos e espaços
                String filtered = input.replaceAll("[^\\p{L}\\s]", ""); // \p{L} permite letras em qualquer idioma, \s permite espaços

                // Capitalizar a primeira letra de cada palavra
                String capitalized = capitalizeWords(filtered);

                // Verifica se o texto foi alterado
                if (!input.equals(capitalized)) {
                    // Atualiza o campo com o texto filtrado e capitalizado
                    binding.sobrenomeInput.setText(capitalized);
                    // Define a posição do cursor no final do texto
                    binding.sobrenomeInput.setSelection(capitalized.length());
                }
            }
        });
        // Configurando o botão de avançar
        binding.nextButton.setOnClickListener(v -> validateUsernameDataBase());
    }

    private void addTextWatchers() {
        TextInputEditText[] inputs = {
                binding.nomeInput,
                binding.sobrenomeInput,
                binding.usernameInput,
                binding.dtNascimentoInput,
        };

        for (TextInputEditText input : inputs) {
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateNextButtonState(); // Verifica o estado do botão quando o texto é alterado
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void updateNextButtonState() {
        boolean allFieldsFilled = !binding.nomeInput.getText().toString().trim().isEmpty() &&
                binding.nomeInput.getText().toString().trim().length() >= 3 &&
                binding.sobrenomeInput.getText().toString().length() >= 3 &&
                !binding.sobrenomeInput.getText().toString().trim().isEmpty() &&
                !binding.usernameInput.getText().toString().trim().isEmpty() &&
                binding.usernameInput.getText().toString().length() >= 5 &&
                !binding.dtNascimentoInput.getText().toString().trim().isEmpty() &&
                generoPosition != null;

        if (allFieldsFilled) {
            ButtonUtils.enableButton(this, binding.nextButton, binding.progressBar);
        } else {
            ButtonUtils.disableButton(this, binding.nextButton, binding.progressBar);
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
    }    private String capitalizeFirstLetters(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    public void nextPage() {
        Intent intent = new Intent(CadastroInfosPessoais3.this, CadastroInfosSeguranca4.class);
        // Criando Bundle
        Bundle bundle = new Bundle();

        // Verificando se o e-mail e telefone passado pela última tela está nulo
        String email = getIntent().getExtras() != null ? getIntent().getExtras().getString("email") : null;
        String telefone = getIntent().getExtras() != null ? getIntent().getExtras().getString("telefone") : null;
        if (email != null && telefone != null) {
            bundle.putString("email", email);
            bundle.putString("telefone", telefone);
            // Adicionando as informações pessoais ao bundle
            String nomeCompleto = capitalizeFirstLetters(Objects.requireNonNull(binding.nomeInput.getText()).toString().trim() + " " +
                    Objects.requireNonNull(binding.sobrenomeInput.getText()).toString().trim());
            bundle.putString("nomeCompleto", nomeCompleto);
            bundle.putString("username", Objects.requireNonNull(binding.usernameInput.getText()).toString());
            bundle.putString("dtNascimento", dataNasc);
            bundle.putInt("genero", generoPosition);
            // Passando o Bundle para a próxima tela
            intent.putExtras(bundle);
            // Iniciando outra activity
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Animação de entrada
        } else {
            Toast.makeText(this, "Erro. E-mail e telefone não enviados.", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToPreviousScreen(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // Animação de saída
    }

    // Método de validação de e-mail
    private void validateUsername(String username) {
        if (username.isEmpty()) {
            InputUtils.setNormal(this, binding.usernameInputLayout, binding.usernameInput, null);
            binding.usernameStatus.setVisibility(View.GONE);
            binding.indiceNomeUsuario.setTextColor(Color.parseColor("#B6B6B6"));
        } else if (username.length() >= 5) {
            InputUtils.setNormal(this, binding.usernameInputLayout, binding.usernameInput, null);
            binding.usernameStatus.setVisibility(View.VISIBLE);
            binding.usernameStatus.setImageResource(R.drawable.check);
            binding.indiceNomeUsuario.setTextColor(Color.parseColor("#B6B6B6"));
        } else {
            InputUtils.setError(this, binding.usernameInputLayout, binding.usernameInput, null, null);
            binding.usernameInput.setError("Username deve ter pelo menos 5 caracteres. Pode utilizar-se dos seguintes caracteres: . e _.", null);
            binding.usernameStatus.setVisibility(View.VISIBLE);
            binding.usernameStatus.setImageResource(R.drawable.error_x);
            binding.indiceNomeUsuario.setTextColor(Color.parseColor("#EC7979"));
        }
    }

    private void validateUsernameDataBase() {
        InputUtils.disableInput(binding.nomeInput);
        InputUtils.disableInput(binding.sobrenomeInput);
        InputUtils.disableInput(binding.usernameInput);
        InputUtils.disableInput(binding.dtNascimentoInput);
        InputUtils.disableInput(binding.generoInput);

        binding.usernameStatus.setImageResource(R.drawable.check);
        binding.indiceNomeUsuario.setTextColor(Color.parseColor("#B6B6B6"));
        InputUtils.setNormal(this, binding.usernameInputLayout, binding.usernameInput, null);

        ButtonUtils.enableButtonWithProgressBar(this, binding.nextButton, binding.progressBar);

        String url = "https://cc-api-sql-qa.onrender.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<List<Usuario>> call = usuarioApi.findByUsername(Objects.requireNonNull(binding.usernameInput.getText()).toString());

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                ButtonUtils.enableButton(CadastroInfosPessoais3.this, binding.nextButton, binding.progressBar);
                InputUtils.enableInput(binding.nomeInput);
                InputUtils.enableInput(binding.sobrenomeInput);
                InputUtils.enableInput(binding.usernameInput);
                InputUtils.enableInput(binding.dtNascimentoInput);
                InputUtils.enableInput(binding.generoInput);

                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        InputUtils.setError(CadastroInfosPessoais3.this, binding.usernameInputLayout, binding.usernameInput, null, null);
                        binding.usernameInput.setError("Já existe alguém com este nome de usuário.", null);
                        binding.usernameStatus.setImageResource(R.drawable.error_x);
                        binding.indiceNomeUsuario.setTextColor(Color.parseColor("#EC7979"));
                        AnimationUtils.shakeAnimation(binding.usernameInputLayout);
                        AnimationUtils.shakeAnimation(binding.indiceNomeUsuario);
                        AnimationUtils.shakeAnimation(binding.usernameStatus);
                    } else {
                        InputUtils.setError(CadastroInfosPessoais3.this, binding.usernameInputLayout, binding.usernameInput, null, null);
                        binding.usernameInput.setError("Erro ao verificar o nome de usuário. Tente novamente ou contate o suporte.", null);
                    }
                } else {
                    // Username está disponívela
                    nextPage();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable throwable) {
                // Tratamento para falhas de conexão ou problemas de comunicação com o servidor
                InputUtils.setError(CadastroInfosPessoais3.this, binding.usernameInputLayout, binding.usernameInput, null, "Erro ao verificar o nome de usuário. Tente novamente ou contate o suporte.");
                Toast.makeText(CadastroInfosPessoais3.this, "Falha na conexão com o servidor.", Toast.LENGTH_SHORT).show();
                ButtonUtils.enableButtonWithProgressBar(CadastroInfosPessoais3.this, binding.nextButton, binding.progressBar);
            }
        });

    }

    private String capitalizeWords(String input) {
        String[] words = input.split("\\s+");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                // Capitalizar a primeira letra e manter o restante como está
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}
