package com.example.construconecta_interdisciplinar_certo.shop.categoria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.adapters.AdapterProdutoHome;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProdutoCategoriaActivity extends BaseActivity {
    private ProgressBar progressBar;
    private RecyclerView produtoRecyclerView;
    private TextView txtCategoria;
    private AdapterProdutoHome adapter;
    private List<Produto> produtos; // Lista de produtos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_categoria);
        progressBar = findViewById(R.id.progressBar2Categorie);

        Intent intent = getIntent();
        // Pega o valor extra que foi passado
        String categoria = intent.getStringExtra("categoria");

        txtCategoria = findViewById(R.id.categoriaTxt);
        txtCategoria.setText(categoria);

        (findViewById(R.id.backButton)).setOnClickListener(v -> finish());

        produtoRecyclerView = findViewById(R.id.recyclerViewCategoria);
        produtoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializando a lista e o Adapter com uma lista vazia
        produtos = new ArrayList<>();
        adapter = new AdapterProdutoHome(produtos);
        produtoRecyclerView.setAdapter(adapter);
        chamarAPIRetrofitCategoria(categoria);
    }

    private void chamarAPIRetrofitCategoria(String categoria) {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);
        Call<List<Produto>> call = produtoApi.findByCategoryName(categoria);

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                Log.d("API Response", "Código de status: " + response.code());
                Log.d("API Response", "Corpo: " + response.body());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("API NoTopo", "Dados recebidos: " + response.body().toString());
                        produtos.clear();
                        produtos.addAll(response.body());
                        adapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                        produtoRecyclerView.setVisibility(View.VISIBLE);
                        txtCategoria.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("API NoTopo", "Corpo da resposta é nulo");
                    }
                } else {
                    Log.d("API NoTopo", "Erro: Código de status " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable throwable) {
                Toast.makeText(ProdutoCategoriaActivity.this, "Erro ao mostrar categorias: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filterList(String query) {
        List<Produto> filteredList = new ArrayList<>();
        for (Produto item : produtos) {
            if (item.getNomeProduto().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Nenhum resultado encontrado", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setFilteredList(filteredList);
        }
    }
}
