package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Carrinho;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CarrinhoApi {
    @POST("shopping-cart/add")
    Call<Carrinho> addCarrinho(@Body Carrinho carrinho);

    @GET("shopping-cart/findByUserId/{usuario}")
    Call<List<Carrinho>> findByUserId(@Path("usuario") String usuario);

    @DELETE("shopping-cart/deleteByUsuarioId/{userId}")
    Call<Void> deleteCarrinhoByUserId(@Path("userId") String userId);

    //apagar item do carrinho
    @DELETE("/shopping-cart/delete/{shoppingCartId}")
    Call<ResponseBody> deleteCarrinho(@Path("shoppingCartId") int shoppingCartId);

    @DELETE("shopping-cart/deleteByUsuarioId/{userId}")
    Call<ResponseBody> deleteAll_carrinho(@Path("userId") String userId);
}
