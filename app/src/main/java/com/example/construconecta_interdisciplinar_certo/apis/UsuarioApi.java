package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioApi {
    @POST("user/add")
    Call<Usuario> createUser(@Body Usuario user);

    @GET("user/users")
    Call<List<Usuario>> findAllUsers();

    @GET("user/findByTelefone/{telefone}")
    Call<List<Usuario>> findByTelefone(@Path("telefone") String telefone);

    @GET("user/findByNomeUsuario/{nomeUsuario}")
    Call<List<Usuario>> findByUsername(@Path("nomeUsuario") String nomeUsuario);

    @GET("user/findByCpf/{cpf}")
    Call<List<Usuario>> findByCpf(@Path("cpf") String cpf);

    @GET("user/findByEmail/{email}")
    Call<List<Usuario>> findByEmail(@Path("email") String email);

    @GET("user/findByUid/{uid}")
    Call<Usuario> findByUid(@Path("uid") String uid);

    @PUT("user/updateAll/{uid}")
    Call<Usuario> updateUser(@Path("uid") String uid, @Body Usuario user);

}
