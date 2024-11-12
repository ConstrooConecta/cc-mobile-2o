package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Desconto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DescontoApi {
    @GET("discounts/findByVoucherName/{voucherName}")
    Call<List<Desconto>> findByVoucherName(@Path("voucherName") String cupom);
}

