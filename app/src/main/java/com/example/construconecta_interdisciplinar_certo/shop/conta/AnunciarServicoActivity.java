package com.example.construconecta_interdisciplinar_certo.shop.conta;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.ServicoApi;
import com.example.construconecta_interdisciplinar_certo.apis.TagServicoApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityAnunciarServicoBinding;
import com.example.construconecta_interdisciplinar_certo.models.Servico;
import com.example.construconecta_interdisciplinar_certo.models.TagServico;
import com.example.construconecta_interdisciplinar_certo.utils.ButtonUtils;
import com.example.construconecta_interdisciplinar_certo.utils.InputUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AnunciarServicoActivity extends AppCompatActivity {
    private ActivityAnunciarServicoBinding binding;
    private List<TagServico> categoriasServico = new ArrayList<>();
    private List<String> categoriasList = new ArrayList<>();
    private boolean validacaoCategoria = false, validacaoDescricao = false, validacaoPreco = false;
    private int categoriaPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnunciarServicoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        verificarCampos();

        // Chamada para carregar as categorias da API
        chamarAPIRetrofitTagServico();

        // Mostrar dropdown ao clicar no campo de categoria
        binding.spnServicoCategorieInput.setOnClickListener(v -> binding.spnServicoCategorieInput.showDropDown());

        // Definir ação ao selecionar uma categoria
        binding.spnServicoCategorieInput.setOnItemClickListener((parent, view, position, id) -> {
            validacaoCategoria = true;
            verificarCampos();
            categoriaPosition = position;
        });

        binding.edtDescricaoInput1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDescricao(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.edtPrecoInput1.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePreco(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.botaoANUNCIAR1.setOnClickListener(v -> {
            //pegar o uid do usuario logado
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String uid = auth.getCurrentUser().getUid();
            String categoriaSelecionada = categoriasList.get(categoriaPosition);

            TagServico tagSelecionada = categoriasServico.get(categoriaPosition);

            // Crie uma lista com a tag selecionada (ou adicione outras tags se necessário)
            List<TagServico> tagServicos = new ArrayList<>();
            tagServicos.add(tagSelecionada);

            Servico servico = new Servico(
                    1,
                    categoriaSelecionada,
                    binding.edtDescricaoInput1.getText().toString(),
                    Double.parseDouble(binding.edtPrecoInput1.getText().toString()),
                    uid,
                    tagServicos
            );
            chamarAPIAddServico(servico);


        });
    }

    private void validateDescricao(String descricao) {
        if (descricao.isEmpty()) {
            InputUtils.setNormal(this, binding.edtDescricaoInputLayout1, binding.edtDescricaoInput1, null);
            binding.usernameStatus31.setVisibility(View.VISIBLE);
            binding.usernameStatus31.setImageResource(R.drawable.error_x);
            validacaoDescricao = false;
            verificarCampos();
        } else if (descricao.length() >= 10 && descricao.length() <= 500) {
            InputUtils.setNormal(this, binding.edtDescricaoInputLayout1, binding.edtDescricaoInput1, null);
            binding.usernameStatus31.setVisibility(View.VISIBLE);
            binding.usernameStatus31.setImageResource(R.drawable.check);
            validacaoDescricao = true;
            verificarCampos();
        } else {
            InputUtils.setError(this, binding.edtDescricaoInputLayout1, binding.edtDescricaoInput1, null, null);
            binding.edtDescricaoInput1.setError("Descrição deve ter pelo menos 10 caracteres e no máximo 500.", null);
            binding.usernameStatus31.setVisibility(View.VISIBLE);
            binding.usernameStatus31.setImageResource(R.drawable.error_x);
            validacaoDescricao = false;
            verificarCampos();
        }
    }

    private void validatePreco(String precoText) {
        try {
            double preco = Double.parseDouble(precoText);

            if (preco >= 0.01 && preco <= 1000000.00) {
                validacaoPreco = true;
                binding.edtPrecoInput1.setError(null); // Remove o erro, se houver
            } else {
                validacaoPreco = false;
                binding.edtPrecoInputLayout1.setError("Preço deve ser maior que R$0,01 e menor que R$1.000.000,00");
            }
        } catch (NumberFormatException e) {
            validacaoPreco = false;
            binding.edtPrecoInputLayout1.setError("Insira um preço válido");
        }

        verificarCampos(); // Verifica todos os campos para habilitar ou desabilitar o botão
    }

    private void verificarCampos() {
        // Ativar ou desativar o botão com base nas validações
        if (validacaoPreco && validacaoDescricao && validacaoCategoria) {
            ButtonUtils.enableButton(this, binding.botaoANUNCIAR1, binding.progressBar22);
            Toast.makeText(this, "abuu", Toast.LENGTH_SHORT).show();
        } else {
            assert binding.progressBar22 != null;
            ButtonUtils.disableButton(this, binding.botaoANUNCIAR1, binding.progressBar22);
            binding.botaoANUNCIAR1.setBackgroundColor(Color.LTGRAY); // Muda a cor do botão para indicar que está desativado
        }
    }

    private void chamarAPIRetrofitTagServico() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TagServicoApi tagServicoApi = retrofit.create(TagServicoApi.class);
        Call<List<TagServico>> call = tagServicoApi.findAllTagServices();

        call.enqueue(new Callback<List<TagServico>>() {
            @Override
            public void onResponse(Call<List<TagServico>> call, Response<List<TagServico>> response) {
                Log.d("API Response", "Código de status: " + response.code());
                Log.d("API Response", "Corpo: " + response.body());

                if (response.isSuccessful()) {
                    categoriasServico = response.body();
                    if (categoriasServico != null) {

                        for (TagServico tagServico : categoriasServico) {
                            categoriasList.add(tagServico.getNome());
                        }
                        atualizarCategoriasDropdown();
                    } else {
                        Toast.makeText(AnunciarServicoActivity.this, "A resposta do corpo é nula", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AnunciarServicoActivity.this, "Erro na resposta da API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TagServico>> call, Throwable throwable) {
                Toast.makeText(AnunciarServicoActivity.this, "Erro ao mostrar serviço: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarCategoriasDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item, R.id.dropdownText, categoriasList);
        binding.spnServicoCategorieInput.setAdapter(adapter);
    }

    private void chamarAPIAddServico(Servico servico) {
        String url = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicoApi servicoApi = retrofit.create(ServicoApi.class);
        Call<Servico> call = servicoApi.addService(servico);

        call.enqueue(new Callback<Servico>() {
            @Override
            public void onResponse(Call<Servico> call, Response<Servico> response) {
                if (response.isSuccessful()) {
                    Log.d("POST_SUCCESS", "Servico criado: " + response.body());
                    mostrarModalServicoCriado();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        finish();
                    }, 3000);

                } else {
                    Log.e("POST_FAILURE", "Erro ao criar Servico: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Servico> call, Throwable t) {
                Log.e("POST_FAILURE", "Erro na chamada de API", t);
            }
        });
    }

    private void mostrarModalServicoCriado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AnunciarServicoActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_servico_criado, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        alertDialog.getWindow().setLayout(
                (int) getResources().getDimension(R.dimen.modal_width),
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        new Handler().postDelayed(alertDialog::dismiss, 2000);

    }
}
