package com.example.construconecta_interdisciplinar_certo.shop.contratar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.adapters.AdapterServico;
import com.example.construconecta_interdisciplinar_certo.apis.ServicoApi;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.models.Servico;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecyclerServicosActivity extends BaseActivity {
    private ProgressBar progressBar;
    private RecyclerView servicoRecyclerView;
    private SearchView searchBar;
    private AdapterServico adapter;
    private List<Servico> servicos;
    private Map<String, Usuario> usuariosMap = new HashMap<>();
    private List<Usuario> usuarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_servicos);
        progressBar = findViewById(R.id.progressBar2Servico);

        searchBar = findViewById(R.id.searchBarServico);

        servicoRecyclerView = findViewById(R.id.recyclerViewServicos);
        servicoRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        servicos = new ArrayList<>();
        adapter = new AdapterServico(servicos, this);
        servicoRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        // Pega o valor extra que foi passado
        String tagName = intent.getStringExtra("tagName");
        chamarAPIRetrofitServicos(tagName);

        // Inicializa a lista de usuários e o mapa
        carregarUsuarios();

        // Configura a SearchBar para filtrar a lista de serviços
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
    }

    private void initializeUsuariosMap() {
        for (Usuario usuario : usuarios) {
            usuariosMap.put(usuario.getUid(), usuario); // Associa o UID ao objeto Usuario
        }
    }

    private void carregarUsuarios() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<List<Usuario>> call = usuarioApi.findAllUsers();

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                progressBar.setVisibility(View.GONE); // Esconde a barra de progresso ao carregar
                if (response.isSuccessful() && response.body() != null) {
                    usuarios.clear();
                    usuarios.addAll(response.body()); // Preenche a lista de usuários com a resposta da API
                    initializeUsuariosMap(); // Cria o mapa de UID para usuário
                } else {
                    Toast.makeText(RecyclerServicosActivity.this, "Erro ao carregar usuários", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RecyclerServicosActivity.this, "Erro na chamada de usuários: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filterList(String query) {
        List<Servico> filteredList = new ArrayList<>();
        for (Servico servico : servicos) {
            Usuario usuario = usuariosMap.get(servico.getServicoId()); // Busca o usuário pelo UID do serviço
            if (usuario != null && usuario.getNomeUsuario().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(servico);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Nenhum resultado encontrado", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setFilteredList(filteredList);
        }
    }

    private void chamarAPIRetrofitServicos(String tagName) {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicoApi servicoApi = retrofit.create(ServicoApi.class);
        Call<List<Servico>> call = servicoApi.findByTagName(tagName);

        call.enqueue(new Callback<List<Servico>>() {
            @Override
            public void onResponse(Call<List<Servico>> call, Response<List<Servico>> response) {
                Log.d("API Response", "Código de status: " + response.code());
                Log.d("API Response", "Corpo: " + response.body());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("API NoTopo", "Dados recebidos: " + response.body().toString());
                        servicos.clear();
                        servicos.addAll(response.body());
                        adapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                        servicoRecyclerView.setVisibility(View.VISIBLE);
                        searchBar.setVisibility(View.VISIBLE);

                    } else {
                        Log.d("API NoTopo", "Corpo da resposta é nulo");
                    }
                } else {
                    Log.d("API NoTopo", "Erro: Código de status " + response.code());
                }

            }


            @Override
            public void onFailure(Call<List<Servico>> call, Throwable throwable) {
                Toast.makeText(RecyclerServicosActivity.this, "Erro ao mostrar serviços: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}