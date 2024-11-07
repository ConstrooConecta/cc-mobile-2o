package com.example.construconecta_interdisciplinar_certo.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.shop.home.DetalhesProdutosActivity;

import java.util.List;

public class AdapterProdutoHome extends RecyclerView.Adapter<AdapterProdutoHome.ProdutoViewHolder> {
    private List<Produto> produtos;

    // Construtor
    public AdapterProdutoHome(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public void setFilteredList(List<Produto> Listaprodutos) {
        this.produtos = Listaprodutos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto, parent, false);
        ProdutoViewHolder pvh = new ProdutoViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        // Atualizando o conteúdo das views
        Produto produto = produtos.get(position);
        holder.cod.setText("Cód. " + produto.getProdutoId()); // Exibindo o cod
        holder.nome.setText(produto.getNomeProduto());
        holder.nome.setTypeface(holder.nome.getTypeface(), android.graphics.Typeface.BOLD);
        if (produto.getDesconto() > 0) {
            holder.preco.setText("R$ " + String.valueOf(produto.getPreco()));
            holder.preco.setPaintFlags(holder.preco.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.preco.setTextColor(Color.GRAY); // Cor cinza para o preço original
            holder.preco.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.preco.setVisibility(View.VISIBLE); // Torna visível o preço original
            holder.preco.setTypeface(null, Typeface.NORMAL);

            // Mostra o preço com desconto em vermelho e tamanho 24sp
            double precoComDesconto = produto.getPreco() - (produto.getPreco() * produto.getDesconto());

            holder.desconto.setText("R$ " + String.format("%.2f", precoComDesconto));
            holder.desconto.setTextColor(Color.RED); // Muda a cor do desconto para vermelho
            holder.desconto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24); // Tamanho de 24sp
            holder.desconto.setTypeface(null, Typeface.BOLD);
            holder.desconto.setVisibility(View.VISIBLE);
        } else {
            holder.preco.setText("R$ " + String.valueOf(produto.getPreco()) + " cada");
            holder.desconto.setVisibility(View.GONE);
        }

        // Carregando a imagem usando Glide
        String url = produto.getImagem();
        if (url == null || url.isEmpty()) {
            url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8lRbS7eKYzDq-Ftxc1p8G_TTw2unWBMEYUw&s";
        }
        Glide.with(holder.imagem.getContext()).asBitmap().load(url).into(holder.imagem);

        // Adicionando o clique no item
        String finalUrl = url;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrindo a activity de detalhes do produto
                Intent intent = new Intent(holder.itemView.getContext(), DetalhesProdutosActivity.class);
                intent.putExtra("nomeProduto", produto.getNomeProduto());
                intent.putExtra("imagemProduto", finalUrl);
                intent.putExtra("usuario", produto.getUsuario());
                intent.putExtra("Preco", produto.getPreco());
                intent.putExtra("Desconto", produto.getDesconto());
                intent.putExtra("descricao", produto.getDescricao());
                intent.putExtra("id", produto.getProdutoId());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Verificando se 'produtos' não é nulo antes de retornar o tamanho
        return (produtos != null) ? produtos.size() : 0;
    }

    // ViewHolder interno
    public static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView nome;
        TextView cod;
        ImageView imagem;
        TextView preco;
        TextView desconto;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializando as views
            nome = itemView.findViewById(R.id.nomeProduto);
            imagem = itemView.findViewById(R.id.fotoProdutoCard);
            preco = itemView.findViewById(R.id.Preco);
            desconto = itemView.findViewById(R.id.Desconto);
            cod = itemView.findViewById(R.id.Codigo);

        }
    }
}
