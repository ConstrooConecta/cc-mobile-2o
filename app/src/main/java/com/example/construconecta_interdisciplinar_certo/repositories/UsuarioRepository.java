package com.example.construconecta_interdisciplinar_certo.repositories;

import android.util.Log;

import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuarioRepository {
    private static UsuarioRepository instance;
    private List<Usuario> usuarios;

    private UsuarioRepository() {
        usuarios = new ArrayList<>();
    }

    public static UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
        }
        return instance;
    }

    public void loadData(String email, UsuarioDataCallback callback) {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<List<Usuario>> call = usuarioApi.findByEmail(email);

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    usuarios.clear(); // Limpe a lista antes de adicionar novos dados
                    usuarios.addAll(response.body());
                    Log.d("UsuarioRepository", "Usuários carregados");
                    callback.onDataLoaded(usuarios);
                } else {
                    Log.e("UsuarioRepository", "Erro ao carregar usuários");
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("UsuarioRepository", "Falha ao chamar a API para usuários: " + t.getMessage());
            }
        });
    }

    public interface UsuarioDataCallback {
        void onDataLoaded(List<Usuario> usuarios);
    }
}
