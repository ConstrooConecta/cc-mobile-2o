package com.example.construconecta_interdisciplinar_certo.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.Produto;

import java.util.List;
import java.util.Objects;

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

        public CarrinhoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializando as views
            nomeProdutoCarrinho = itemView.findViewById(R.id.nomeProdutoCarrinho);
            fotoProdutoCardCarrinho = itemView.findViewById(R.id.fotoProdutoCardCarrinho);
            precoProdutoCarrinho = itemView.findViewById(R.id.PrecoProdutoCarrinho);
        }
    }
}
