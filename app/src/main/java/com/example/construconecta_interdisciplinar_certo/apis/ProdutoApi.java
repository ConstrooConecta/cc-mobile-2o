package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProdutoApi {
    @GET("product/products")
    Call<List<Produto>> findUser();

    @GET("product/findByTopic/{topico}")
    Call<List<Produto>> findByTopic(@Path("topico") int topico);

    @GET("product/findByCategory/categoryName/{nomeCategoria}")
    Call<List<Produto>> findByCategoryName(@Path("nomeCategoria") String nomeCategoria);

    @POST("product/add")
    Call<Produto> createProduct(@Body Produto product);


}
