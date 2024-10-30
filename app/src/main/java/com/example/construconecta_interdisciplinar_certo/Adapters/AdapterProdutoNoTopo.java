package com.example.construconecta_interdisciplinar_certo.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.construconecta_interdisciplinar_certo.shop.DetalhesProdutosActivity;

import java.util.List;

//AdapterProdutoNoTopo
public class AdapterProdutoNoTopo extends RecyclerView.Adapter<AdapterProdutoNoTopo.ProdutoViewHolder> {
    private List<Produto> listaProdutos;
    private Context context;

    public AdapterProdutoNoTopo(List<Produto> listaProdutos, Context context) {
        if (context != null) {
            this.context = context;
        } else {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.listaProdutos = listaProdutos;
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_no_topo, parent, false);
        return new ProdutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        Produto produto = listaProdutos.get(position);

        // Configura os textos
        holder.textViewTitulo.setText(produto.getNomeProduto());
        holder.textViewCodigo.setText("Cód. " + produto.getprodutoId());
        holder.textViewPrecoAntigo.setText("R$ " + produto.getPreco() + " cada");
        //deixar o preco antigo riscado
        double precoNovo = produto.getPreco() - (produto.getPreco() * produto.getDesconto());

        //fazendo precoNovo ter só 2 casas decimais
        precoNovo = Math.round(precoNovo * 100.0) / 100.0;
        holder.textViewPrecoNovo.setText("R$ " + precoNovo + " cada");

        String url = produto.getImagem();
        if (url == null || url.isEmpty()) {
            url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8lRbS7eKYzDq-Ftxc1p8G_TTw2unWBMEYUw&s";
        }
        // Usa Glide para carregar a imagem
        Glide.with(context)
                .load(url)  // URL ou recurso da imagem do produto
                .centerCrop()
                .into(holder.imageViewProduto);

        // Você pode adicionar aqui o que acontece quando o botão de favorito (coração) for clicado
        holder.imageViewCoracao.setOnClickListener(v -> {
            if (holder.imageViewCoracao.getTag() == null || holder.imageViewCoracao.getTag().equals("selecionado")) {
                // Se for nula ou já estiver "selecionado", volta para o ícone original
                holder.imageViewCoracao.setImageResource(R.drawable.favoritos);
                holder.imageViewCoracao.setTag("normal"); // Marca como não selecionado
            } else {
                // Caso contrário, muda para o ícone de coração vermelho
                holder.imageViewCoracao.setImageResource(R.drawable.coracao_selecionado);
                holder.imageViewCoracao.setTag("selecionado"); // Marca como selecionado
            }
        });

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
                intent.putExtra("id", produto.getprodutoId());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }

    public static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduto, imageViewCoracao;
        TextView textViewTitulo, textViewCodigo, textViewPrecoAntigo, textViewPrecoNovo;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProduto = itemView.findViewById(R.id.imageViewProdutoTopo);
            imageViewCoracao = itemView.findViewById(R.id.imageViewFavorito);
            textViewTitulo = itemView.findViewById(R.id.textViewNomeProdutoTopo);
            textViewCodigo = itemView.findViewById(R.id.textViewCodigoProdutoTopo);
            textViewPrecoAntigo = itemView.findViewById(R.id.textViewPrecoAntigoTopo);
            textViewPrecoNovo = itemView.findViewById(R.id.textViewPrecoAtualTopo);
        }
    }
}
