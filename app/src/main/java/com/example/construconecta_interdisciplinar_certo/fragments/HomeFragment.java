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

import com.example.construconecta_interdisciplinar_certo.adapters.AdapterProdutoHome;
import com.example.construconecta_interdisciplinar_certo.adapters.AdapterProdutoNoTopo;
import com.example.construconecta_interdisciplinar_certo.adapters.AdapterProdutoOfertas;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.repositories.ProdutoRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView produtoRecyclerView, recyclerViewOferta, recyclerViewNoTopo;
    private AdapterProdutoOfertas adapterOferta;
    private AdapterProdutoNoTopo adapterNoTopo;
    private AdapterProdutoHome adapter;
    private List<Produto> produtos, produtosOferta, produtosNoTopo;
    private ProgressBar progressBar;
    private TextView textView12, textView13, desconto;
    private ImageView imagem;
    private View barrafixa;
    private SearchView searchView;

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
        produtoRecyclerView = view.findViewById(R.id.recyclerViewRelevantes);
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

        // Chamar dados do repositório
        ProdutoRepository.getInstance().loadData((produtos, produtosOferta, produtosNoTopo) -> {
            this.produtos.clear();
            this.produtos.addAll(produtos);
            adapter.notifyDataSetChanged();

            this.produtosOferta.clear();
            this.produtosOferta.addAll(produtosOferta);
            adapterOferta.notifyDataSetChanged();

            this.produtosNoTopo.clear();
            this.produtosNoTopo.addAll(produtosNoTopo);
            adapterNoTopo.notifyDataSetChanged();

            verificarFinalizacao();
        });
        return view;
    }

    private void verificarFinalizacao() {
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
