package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CarrinhoApi {
    //add coisas carrinho
    //shopping-cart/add


    @POST("shopping-cart/add")
    Call<Carrinho> addCarrinho(@Body Carrinho carrinho);

    @GET("shopping-cart/findByUserId/{usuario}")
    Call<List<Carrinho>> findByUserId(@Path("usuario") String usuario);
}
