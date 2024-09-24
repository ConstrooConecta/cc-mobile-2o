package com.example.construconecta_interdisciplinar_certo.network;

import com.example.construconecta_interdisciplinar_certo.network.model.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("user/add")
    Call<Usuario>inserirUser(@Body Usuario user);

}
