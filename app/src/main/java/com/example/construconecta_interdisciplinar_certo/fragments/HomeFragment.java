package com.example.construconecta_interdisciplinar_certo.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterProdutoHome;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.models.Produto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private RecyclerView produtoRecyclerView;
    private AdapterProdutoHome adapter;
    private List<Produto> produtos; // Lista de produtos

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializando o RecyclerView
        produtoRecyclerView = view.findViewById(R.id.recyclerView);
        produtoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializando a lista e o Adapter com uma lista vazia
        produtos = new ArrayList<>();
        adapter = new AdapterProdutoHome(produtos);
        produtoRecyclerView.setAdapter(adapter);

        // Chamar a API
        chamar_API_Retrofit();

        return view;
    }

    private void chamar_API_Retrofit() {
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
                Log.d("API Response", "Código de status: " + response.code());
                Log.d("API Response", "Corpo: " + response.body());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        produtos.clear();
                        produtos.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Deu Certo", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "A resposta do corpo é nula", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Erro na resposta da API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<Produto>> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Deu errado: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
