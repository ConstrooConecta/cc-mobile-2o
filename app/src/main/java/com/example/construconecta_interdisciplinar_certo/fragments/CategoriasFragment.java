package com.example.construconecta_interdisciplinar_certo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.models.Categoria;
import com.example.construconecta_interdisciplinar_certo.repositories.CategoriaRepository;
import com.example.construconecta_interdisciplinar_certo.shop.categoria.ProdutoCategoriaActivity;

import java.util.ArrayList;
import java.util.List;

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

        // Carregar dados das categorias
        carregarCategorias();

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
                Intent intent = new Intent(getActivity(), ProdutoCategoriaActivity.class);
                intent.putExtra("categoria", categoriaSelecionada);
                startActivity(intent);
            }
        });

        return view;
    }

    private void carregarCategorias() {
        CategoriaRepository.getInstance().loadData(categorias -> {
            this.categorias = categorias;
            categoriasList.clear(); // Limpa a lista antes de adicionar novos itens
            for (Categoria categoria : this.categorias) {
                // Adiciona a categoria se não estiver na lista
                if (!categoriasList.contains(categoria.getNome())) {
                    categoriasList.add(categoria.getNome());
                }
            }
            // Atualiza o ArrayAdapter com os novos nomes das categorias
            adapter.notifyDataSetChanged();
        });
    }
}

