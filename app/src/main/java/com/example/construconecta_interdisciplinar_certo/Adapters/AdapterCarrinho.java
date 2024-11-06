package com.example.construconecta_interdisciplinar_certo.Adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.CarrinhoApi;
import com.example.construconecta_interdisciplinar_certo.checkout.CarrinhoActivity;
import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.Produto;

import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterCarrinho extends RecyclerView.Adapter<AdapterCarrinho.CarrinhoViewHolder> {

    private List<Carrinho> produtosCarrinho;
    private List<Produto> produtos;  // Lista de produtos

    // Construtor
    public AdapterCarrinho(List<Carrinho> produtosCarrinho, List<Produto> produtos) {
        this.produtosCarrinho = produtosCarrinho;
        this.produtos = produtos;  // Passa a lista de produtos
    }

    @NonNull
    @Override
    public CarrinhoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrinho, parent, false);
        return new CarrinhoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CarrinhoViewHolder holder, int position) {
        Carrinho carrinho = produtosCarrinho.get(position);

        // Busca o produto pelo ID
        Produto produto = buscarProdutoPorId(String.valueOf(carrinho.getProduto()));

        // Atualiza os dados do produto no carrinho
        if (produto != null) {
            holder.nomeProdutoCarrinho.setText(produto.getNomeProduto());  // Exibe o nome do produto
        } else {
            holder.nomeProdutoCarrinho.setText("Produto não encontrado");
        }

        holder.precoProdutoCarrinho.setText("R$ " + String.format("%.2f", carrinho.getValorTotal()));
        holder.precoProdutoCarrinho.setTextColor(Color.RED);

        // Carregando a imagem do produto
        String url = carrinho.getProdutoImg();
        if (url == null || url.isEmpty()) {
            url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8lRbS7eKYzDq-Ftxc1p8G_TTw2unWBMEYUw&s";
        }
        Glide.with(holder.fotoProdutoCardCarrinho.getContext()).asBitmap().load(url).into(holder.fotoProdutoCardCarrinho);

        holder.iconRemoveFromCart.setOnClickListener(v -> {

            CarrinhoDelete(carrinho.getCarrinhoId(), v, position);
        });

    }


    // Método para buscar o produto pelo ID
    private Produto buscarProdutoPorId(String produtoId) {
        for (Produto produto : produtos) {
            if (Objects.equals(produto.getprodutoId(), produtoId)) {
                return produto;
            }
        }
        return null;  // Retorna null se o produto não for encontrado
    }

    @Override
    public int getItemCount() {
        return (produtosCarrinho != null) ? produtosCarrinho.size() : 0;
    }

    // ViewHolder interno
    public static class CarrinhoViewHolder extends RecyclerView.ViewHolder {
        TextView nomeProdutoCarrinho;
        ImageView fotoProdutoCardCarrinho;
        TextView precoProdutoCarrinho;
        ImageView iconRemoveFromCart;

        public CarrinhoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializando as views
            nomeProdutoCarrinho = itemView.findViewById(R.id.nomeProdutoCarrinho);
            fotoProdutoCardCarrinho = itemView.findViewById(R.id.fotoProdutoCardCarrinho);
            precoProdutoCarrinho = itemView.findViewById(R.id.PrecoProdutoCarrinho);
            iconRemoveFromCart = itemView.findViewById(R.id.iconRemoveFromCart);
        }
    }

    private void CarrinhoDelete(Integer shoppingCartId, View v, int position) {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarrinhoApi carrinhoApi = retrofit.create(CarrinhoApi.class);
        Call<ResponseBody> call = carrinhoApi.deleteCarrinho(shoppingCartId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Remova o item da lista e notifique o adapter
                    produtosCarrinho.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, produtosCarrinho.size());

                    Toast.makeText(v.getContext(), "Item removido do carrinho", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(v.getContext(), "Carrinho vazio", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Erro ao remover item do carrinho: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.d("Carrinho", "Erro ao remover item do carrinho: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(v.getContext(), "Erro ao carregar carrinho: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Carrinho", "Erro ao carregar carrinho: " + t.getMessage());
            }
        });

    }}
