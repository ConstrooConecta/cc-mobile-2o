package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityCadastroInfosPessoais3Binding;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Objects;

public class CadastroInfosPessoais3 extends BaseActivity {

    private ActivityCadastroInfosPessoais3Binding binding;
    String dataNasc;
    private Integer generoPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializa o View Binding
        binding = ActivityCadastroInfosPessoais3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Adicionando TextWatchers a todos os campos de entrada
        addTextWatchers();

        // Desabilitando botão de avançar
        binding.nextButton.setEnabled(false);
        binding.nextButton.setBackgroundResource(R.drawable.disable_button_design);

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

        // Listener para abrir o dropdown4
        binding.generoInput.setOnClickListener(view -> {
            binding.generoInput.showDropDown();
        });
        // Listener para tratar a seleção de um item
        binding.generoInput.setOnItemClickListener((parent, view, position, id) -> {
            generoPosition = position + 1; // Atualiza a posição selecionada
            updateNextButtonState(); // Verifica o estado do botão após seleção
        });
        // Listener para abrir o DatePicker ao clicar no input de data de nascimento
        binding.dtNascimnetoInput.setOnClickListener(v -> showDatePickerDialog(binding.dtNascimnetoInput));
        // Listener para abrir o DatePicker ao clicar no botão de calendário
        binding.calendarButton.setOnClickListener(v -> showDatePickerDialog(binding.dtNascimnetoInput));
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

        binding.nomeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                // Permitir apenas letras com acentos e espaços
                String filtered = input.replaceAll("[^\\p{L}\\s]", ""); // \p{L} permite letras em qualquer idioma, \s permite espaços

                // Verifica se o texto foi alterado
                if (!input.equals(filtered)) {
                    // Atualiza o campo com o texto filtrado
                    binding.nomeInput.setText(filtered);
                    // Define a posição do cursor no final do texto
                    binding.nomeInput.setSelection(filtered.length());
                }
            }
        });

        binding.sobrenomeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                // Permitir apenas letras com acentos e espaços
                String filtered = input.replaceAll("[^\\p{L}\\s]", ""); // \p{L} permite letras em qualquer idioma, \s permite espaços

                // Verifica se o texto foi alterado
                if (!input.equals(filtered)) {
                    // Atualiza o campo com o texto filtrado
                    binding.sobrenomeInput.setText(filtered);
                    // Define a posição do cursor no final do texto
                    binding.sobrenomeInput.setSelection(filtered.length());
                }
            }
        });
    }

    private void addTextWatchers() {
        TextInputEditText[] inputs = {
                binding.nomeInput,
                binding.sobrenomeInput,
                binding.usernameInput,
                binding.dtNascimnetoInput,
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
                binding.usernameInput.getText().toString().length() >= 6 &&
                !binding.dtNascimnetoInput.getText().toString().trim().isEmpty() &&
                generoPosition != null;

        if (allFieldsFilled) {
            binding.nextButton.setEnabled(true);
            binding.nextButton.setBackgroundResource(R.drawable.primary_button_design);
        } else {
            binding.nextButton.setEnabled(false);
            binding.nextButton.setBackgroundResource(R.drawable.disable_button_design);
        }
    }

    private void showDatePickerDialog(TextInputEditText input) {
        // Obter a data atual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Criar o DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Atualizar o campo de data com a data selecionada
                    input.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
                    // Atualizar a data de nascimento no formato adequado
                    dataNasc = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                },
                year, month, day
        );

        // Exibir o DatePickerDialog
        datePickerDialog.show();
    }

    // Avançar para a próxima página de cadastro
    public void nextPage(View view) {
        // Parse a data de nascimento
        String[] parts = dataNasc.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        Calendar birthDate = Calendar.getInstance();
        birthDate.set(year, month - 1, day); // mês é zero-indexado

        // Obtendo a data atual
        Calendar currentDate = Calendar.getInstance();

        // Verificando se a data de nascimento é futura
        if (birthDate.after(currentDate)) {
            Toast.makeText(this, "Data de nascimento não pode ser no futuro.", Toast.LENGTH_SHORT).show();
            return; // Impede a navegação para a próxima tela
        }

        // Calculando a idade
        int age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (currentDate.get(Calendar.MONTH) < month - 1 ||
                (currentDate.get(Calendar.MONTH) == month - 1 && currentDate.get(Calendar.DAY_OF_MONTH) < day)) {
            age--; // Se ainda não tiver feito aniversário este ano
        }

        // Verificando se o usuário tem mais de 16 anos
        if (age < 16) {
            Toast.makeText(this, "Você deve ter mais de 16 anos.", Toast.LENGTH_SHORT).show();
            return; // Impede a navegação para a próxima tela
        }

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
            bundle.putString("nomeCompleto", Objects.requireNonNull(binding.nomeInput.getText()) + " " + Objects.requireNonNull(binding.sobrenomeInput.getText()));
            bundle.putString("username", String.valueOf(Objects.requireNonNull(binding.usernameInput.getText())));
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
            // Se o campo usernameInput estiver vazio, esconda o usernameStatus
            binding.usernameStatus.setVisibility(View.GONE);
        } else if (username.length() >= 6) {
            binding.usernameStatus.setVisibility(View.VISIBLE);
            binding.usernameStatus.setImageResource(R.drawable.check);
        } else {
            binding.usernameStatus.setVisibility(View.VISIBLE);
            binding.usernameStatus.setImageResource(R.drawable.error_x);
        }
    }
}
