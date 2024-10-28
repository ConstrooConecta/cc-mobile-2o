package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Categoria;
import com.example.construconecta_interdisciplinar_certo.models.Produto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoriaApi {
    @GET("category/categories")
    Call<List<Categoria>> getAllCategories();


}
