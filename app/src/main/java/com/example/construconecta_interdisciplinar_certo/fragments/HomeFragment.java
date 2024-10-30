package com.example.construconecta_interdisciplinar_certo.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterProdutoHome;
import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterProdutoNoTopo;
import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterProdutoOfertas;
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
    private RecyclerView produtoRecyclerView, recyclerViewOferta, recyclerViewNoTopo;
    private AdapterProdutoOfertas adapterOferta;
    private AdapterProdutoNoTopo adapterNoTopo;
    private AdapterProdutoHome adapter;
    private List<Produto> produtos, produtosOferta, produtosNoTopo;
    private ProgressBar progressBar; // Progress bar
    private TextView textView12, textView13, desconto;
    private ImageView imagem;
    private View barrafixa;
    private SearchView searchView;
    private int contador = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        textView12 = view.findViewById(R.id.textView12);
        textView13 = view.findViewById(R.id.textView13);
        desconto = view.findViewById(R.id.Desconto);
        searchView = view.findViewById(R.id.searchView);
        barrafixa = view.findViewById(R.id.barraFixa);
        imagem = view.findViewById(R.id.imageView5);

        // Inicializando o RecyclerView
        produtoRecyclerView = view.findViewById(R.id.recyclerView);
        produtoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializando a lista e o Adapter com uma lista vazia
        produtos = new ArrayList<>();
        adapter = new AdapterProdutoHome(produtos);
        produtoRecyclerView.setAdapter(adapter);

        //ofertas
        recyclerViewOferta = view.findViewById(R.id.recyclerViewOfertas);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewOferta.setLayoutManager(layoutManager);
        produtosOferta = new ArrayList<>();
        adapterOferta = new AdapterProdutoOfertas(produtosOferta, getContext());
        recyclerViewOferta.setAdapter(adapterOferta);

        //no topo
        recyclerViewNoTopo = view.findViewById(R.id.recyclerViewNoTopo);
        LinearLayoutManager layoutManagerNoTopo = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewNoTopo.setLayoutManager(layoutManagerNoTopo);

        produtosNoTopo = new ArrayList<>();
        adapterNoTopo = new AdapterProdutoNoTopo(produtosNoTopo, getContext());
        recyclerViewNoTopo.setAdapter(adapterNoTopo);

        // Chamar as APIs
        chamarAPIRetrofitRelevantes();
        chamarAPIRetrofitOfertas();
        chamarAPIRetrofitNoTopo();

        return view;
    }

    private void chamarAPIRetrofitRelevantes() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);
        Call<List<Produto>> call = produtoApi.findByTopic(3);

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
                        verificarFinalizacao();
                    } else {
                        Toast.makeText(getActivity(), "A resposta do corpo é nula", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Erro na resposta da API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Produto>> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Erro ao mostrar produtos relevantes: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chamarAPIRetrofitOfertas() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);
        Call<List<Produto>> call = produtoApi.findByTopic(2);

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                Log.d("API Response", "Código de status: " + response.code());
                Log.d("API Response", "Corpo: " + response.body());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        produtosOferta.clear();
                        produtosOferta.addAll(response.body());
                        adapterOferta.notifyDataSetChanged();
                        verificarFinalizacao();
                    } else {
                        Toast.makeText(getActivity(), "A resposta do corpo é nula", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Erro na resposta da API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Produto>> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Erro ao mostrar produtos em Ofertas: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chamarAPIRetrofitNoTopo() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);
        Call<List<Produto>> call = produtoApi.findByTopic(1);

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                Log.d("API Response", "Código de status: " + response.code());
                Log.d("API Response", "Corpo: " + response.body());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("API NoTopo", "Dados recebidos: " + response.body().toString());
                        produtosNoTopo.clear();
                        produtosNoTopo.addAll(response.body());
                        adapterNoTopo.notifyDataSetChanged();
                        verificarFinalizacao();
                    } else {
                        Log.d("API NoTopo", "Corpo da resposta é nulo");
                    }
                } else {
                    Log.d("API NoTopo", "Erro: Código de status " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Produto>> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Erro ao mostrar produtos No TOPO: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarFinalizacao() {
        contador++;
        if (contador == 3) {
            // Esconder a progress bar e mostrar as outras views
            progressBar.setVisibility(View.GONE);
            produtoRecyclerView.setVisibility(View.VISIBLE);
            recyclerViewOferta.setVisibility(View.VISIBLE);
            recyclerViewNoTopo.setVisibility(View.VISIBLE);
            textView12.setVisibility(View.VISIBLE);
            textView13.setVisibility(View.VISIBLE);
            desconto.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
            barrafixa.setVisibility(View.VISIBLE);
            imagem.setVisibility(View.VISIBLE);
        }
    }
}
