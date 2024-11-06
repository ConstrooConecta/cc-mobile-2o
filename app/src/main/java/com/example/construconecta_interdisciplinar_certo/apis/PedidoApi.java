package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Pedido;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PedidoApi {
    @POST("order/add")
    Call<Pedido> createOrder(@Body Pedido order);

    @GET("order/findByUserId/{userId}")
    Call<List<Pedido>> findByUserId(@Path("userId") String userId);

    @GET("order/findByOrderId/{orderId}")
    Call<Pedido> findById(@Path("orderId") Integer orderId);
}
