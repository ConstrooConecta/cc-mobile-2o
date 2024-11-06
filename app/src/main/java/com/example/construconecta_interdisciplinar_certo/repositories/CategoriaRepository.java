package com.example.construconecta_interdisciplinar_certo.repositories;

import android.util.Log;

import com.example.construconecta_interdisciplinar_certo.apis.CategoriaApi;
import com.example.construconecta_interdisciplinar_certo.models.Categoria;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoriaRepository {
    private static CategoriaRepository instance;
    private List<Categoria> categorias;
    private boolean isDataLoaded = false;

    private CategoriaRepository() {
        categorias = new ArrayList<>();
    }

    public static CategoriaRepository getInstance() {
        if (instance == null) {
            instance = new CategoriaRepository();
        }
        return instance;
    }

    public void loadData(CategoriaDataCallback callback) {
        if (isDataLoaded) {
            callback.onDataLoaded(categorias);
            return;
        }

        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CategoriaApi categoriaApi = retrofit.create(CategoriaApi.class);
        Call<List<Categoria>> call = categoriaApi.getAllCategories();

        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categorias.addAll(response.body());
                    isDataLoaded = true; // Marcar como carregado
                    Log.d("CategoriaRepository", "Categorias carregadas");
                    callback.onDataLoaded(categorias);
                } else {
                    Log.e("CategoriaRepository", "Erro ao carregar categorias");
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e("CategoriaRepository", "Falha ao chamar a API para categorias: " + t.getMessage());
            }
        });
    }

    public interface CategoriaDataCallback {
        void onDataLoaded(List<Categoria> categorias);
    }
}
