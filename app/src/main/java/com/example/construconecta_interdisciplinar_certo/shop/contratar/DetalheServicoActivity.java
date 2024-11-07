package com.example.construconecta_interdisciplinar_certo.shop.contratar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetalheServicoActivity extends BaseActivity {
    String usuarioUID;
    private ImageView imageViewServico;
    private Button buttonPedirOrcamento;
    private String telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_servico);

        buttonPedirOrcamento = findViewById(R.id.buttonPedirOrcamento);
        Intent intent = getIntent();
        String nome = intent.getStringExtra("nomeServico");
        String descricao = intent.getStringExtra("descricao");
        usuarioUID = intent.getStringExtra("usuarioUID");
        buscarUsuarioPorUid(usuarioUID);
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

        buttonPedirOrcamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetalheServicoActivity.this, "Botão clicado", Toast.LENGTH_SHORT).show();
                String mensagem = "Olá! Gostaria de mais informações sobre seu serviço anunciado no Constroo."; // Mensagem que você quer enviar
                String url = "https://api.whatsapp.com/send?phone=" + "55" + telefone + "&text=" + Uri.encode(mensagem);


                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
        });
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void buscarUsuarioPorUid(String uid) {
        String apiUrl = "https://cc-api-sql-qa.onrender.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<Usuario> call = usuarioApi.findByUid(uid);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    telefone = usuario.getTelefone();
                } else {
                    Toast.makeText(DetalheServicoActivity.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("DetalheServicoActivity", "Erro na chamada de API 1: " + t.getMessage());
                Toast.makeText(DetalheServicoActivity.this, "uid: " + uid, Toast.LENGTH_SHORT).show();
                Toast.makeText(DetalheServicoActivity.this, "Erro na chamada de API 1: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
