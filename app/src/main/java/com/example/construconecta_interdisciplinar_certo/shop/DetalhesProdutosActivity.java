package com.example.construconecta_interdisciplinar_certo.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.CarrinhoApi;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.checkout.CarrinhoActivity;
import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.onboarding.CadastroInfosSeguranca4;
import com.example.construconecta_interdisciplinar_certo.ui.AtualizacoesFuturas;
import com.example.construconecta_interdisciplinar_certo.ui.MainActivity;
import com.example.construconecta_interdisciplinar_certo.utils.ButtonUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetalhesProdutosActivity extends AppCompatActivity {
    private TextView nomeProduto, precoProduto, descontoProduto, de, descricaoProdutoTextView;
    private ImageView imagemProduto, oferta, lojaAle, coracaoFavorito;
    private String imagemUrl , idProduto;
    private Double precoProdutoToCarrinho;
    private Boolean favorito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_detalhes); // Criar este layout
        Intent intent = getIntent();
        idProduto = intent.getStringExtra("id");
        favorito = intent.getBooleanExtra("favorito",false);

        // Inicializando as views
        coracaoFavorito = findViewById(R.id.coracao_favorito);
        if (favorito) {
            coracaoFavorito.setImageResource(R.drawable.coracao_selecionado);
            coracaoFavorito.setTag("selecionado");

        }
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
        if (intent != null) {
            String nome = intent.getStringExtra("nomeProduto");
            imagemUrl = intent.getStringExtra("imagemProduto");
            String descricaoProduto = intent.getStringExtra("descricao");
            double preco = intent.getDoubleExtra("Preco", 0);
            double desconto = intent.getDoubleExtra("Desconto", 0);


            // Preencher as views com os dados recebidos
            nomeProduto.setText(nome);
            precoProduto.setText("R$ " + preco);
            descricaoProdutoTextView.setText(descricaoProduto);
            precoProdutoToCarrinho=preco;

            if (desconto > 0) {
                // Calcular o preço com desconto e a porcentagem
                double precoComDesconto = preco - (preco * desconto);
                precoProdutoToCarrinho = precoComDesconto;
                double percentual = ((preco-precoComDesconto)/preco)*100;

                // Exibir o preço com desconto
                descontoProduto.setText("R$ " + String.format("%.2f", precoComDesconto));



                // mostrar o percentual de desconto
                percentualDesconto.setText("-" + String.format("%.2f", percentual) + "%");
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
    public void voltarHome(View view) {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (idProduto != null && !idProduto.isEmpty()) {
            try {
                int idProdutoInt = Integer.parseInt(idProduto);  // Converte a string para int
                assert usuarioAtual != null;
                Carrinho carrinho = new Carrinho(
                        1000 + new Random().nextInt(9999 - 1000),
                        usuarioAtual.getUid(),
                        idProdutoInt,
                        1,
                        imagemUrl,
                        precoProdutoToCarrinho
                );
                addToShoppingCart(carrinho);
            } catch (NumberFormatException e) {
                e.printStackTrace();  // Trate o erro se o valor não puder ser convertido
                Toast.makeText(this, "Erro ao converter o ID para número!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "ID do produto inválido ou nulo!", Toast.LENGTH_SHORT).show();
        }

        mostrarModalAdicionadoAoCarrinho();
        //depois de 3 segundos ele abre a tela home
        new Handler().postDelayed(this::openHome, 3000);

    }

    private void openHome() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }



        private void mostrarModalAdicionadoAoCarrinho() {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetalhesProdutosActivity.this);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.modal_adicionado_carrinho, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.show();

            alertDialog.getWindow().setLayout(
                    (int) getResources().getDimension(R.dimen.modal_width),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );

            new Handler().postDelayed(alertDialog::dismiss, 2000);

        }

    private void addToShoppingCart(Carrinho carrinho) {
        String url = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarrinhoApi api = retrofit.create(CarrinhoApi.class);
        Call<Carrinho> call = api.addCarrinho(carrinho);

        call.enqueue(new Callback<Carrinho>() {
            @Override
            public void onResponse(Call<Carrinho> call, Response<Carrinho> response) {
                if(response.isSuccessful()) {
                    Carrinho createdUser = response.body();
                    Log.d("POST_SUCCESS", "Carrinho criado: " + createdUser.getCarrinhoId());

                } else {
                    // Tratar erro ao adicionar no banco
                    Log.d("REQUEST_BODY", carrinho.toString()); // Certifique-se de que o método toString() do seu objeto Carrinho exiba os dados corretamente
                    Log.e("POST_ERROR", "Erro ao salvar carrinho. Código: " + response.code());
                    Toast.makeText(DetalhesProdutosActivity.this, "Erro ao salvar carrinho no banco de dados. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<Carrinho> call, Throwable t) {

                Toast.makeText(DetalhesProdutosActivity.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void abrirCarrinho(View view) {
        Intent intent = new Intent(this, CarrinhoActivity.class);
        startActivity(intent);
    }


}
