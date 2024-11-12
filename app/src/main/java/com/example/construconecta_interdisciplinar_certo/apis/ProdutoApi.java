package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Produto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProdutoApi {
    @GET("product/products")
    Call<List<Produto>> findAllProducts();

    @POST("product/add")
    Call<Produto> createProduct(@Body Produto product);

    @PATCH("/product/update/{produtoId}")
    Call<Void> updateProduct(@Path("produtoId") int produtoId, @Body Map<String, Integer> estoque);


    @DELETE("/product/delete/{produtoId}")
    Call<Void> deleteProduct(@Path("produtoId") int produtoId);

    @GET("product/findByTopic/{topico}")
    Call<List<Produto>> findByTopic(@Path("topico") Integer topico);

    @GET("product/findByCategory/categoryName/{nomeCategoria}")
    Call<List<Produto>> findByCategoryName(@Path("nomeCategoria") String nomeCategoria);

    @GET("product/findById/{produtoId}")
    Call<Produto> findProductsById(@Path("produtoId") Integer produtoId);
}
