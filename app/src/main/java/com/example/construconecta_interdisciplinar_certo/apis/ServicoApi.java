package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.models.Servico;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServicoApi {

    @GET("service/services")
    Call<List<Servico>> findAllServices();

    @GET("service/findByServiceTag/serviceTagName/{serviceTagName}")
    Call<List<Servico>> findByTagName(@Path("serviceTagName") String serviceTagName);


}
