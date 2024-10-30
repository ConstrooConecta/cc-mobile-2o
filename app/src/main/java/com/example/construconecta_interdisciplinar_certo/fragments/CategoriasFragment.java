package com.example.construconecta_interdisciplinar_certo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.construconecta_interdisciplinar_certo.ProdutoCategoriaActivity;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.CategoriaApi;
import com.example.construconecta_interdisciplinar_certo.models.Categoria;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoriasFragment extends Fragment {
    private ListView listViewCategorias;
    private SearchView searchView;
    private List<Categoria> categorias = new ArrayList<>();
    private List<String> categoriasList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public CategoriasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categorias, container, false);

        // Configura o ListView
        listViewCategorias = view.findViewById(R.id.listViewCategorias);
        searchView = view.findViewById(R.id.searchBar);

        // Configurando o ArrayAdapter para o ListView
        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_categoria, R.id.textViewCategoria, categoriasList);
        listViewCategorias.setAdapter(adapter);

        // Dados das categorias (isso pode ser alterado conforme necessário)
        chamarAPICategoria();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // Configurando o clique nos itens da ListView
        listViewCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String categoriaSelecionada = categoriasList.get(position);
                Toast.makeText(getActivity(), "Categoria: " + categoriaSelecionada, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), ProdutoCategoriaActivity.class);
                intent.putExtra("categoria", categoriaSelecionada);
                startActivity(intent);
            }
        });
        return view;
    }

    private void chamarAPICategoria() {
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
                    categorias = response.body();

                    categoriasList.clear(); // Limpa a lista antes de adicionar novos itens
                    for (Categoria categoria : categorias) {
                        categoriasList.add(categoria.getNome());
                    }

                    // Atualiza o ArrayAdapter com os novos nomes das categorias
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Erro ao carregar categorias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Toast.makeText(getActivity(), "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
