package com.example.construconecta_interdisciplinar_certo.checkout;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterCarrinho;
import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterProdutoHome;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.CarrinhoApi;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.shop.DetalhesProdutosActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

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
    private TextView quantidadeItens, subto,textViewTotal,textCarrinhoVazio;
    private ImageView imagem, imageViewCarrinhoVazio;
    private Button button,buttonCarrinhoVazio;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        // Inicializa os componentes da interface
        progressBar = findViewById(R.id.progressBar);
        subto = findViewById(R.id.textView51);
        button = findViewById(R.id.button);
        textViewTotal = findViewById(R.id.textView52SubtotalV);
        imagem = findViewById(R.id.imageView7);
        recyclerView = findViewById(R.id.recyclerViewCarrinho);
        quantidadeItens = findViewById(R.id.quantidadeItens);
        imageViewCarrinhoVazio = findViewById(R.id.imagemCarrinhoVazio);

        buttonCarrinhoVazio=findViewById(R.id.buttonCarrinhoVazio);
        textCarrinhoVazio = findViewById(R.id.textView7);

        // Configura o RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicialmente esconde todos os elementos exceto a ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        subto.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        textViewTotal.setVisibility(View.INVISIBLE);
        imagem.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE); // Mantém o RecyclerView invisível até os dados serem carregados
        imageViewCarrinhoVazio.setVisibility(View.GONE);
        textCarrinhoVazio.setVisibility(View.GONE);
        buttonCarrinhoVazio.setVisibility(View.GONE);

        // Chama a função para buscar os produtos primeiro
        chamar_API_Retrofit_Produtos();
        button.setOnClickListener(v -> {
            Intent intent = new Intent(CarrinhoActivity.this, MetodosPagamento.class);
            intent.putExtra("total", TotalExibivel);
            startActivity(intent);
        });
    }

    private void chamar_API_Retrofit_Produtos() {
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
                        // Após carregar os produtos, chama a função para carregar o carrinho
                        chamar_API_Retrofit_Carrinho(userId);
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

    private void chamar_API_Retrofit_Carrinho(String userId) {
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

                        // Configura o Adapter e notifica as mudanças
                        adapter = new AdapterCarrinho(carrinhos, produtos);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        // Calcula o total exibível
                        calcularTotalExibivel(carrinhosResponse);
                        //deixar exbindo apenas 2 casas decimais
                        textViewTotal.setText("Total: R$ " +String.format("%.2f", TotalExibivel));
                        textViewTotal.setTypeface(null, Typeface.BOLD);
                        //passando o textViewTotal num bundle pra proxima tela:


                        quantidadeItens.setText(adapter.getItemCount() + " itens");

                        // Agora que os dados foram carregados, podemos exibir os elementos
                        progressBar.setVisibility(View.GONE);
                        subto.setVisibility(View.VISIBLE);
                        button.setVisibility(View.VISIBLE);
                        textViewTotal.setVisibility(View.VISIBLE);
                        imagem.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE); // Exibe o RecyclerView agora que os dados estão carregados
                    } else {
                        Toast.makeText(CarrinhoActivity.this, "Carrinho vazio", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
                        textCarrinhoVazio.setVisibility(View.VISIBLE);
                        buttonCarrinhoVazio.setVisibility(View.VISIBLE);
                    }
                } else {
                    quantidadeItens.setText(0 + " itens");
                    progressBar.setVisibility(View.GONE);
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

    // Método para calcular o total exibível
    private void calcularTotalExibivel(List<Carrinho> carrinhosResponse) {
        TotalExibivel = 0.0; // Zera o total antes de calcular
        for (Carrinho item : carrinhosResponse) {
            TotalExibivel += item.getValorTotal(); // Some o preço de cada item
        }
    }



    private void finish(View view) {
        //encerrar a activity atual
        finish();
    }
}