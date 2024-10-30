package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.TagServico;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TagServicoApi {
    @GET("serviceTag/servicesTag")
    Call<List<TagServico>> findAllTagServices();
}
