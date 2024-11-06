package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.ItemPedido;
import com.example.construconecta_interdisciplinar_certo.models.Pedido;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ItemPedidoApi {
    @POST("orderItem/add")
    Call<ItemPedido> createOrderItem(@Body ItemPedido orderItem);

    @GET("orderItem/findByOrder/{orderId}")
    Call<List<ItemPedido>> findByOrderId(@Path("orderId") Integer orderId);
}
