package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.PagamentoPlano;
import com.example.construconecta_interdisciplinar_certo.models.Produto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PagamentoPlanoApi {


    @GET("payment-plan/payments-plan")
    Call<List<PagamentoPlano>> findAll();

    @GET("payment-plan/findByUserId/{usuario}")
    Call<List<PagamentoPlano>> findByUserId(@Path("usuario") String usuario);

    @POST("payment-plan/add")
    Call<PagamentoPlano> createPaymentPlan(@Body PagamentoPlano pagamentoPlano);
}
