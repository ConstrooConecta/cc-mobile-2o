package com.example.construconecta_interdisciplinar_certo.checkout;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterCarrinho;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.CarrinhoApi;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarrinhoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterCarrinho adapter;
    private List<Carrinho> carrinhos = new ArrayList<>();
    private List<Produto> produtos = new ArrayList<>();
    private ProgressBar progressBar;
    private double TotalExibivel = 0.0; // Adiciona a variável para o total
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private String userId = currentUser.getUid();
    private TextView quantidadeItens, subto, textViewTotal, textCarrinhoVazio,textView37;
    private ImageView imagem, imageViewCarrinhoVazio;
    private Button button, buttonCarrinhoVazio;
    private ImageButton imageButton3;
    private final Handler handler = new Handler();
    private final int INTERVALO_ATUALIZACAO = 5000; // Intervalo de 5 segundos

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);
        //pegar uid do usuario logado do firebase
        imageButton3 = findViewById(R.id.imageButton3);
        textView37 = findViewById(R.id.textView37);



        // Inicializa os componentes da interface
        progressBar = findViewById(R.id.progressBar);
        subto = findViewById(R.id.textView51);
        button = findViewById(R.id.button);
        textViewTotal = findViewById(R.id.textView52SubtotalV);
        imagem = findViewById(R.id.imageView7);
        recyclerView = findViewById(R.id.recyclerViewCarrinho);
        quantidadeItens = findViewById(R.id.quantidadeItens);
        imageViewCarrinhoVazio = findViewById(R.id.imagemCarrinhoVazio);
        buttonCarrinhoVazio = findViewById(R.id.buttonCarrinhoVazio);
        textCarrinhoVazio = findViewById(R.id.textView7);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar.setVisibility(View.VISIBLE);
        subto.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        textViewTotal.setVisibility(View.INVISIBLE);
        imagem.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        imageViewCarrinhoVazio.setVisibility(View.GONE);
        textCarrinhoVazio.setVisibility(View.GONE);
        buttonCarrinhoVazio.setVisibility(View.GONE);

        chamarAPIRetrofitProdutos();
        button.setOnClickListener(v -> {
            Intent intent = new Intent(CarrinhoActivity.this, MetodosPagamento.class);
            intent.putExtra("total", TotalExibivel);
            startActivity(intent);
        });

        // Inicia o processo de verificação a cada 5 segundos
        iniciarVerificacaoPeriodica();
    }

    private void iniciarVerificacaoPeriodica() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                chamarAPIRetrofitProdutos(); // Atualiza produtos e carrinho
                handler.postDelayed(this, INTERVALO_ATUALIZACAO); // Repete a cada 5 segundos
            }
        }, INTERVALO_ATUALIZACAO);
    }

    private void chamarAPIRetrofitProdutos() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);
        Call<List<Produto>> call = produtoApi.findUser();

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                if (response.isSuccessful()) {
                    produtos = response.body();
                    if (produtos != null) {
                        chamarAPIRetrofitCarrinho(userId);
                    }
                } else {
                    Toast.makeText(CarrinhoActivity.this, "Erro ao carregar produtos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CarrinhoActivity.this, "Erro na chamada de produtos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chamarAPIRetrofitCarrinho(String userId) {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarrinhoApi carrinhoApi = retrofit.create(CarrinhoApi.class);
        Call<List<Carrinho>> call = carrinhoApi.findByUserId(userId);

        call.enqueue(new Callback<List<Carrinho>>() {
            @Override
            public void onResponse(Call<List<Carrinho>> call, Response<List<Carrinho>> response) {
                if (response.isSuccessful()) {
                    List<Carrinho> carrinhosResponse = response.body();
                    if (carrinhosResponse != null && !carrinhosResponse.isEmpty()) {
                        carrinhos.clear();
                        carrinhos.addAll(carrinhosResponse);


                        adapter = new AdapterCarrinho(carrinhos, produtos);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        calcularTotalExibivel(carrinhosResponse);
                        textViewTotal.setText("Total: R$ " + String.format("%.2f", TotalExibivel));
                        textViewTotal.setTypeface(null, Typeface.BOLD);
                        quantidadeItens.setText(adapter.getItemCount() + " itens");

                        progressBar.setVisibility(View.GONE);
                        subto.setVisibility(View.VISIBLE);
                        button.setVisibility(View.VISIBLE);
                        textViewTotal.setVisibility(View.VISIBLE);
                        imagem.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        quantidadeItens.setText(0 + " itens");
                        imagem.setVisibility(View.GONE);
                        subto.setVisibility(View.GONE);
                        textViewTotal.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        textView37.setVisibility(View.GONE);
                        imageButton3.setVisibility(View.GONE);
                        imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
                        textCarrinhoVazio.setVisibility(View.VISIBLE);
                        buttonCarrinhoVazio.setVisibility(View.VISIBLE);
                    }
                } else {
                    quantidadeItens.setText(0 + " itens");
                    imagem.setVisibility(View.GONE);
                    subto.setVisibility(View.GONE);
                    textViewTotal.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    textView37.setVisibility(View.GONE);
                    imageButton3.setVisibility(View.GONE);
                    imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
                    textCarrinhoVazio.setVisibility(View.VISIBLE);
                    buttonCarrinhoVazio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Carrinho>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CarrinhoActivity.this, "Erro ao carregar carrinho: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcularTotalExibivel(List<Carrinho> carrinhosResponse) {
        TotalExibivel = 0.0;
        for (Carrinho item : carrinhosResponse) {
            TotalExibivel += item.getValorTotal();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Para a verificação quando a Activity é destruída
    }

    public void DeletarCarrinho(View view) {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarrinhoApi carrinhoApi = retrofit.create(CarrinhoApi.class);
        Call<ResponseBody> call = carrinhoApi.deleteAll_carrinho(userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CarrinhoActivity.this, "Carrinho deletado com sucesso!", Toast.LENGTH_SHORT).show();
                    quantidadeItens.setText(0 + " itens");
                    textView37.setVisibility(View.GONE);
                    imageButton3.setVisibility(View.GONE);
                    imagem.setVisibility(View.GONE);
                    subto.setVisibility(View.GONE);
                    textViewTotal.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
                    textCarrinhoVazio.setVisibility(View.VISIBLE);
                    buttonCarrinhoVazio.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                } else {
                    Toast.makeText(CarrinhoActivity.this, "Erro ao deletar carrinho: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CarrinhoActivity.this, "Erro ao deletar carrinho: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}