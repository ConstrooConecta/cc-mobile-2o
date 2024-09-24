package com.example.construconecta_interdisciplinar_certo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.construconecta_interdisciplinar_certo.loginPath.TelaLogin3;
import com.example.construconecta_interdisciplinar_certo.network.ApiService;
import com.example.construconecta_interdisciplinar_certo.network.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
// Importações necessárias...
public class TelaCadastro1 extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextInputEditText telefone;
    private Retrofit retrofit;
    private ImageView btCalendar;
    private TextInputEditText email, nomeInput, senhaInput,dataNascimentoInput, nomeUsuarioInput,cpf;
    private Spinner Spinnergenero;
    private Integer generoSelecionadoPosicao;
    String dataNascValor;
    String telefoneCerto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro1);
        telefone = findViewById(R.id.telefone);
        btCalendar = findViewById(R.id.btCalendar);
        dataNascimentoInput = findViewById(R.id.dtNascimento);
        email = findViewById(R.id.emailInput);
        nomeInput = findViewById(R.id.nomeInput);
        senhaInput = findViewById(R.id.senhaInput);
        Spinnergenero = findViewById(R.id.spinner_opcoes);
        nomeUsuarioInput = findViewById(R.id.textInputEditTextUserName);
        cpf = findViewById(R.id.textInputEditTextcpf);






        telefone.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não é necessário manipular antes da mudança de texto
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                telefone.setText(formatted.toString());
                // Verificação para garantir que a posição não exceda o comprimento do texto
                int selectionPosition = formatted.length();
                if (selectionPosition > telefone.getText().length()) {
                    selectionPosition = telefone.getText().length();
                }
                telefone.setSelection(selectionPosition); // Define a posição da seleção corretamente
                isUpdating = false;
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Não é necessário manipular após a mudança de texto
                telefoneCerto = ValidarTelefone(telefone);

            }
        });



        // Listener para capturar o valor selecionado no Spinner
        Spinnergenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                generoSelecionadoPosicao = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );
        btCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(TelaCadastro1.this, null, year, month, dayOfMonth);

                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Handle the selected date
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        dataNascimentoInput.setText(selectedDate);
                        dataNascValor = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
//                        txtDataNasc = setError(null);


                    }
                });

                dialog.show();
            }
        });
    }
    public void voltarMain(View view) {
        finish();
    }
    public void AvancarParaLogin(View view  ) {
        salvarUsuario();
        Intent intent = new Intent(TelaCadastro1.this, TelaLogin3.class);
        startActivity(intent);
    }

    private void inserirUsuarioSpring(Usuario usuario){
        String API = "https://cc-api-sql-qa.onrender.com/";
        // Configurar acesso API
        retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // criar a chamada
        ApiService apiConstru = retrofit.create(ApiService.class);
        Call<Usuario> call = apiConstru.inserirUser(usuario);
        // executar a chamada
        call.enqueue(new Callback<Usuario>() {
                         public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                             if(response.isSuccessful()){
                                 Usuario createdUser = response.body();
                                 Log.d("POST_SUCCESS", "Usuário criado: " + createdUser.getNomeUsuario());
                             }else{
                                 Log.e("POST_ERROR", "Código de erro: " + response.code());
                                 Toast.makeText(TelaCadastro1.this, "Erro", Toast.LENGTH_SHORT).show();
                             }
                         }
                         public void onFailure(Call<Usuario> call, Throwable t) {
                            // Falha na requisição (problema de conexão, etc.)
                             Log.e("POST_FAILURE", "Falha na requisição: " + t.getMessage());
                         }
                     }
        );
    }
    private String ValidarTelefone(TextInputEditText telefone) {
        String  telefoneCerto1 = telefone.getText().toString().replace("(", "").replace(")", "");

        // Remove o caractere "-"
        String telefoneCerto2= telefoneCerto1.replace("-", "");
        telefoneCerto= telefoneCerto2.replace(" ", "");

        Toast.makeText(this, "Telefone: "+telefoneCerto, Toast.LENGTH_SHORT).show();
        return telefoneCerto;
    }

    private void salvarUsuario(){
        FirebaseAuth autenticator = FirebaseAuth.getInstance();
        autenticator.createUserWithEmailAndPassword(email.getText().toString(), senhaInput.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //atualizar o profile
                            FirebaseUser userlogin = autenticator.getCurrentUser();
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nomeInput.getText().toString())
                                    //colocando a uri da foto que está no drawable
                                    .setPhotoUri(Uri.parse(String.valueOf("urlImagem")))
                                    .build();
                            userlogin.updateProfile(profile)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){


                                                Usuario usuario = new Usuario(
                                                        userlogin.getUid(),
                                                        cpf.getText().toString(),
                                                        nomeInput.getText().toString(),
                                                        nomeUsuarioInput.getText().toString(),
                                                        email.getText().toString(),
                                                        senhaInput.getText().toString(),
                                                        telefoneCerto,
                                                        generoSelecionadoPosicao,
                                                        dataNascValor);


                                                inserirUsuarioSpring(usuario);

                                                finish();
                                            }else {
                                                Toast.makeText(TelaCadastro1.this, "Deu errado pra inserir..." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        }else {
                            //mostra erro
                            Toast.makeText(TelaCadastro1.this, "Deu red..." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}



