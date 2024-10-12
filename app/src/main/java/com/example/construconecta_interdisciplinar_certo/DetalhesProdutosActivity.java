package com.example.construconecta_interdisciplinar_certo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class DetalhesProdutosActivity extends AppCompatActivity {
    private TextView nomeProduto, precoProduto, descontoProduto, de, descricaoProdutoTextView;
    private ImageView imagemProduto, oferta, lojaAle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_detalhes); // Criar este layout

        // Inicializando as views
        nomeProduto = findViewById(R.id.nomeProdutoDetalhes);
        precoProduto = findViewById(R.id.precoProdutoDetalhes);
        descontoProduto = findViewById(R.id.descontoProdutoDetalhes);
        imagemProduto = findViewById(R.id.imagemProdutoDetalhes);
        descricaoProdutoTextView = findViewById(R.id.descricaoProduto);
        oferta = findViewById(R.id.imageView6);
        de = findViewById(R.id.textViewDe);
        TextView percentualDesconto = findViewById(R.id.percentualDesconto); // Adicione essa TextView no seu layout
        lojaAle = findViewById(R.id.lojaAle);

        String url = "https://s2.glbimg.com/H7t2zpkTT9ajl8cNEBqi8kv1OKk=/e.glbimg.com/og/ed/f/original/2016/04/13/abc.jpg";

        Glide.with(this)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(lojaAle);



        // Recebendo os dados via Intent
        Intent intent = getIntent();
        if (intent != null) {
            String nome = intent.getStringExtra("nomeProduto");
            String imagemUrl = intent.getStringExtra("imagemProduto");
            String descricaoProduto = intent.getStringExtra("descricao");
            double preco = intent.getDoubleExtra("Preco", 0);
            double desconto = intent.getDoubleExtra("Desconto", 0);

            // Preencher as views com os dados recebidos
            nomeProduto.setText(nome);
            precoProduto.setText("R$ " + preco);
            descricaoProdutoTextView.setText(descricaoProduto);

            if (desconto > 0) {
                // Calcular o preço com desconto e a porcentagem
                double precoComDesconto = preco - desconto;
                double percentual = (desconto / preco) * 100;

                // Exibir o preço com desconto
                descontoProduto.setText("R$ " + String.format("%.2f", precoComDesconto));



                // mostrar o percentual de desconto
                percentualDesconto.setText("-" + String.format("%.0f", percentual) + "%");
                percentualDesconto.setTextColor(Color.RED); // Muda a cor para vermelho

                // Riscar o preço original
                precoProduto.setPaintFlags(precoProduto.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                precoProduto.setTextColor(Color.GRAY); // Muda a cor para cinza
            } else {
                oferta.setVisibility(View.GONE);
                descontoProduto.setText("");
                de.setText("");
                percentualDesconto.setVisibility(View.GONE);
                precoProduto.setTextColor(Color.BLACK);
                //deixar o produto com 24sp
                precoProduto.setTextSize(24);
                //colocar um pouco mais pra cima
                precoProduto.setPadding(0, 0, 0, 20);
            }

            // Carregar a imagem com Glide
            Glide.with(this).load(imagemUrl).into(imagemProduto);
        }
    }

    public void TelaEmBreve(View view) {
        //abrir a intent e abrir nova tela de "Em_breve"
        Intent intent = new Intent(this, AtualizacoesFuturas.class);
        startActivity(intent);

    }
    //ao clicar no vetor do coração, ele pega uma outra imagem do drawable pra ficar no lugar, que é o coração vermelho
    public void selecionar_Favorito(View view) {
        ImageView coracaoNormal = findViewById(R.id.coracao_favorito);

        // Verifica se a tag é nula ou se o valor da tag é "selecionado"
        if (coracaoNormal.getTag() == null || coracaoNormal.getTag().equals("selecionado")) {
            // Se for nula ou já estiver "selecionado", volta para o ícone original
            coracaoNormal.setImageResource(R.drawable.lovelove);
            coracaoNormal.setTag("normal"); // Marca como não selecionado
        } else {
            // Caso contrário, muda para o ícone de coração vermelho
            coracaoNormal.setImageResource(R.drawable.coracao_selecionado);
            coracaoNormal.setTag("selecionado"); // Marca como selecionado
        }
    }


}
