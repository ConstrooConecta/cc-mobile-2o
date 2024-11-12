package com.example.construconecta_interdisciplinar_certo.checkout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.DescontoApi;
import com.example.construconecta_interdisciplinar_certo.apis.EnderecoApi;
import com.example.construconecta_interdisciplinar_certo.models.Desconto;
import com.example.construconecta_interdisciplinar_certo.models.Endereco;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MetodosPagamento extends AppCompatActivity {

    private TextView valor2, txtStatusCupom;
    private RadioButton radioButton2;
    private Button botaoContinuar, buttonAplicar;
    private ImageButton btnBack;
    private TextInputEditText cupomInput;
    private EnderecoApi enderecoApi;
    private DescontoApi descontoApi;
    private Double total;
    private Double totalComDesconto = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_pagamento);

        valor2 = findViewById(R.id.valor2);
        radioButton2 = findViewById(R.id.radioButton2);
        botaoContinuar = findViewById(R.id.botaoContinuar);
        buttonAplicar = findViewById(R.id.button2);
        txtStatusCupom = findViewById(R.id.txtStatusCupom);
        cupomInput = findViewById(R.id.cupomInput);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        total = getIntent().getExtras() != null ? getIntent().getExtras().getDouble("total") : null;

        if (total != null) {
            valor2.setText("R$ " + String.format("%.2f", total));
        }

        radioButton2.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#867151")));

        botaoContinuar.setEnabled(false);
        botaoContinuar.setBackgroundResource(R.drawable.disable_button_design);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        enderecoApi = retrofit.create(EnderecoApi.class);

        Retrofit retrofitNoSql = new Retrofit.Builder()
                .baseUrl("https://cc-api-nosql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        descontoApi = retrofitNoSql.create(DescontoApi.class);

        String uid = FirebaseAuth.getInstance().getUid();
        radioButton2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            botaoContinuar.setEnabled(isChecked);
            botaoContinuar.setBackgroundResource(R.drawable.primary_button_design);
            botaoContinuar.setOnClickListener(v -> buscarEnderecos(uid));
        });

        buttonAplicar.setOnClickListener(v -> verificarCupom(cupomInput.getText().toString()));
    }

    private void buscarEnderecos(String usuarioId) {
        botaoContinuar.setEnabled(false);
        botaoContinuar.setBackgroundResource(R.drawable.disable_button_design);

        Call<List<Endereco>> call = enderecoApi.getEnderecosByUsuario(usuarioId);
        call.enqueue(new Callback<List<Endereco>>() {
            @Override
            public void onResponse(Call<List<Endereco>> call, Response<List<Endereco>> response) {
                botaoContinuar.setEnabled(true);
                botaoContinuar.setBackgroundResource(R.drawable.primary_button_design);

                Bundle bundle = new Bundle();
                // Se o desconto não foi aplicado, passa o valor original de 'total'.
                double valorTotalFinal = (totalComDesconto != 0.0) ? totalComDesconto : total;
                bundle.putDouble("total", valorTotalFinal); // Passa o valor correto, com ou sem desconto
                bundle.putString("cupom", cupomInput.getText().toString());

                if (response.isSuccessful() && response.body() != null) {
                    List<Endereco> enderecos = response.body();
                    if (!enderecos.isEmpty()) {
                        Intent intent = new Intent(MetodosPagamento.this, EscolhaEnderecoActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(MetodosPagamento.this, CadastroEnderecoActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<Endereco>> call, Throwable t) {
                botaoContinuar.setEnabled(true);
                botaoContinuar.setBackgroundResource(R.drawable.primary_button_design);

                Log.e("MetodosPagamento.java", "Falha ao buscar endereços para a próxima tela", t);
                Toast.makeText(MetodosPagamento.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void verificarCupom(String cupom) {
        if (cupom.isEmpty()) {
            txtStatusCupom.setText("Insira um cupom.");
            txtStatusCupom.setVisibility(View.VISIBLE);
            return;
        }

        Call<List<Desconto>> call = descontoApi.findByVoucherName(cupom);
        call.enqueue(new Callback<List<Desconto>>() {
            @Override
            public void onResponse(Call<List<Desconto>> call, Response<List<Desconto>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Se o cupom for válido
                    Desconto desconto = response.body().get(0);
                    double valorDesconto = desconto.getValorDesconto();
                    double descontoAplicado = (valorDesconto / 100) * total;
                    totalComDesconto = total - descontoAplicado; // Armazena o total com desconto

                    valor2.setText("R$ " + String.format("%.2f", totalComDesconto));

                    txtStatusCupom.setText("Cupom aplicado com sucesso! (" + String.format("%.2f", valorDesconto) + "%)");
                    txtStatusCupom.setTextColor(Color.GREEN);
                    txtStatusCupom.setVisibility(View.VISIBLE);
                } else {
                    // Se o cupom for inválido
                    totalComDesconto = 0.0;  // Reinicia o total com desconto
                    valor2.setText("R$ " + String.format("%.2f", total));  // Mostra o preço original

                    txtStatusCupom.setText("Cupom não encontrado.");
                    txtStatusCupom.setTextColor(Color.RED);
                    txtStatusCupom.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Desconto>> call, Throwable t) {
                // Em caso de erro na requisição
                totalComDesconto = 0.0;  // Reinicia o total com desconto
                valor2.setText("R$ " + String.format("%.2f", total));  // Mostra o preço original

                txtStatusCupom.setText("Erro ao verificar o cupom.");
                txtStatusCupom.setTextColor(Color.RED);
                txtStatusCupom.setVisibility(View.VISIBLE);
                Log.e("MetodosPagamento.java", "Erro ao verificar o cupom", t);
            }
        });
    }
}
