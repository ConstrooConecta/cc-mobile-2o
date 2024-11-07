package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.PagamentoProduto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PagamentoProdutoApi {
    @POST("product-payment/add")
    Call<PagamentoProduto> createProductPayment(@Body PagamentoProduto productPayment);

    @GET("product-payment/findByOrder/{orderId}")
    Call<PagamentoProduto> findByOrderId(@Path("orderId") Integer orderId);
}
