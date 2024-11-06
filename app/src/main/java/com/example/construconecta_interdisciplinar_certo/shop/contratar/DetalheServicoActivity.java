package com.example.construconecta_interdisciplinar_certo.shop.contratar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;

public class DetalheServicoActivity extends BaseActivity {
    private ImageView imageViewServico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_servico);

        Intent intent = getIntent();
        String nome = intent.getStringExtra("nomeServico");
        String descricao = intent.getStringExtra("descricao");
        //o preço é um double
        double preco = intent.getDoubleExtra("preco", 0.0);
        String imagem = intent.getStringExtra("imageUrl");

        //setando os dados na tela
        imageViewServico = findViewById(R.id.imageViewServico);
        TextView textViewNomeServico = findViewById(R.id.textViewNomeServico);
        TextView textViewDescricao = findViewById(R.id.textViewDescricao);
        TextView textViewPreco = findViewById(R.id.textViewFaixaPreco);
        textViewNomeServico.setText(nome);
        textViewDescricao.setText(descricao);
        //o preço é um double
        textViewPreco.setText("R$ " + preco);

        Glide.with(this).load(imagem).into(imageViewServico);
    }
}
