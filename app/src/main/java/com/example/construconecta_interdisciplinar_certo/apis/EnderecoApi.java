package com.example.construconecta_interdisciplinar_certo.apis;

import com.example.construconecta_interdisciplinar_certo.models.Endereco;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EnderecoApi {
    @POST("address/add")
    Call<Endereco> createAddress(@Body Endereco address);

    @GET("/address/findByUsuario/{usuario}")
    Call<List<Endereco>> getEnderecosByUsuario(@Path("usuario") String usuarioId);

    @DELETE("/address/delete/{addressId}")
    Call<Void> deleteAddress(@Path("addressId") int addressId);
}
