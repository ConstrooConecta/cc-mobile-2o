package com.example.construconecta_interdisciplinar_certo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.construconecta_interdisciplinar_certo.apis.CarrinhoApi;
import com.example.construconecta_interdisciplinar_certo.apis.PagamentoPlanoApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityEditarDadosPessoaisBinding;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityTelaPagamentoPlanoBinding;
import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.PagamentoPlano;
import com.example.construconecta_interdisciplinar_certo.shop.DetalhesProdutosActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.net.Authenticator;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TelaPagamentoPlanoActivity extends AppCompatActivity {
    private ActivityTelaPagamentoPlanoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityTelaPagamentoPlanoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //pegar data atual e colocar em uma variavel chamada "dataPagamento"
        LocalDate dataAtual = LocalDate.now();
        String dataPagamentoFormatada = dataAtual.toString();

        binding.btnConfirmPayment1.setOnClickListener(v -> {
            addPaymentPlan(new PagamentoPlano(1,
                    1,
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                    12.90,
                    "PIX",
                    dataPagamentoFormatada)
            );
        });






    }

    private void addPaymentPlan(PagamentoPlano pagamentoPlano) {
        String url = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PagamentoPlanoApi api = retrofit.create(PagamentoPlanoApi.class);
        Call<PagamentoPlano> call = api.createPaymentPlan(pagamentoPlano);

        call.enqueue(new Callback<PagamentoPlano>() {
            @Override
            public void onResponse(Call<PagamentoPlano> call, Response<PagamentoPlano> response) {
                if (response.isSuccessful()) {
                    PagamentoPlano createdPlan = response.body();
                    Log.d("POST_SUCCESS", "Carrinho criado: " + createdPlan.getPagamentoPlanoId());

                    Toast.makeText(TelaPagamentoPlanoActivity.this, "Plano comprado com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Tratar erro ao adicionar no banco
                    Log.d("REQUEST_BODY", pagamentoPlano.toString()); // Certifique-se de que o método toString() do seu objeto Carrinho exiba os dados corretamente
                    Log.e("POST_ERROR", "Erro ao salvar carrinho. Código: " + response.code());
                    Toast.makeText(TelaPagamentoPlanoActivity.this, "Erro ao salvar Plano no banco de dados. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagamentoPlano> call, Throwable t) {
                Toast.makeText(TelaPagamentoPlanoActivity.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}