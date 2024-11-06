package com.example.construconecta_interdisciplinar_certo.shop.conta;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.adapters.AdapterMeusPedidos;
import com.example.construconecta_interdisciplinar_certo.apis.ItemPedidoApi;
import com.example.construconecta_interdisciplinar_certo.apis.PedidoApi;
import com.example.construconecta_interdisciplinar_certo.models.ItemPedido;
import com.example.construconecta_interdisciplinar_certo.models.Pedido;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeusPedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AdapterMeusPedidos adapterPedidos;
    private List<ItemPedido> itemPedidos = new ArrayList<>();  // Mudei para uma lista de ItemPedido
    private PedidoApi pedidoApi;
    private ItemPedidoApi itemPedidoApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_pedidos);

        recyclerView = findViewById(R.id.recyclerViewMeusPedidos);
        progressBar = findViewById(R.id.progressBar5);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterPedidos = new AdapterMeusPedidos(itemPedidos, this);  // Usando ItemPedido no lugar de Pedido
        recyclerView.setAdapter(adapterPedidos);

        ((ImageButton) findViewById(R.id.backButton)).setOnClickListener(v -> finish());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pedidoApi = retrofit.create(PedidoApi.class);
        itemPedidoApi = retrofit.create(ItemPedidoApi.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String usuarioId = user.getUid();
            buscarPedidos(usuarioId);
        }
    }

    private void buscarPedidos(String usuarioId) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        Call<List<Pedido>> call = pedidoApi.findByUserId(usuarioId);
        call.enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    for (Pedido pedido : response.body()) {
                        // Para cada pedido, busque seus itens
                        buscarItensDoPedido(pedido);
                    }
                } else {
                    Toast.makeText(MeusPedidosActivity.this, "Erro ao carregar os pedidos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MeusPedidosActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarItensDoPedido(Pedido pedido) {
        // Buscar todos os itens do pedido
        Call<List<ItemPedido>> itemCall = itemPedidoApi.findByOrderId(pedido.getPedidoId());
        itemCall.enqueue(new Callback<List<ItemPedido>>() {
            @Override
            public void onResponse(Call<List<ItemPedido>> call, Response<List<ItemPedido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemPedidos.addAll(response.body());  // Adicionando todos os itens do pedido na lista
                    adapterPedidos.notifyDataSetChanged();  // Notifica a mudança para atualizar a interface
                }
            }

            @Override
            public void onFailure(Call<List<ItemPedido>> call, Throwable t) {
                Toast.makeText(MeusPedidosActivity.this, "Falha ao carregar os itens do pedido", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
