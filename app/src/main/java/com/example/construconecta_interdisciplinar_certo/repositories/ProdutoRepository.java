package com.example.construconecta_interdisciplinar_certo.repositories;

import android.util.Log;

import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.models.Produto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProdutoRepository {
    private static ProdutoRepository instance;
    private List<Produto> produtos;
    private List<Produto> produtosOferta;
    private List<Produto> produtosNoTopo;
    private boolean isDataLoaded = false;

    private ProdutoRepository() {
        produtos = new ArrayList<>();
        produtosOferta = new ArrayList<>();
        produtosNoTopo = new ArrayList<>();
    }

    public static ProdutoRepository getInstance() {
        if (instance == null) {
            instance = new ProdutoRepository();
        }
        return instance;
    }

    public void loadData(ProdutoDataCallback callback) {
        if (isDataLoaded) {
            callback.onDataLoaded(produtos, produtosOferta, produtosNoTopo);
            return;
        }

        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);

        // Carregar produtos relevantes
        Call<List<Produto>> callRelevantes = produtoApi.findByTopic(3);
        callRelevantes.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    produtos.addAll(response.body());
                    Log.d("ProdutoRepository", "Produtos relevantes carregados");
                } else {
                    Log.e("ProdutoRepository", "Erro ao carregar produtos relevantes");
                }
                // Chamar para produtos de ofertas
                carregarProdutosOfertas(produtoApi, callback);
            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                Log.e("ProdutoRepository", "Falha ao chamar a API para produtos relevantes: " + t.getMessage());
            }
        });
    }

    private void carregarProdutosOfertas(ProdutoApi produtoApi, ProdutoDataCallback callback) {
        Call<List<Produto>> callOfertas = produtoApi.findByTopic(2);
        callOfertas.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    produtosOferta.addAll(response.body());
                    Log.d("ProdutoRepository", "Produtos de ofertas carregados");
                } else {
                    Log.e("ProdutoRepository", "Erro ao carregar produtos de ofertas");
                }
                // Chamar para produtos no topo
                carregarProdutosNoTopo(produtoApi, callback);
            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                Log.e("ProdutoRepository", "Falha ao chamar a API para produtos de ofertas: " + t.getMessage());
            }
        });
    }

    private void carregarProdutosNoTopo(ProdutoApi produtoApi, ProdutoDataCallback callback) {
        Call<List<Produto>> callNoTopo = produtoApi.findByTopic(1);
        callNoTopo.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    produtosNoTopo.addAll(response.body());
                    Log.d("ProdutoRepository", "Produtos no topo carregados");
                } else {
                    Log.e("ProdutoRepository", "Erro ao carregar produtos no topo");
                }
                isDataLoaded = true; // Marcar como carregado
                callback.onDataLoaded(produtos, produtosOferta, produtosNoTopo);
            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                Log.e("ProdutoRepository", "Falha ao chamar a API para produtos no topo: " + t.getMessage());
            }
        });
    }

    public interface ProdutoDataCallback {
        void onDataLoaded(List<Produto> produtos, List<Produto> produtosOferta, List<Produto> produtosNoTopo);
    }
}

