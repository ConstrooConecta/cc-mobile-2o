package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ProdutoApi {
    @GET("product/products")
    Call<List<Produto>> findUser();
}
