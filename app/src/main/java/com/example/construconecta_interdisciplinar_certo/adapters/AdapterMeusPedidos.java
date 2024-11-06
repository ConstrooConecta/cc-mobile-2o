package com.example.construconecta_interdisciplinar_certo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.ItemPedidoApi;
import com.example.construconecta_interdisciplinar_certo.apis.PedidoApi;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.models.ItemPedido;
import com.example.construconecta_interdisciplinar_certo.models.Pedido;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterMeusPedidos extends RecyclerView.Adapter<AdapterMeusPedidos.ViewHolder> {

    private List<ItemPedido> itemPedidos;
    private Context context;
    private ProdutoApi produtoApi;
    private PedidoApi pedidoApi;

    public AdapterMeusPedidos(List<ItemPedido> itemPedidos, Context context) {
        this.itemPedidos = itemPedidos;
        this.context = context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Inicializando produtoApi e pedidoApi
        produtoApi = retrofit.create(ProdutoApi.class);
        pedidoApi = retrofit.create(PedidoApi.class);  // Inicialize aqui
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meus_pedidos_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemPedido itemPedido = itemPedidos.get(position);

        // Buscar o produto relacionado ao item do pedido
        buscarProduto(itemPedido.getProduto(), holder);

        // Buscar o pedido associado ao item
        buscarPedido(itemPedido.getPedido(), holder);
    }
    private void buscarPedido(Integer pedidoId, ViewHolder holder) {
        Call<Pedido> pedidoCall = pedidoApi.findById(pedidoId);
        pedidoCall.enqueue(new Callback<Pedido>() {
            @Override
            public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pedido pedido = response.body();

                    // Verificar o status do pedido baseado na data de entrega
                    String status = "A caminho";
                    String txtDtPedido = "Pedido feito em " + pedido.getDataPedido();
                    String txtDtEntrega = "Chegará em " + pedido.getDataEntrega();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String dataHoje = sdf.format(new Date());

                    // Se a data de entrega for hoje ou no passado, o status é "Entregue"
                    if (dataHoje.equals(pedido.getDataEntrega()) ||
                            pedido.getDataEntrega().compareTo(dataHoje) < 0) {
                        status = "Entregue";
                        txtDtEntrega = "Entregue em " + pedido.getDataEntrega();
                    }

                    // Atualiza a interface com o status e a data de entrega
                    holder.statusPedido.setText(status);
                    holder.statusPedido.setTextColor(0xFF62AC61);
                    holder.txtDtPedido.setText(txtDtPedido);
                    holder.txtDtEntrega.setText(txtDtEntrega);
                }
            }

            @Override
            public void onFailure(Call<Pedido> call, Throwable t) {
                Toast.makeText(context, "Falha ao carregar os dados do pedido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarProduto(Integer produtoId, ViewHolder holder) {
        Call<Produto> produtoCall = produtoApi.findProductsById(produtoId);
        produtoCall.enqueue(new Callback<Produto>() {
            @Override
            public void onResponse(Call<Produto> call, Response<Produto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Produto produto = response.body();
                    // Configura o nome e a imagem do produto
                    holder.txtNomeProduto.setText(produto.getNomeProduto());
                    Glide.with(context)
                            .load(produto.getImagem())
                            .into(holder.produtoImg);
                }
            }

            @Override
            public void onFailure(Call<Produto> call, Throwable t) {
                Toast.makeText(context, "Falha ao carregar o produto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemPedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView statusPedido, txtDtEntrega, txtDtPedido, txtNomeProduto;
        ImageView produtoImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusPedido = itemView.findViewById(R.id.statusPedido);
            txtDtEntrega = itemView.findViewById(R.id.txtDtEntrega);
            txtDtPedido = itemView.findViewById(R.id.txtDtPedido);
            txtNomeProduto = itemView.findViewById(R.id.txtNomeProduto);
            produtoImg = itemView.findViewById(R.id.produtoImg);
        }
    }
}
