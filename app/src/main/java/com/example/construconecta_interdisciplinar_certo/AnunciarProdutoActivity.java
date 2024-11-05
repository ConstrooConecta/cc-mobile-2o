package com.example.construconecta_interdisciplinar_certo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.apis.CategoriaApi;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityAnunciarProdutoBinding;
import com.example.construconecta_interdisciplinar_certo.models.Categoria;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.onboarding.CameraActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnunciarProdutoActivity extends AppCompatActivity {
    private ImageView imagem;
    private String imageData;
    private FirebaseStorage storage;
    private Integer contador = 0;
    private ActivityAnunciarProdutoBinding binding;
    private Integer categoriaPosition;
    private List<Categoria> categorias = new ArrayList<>();
    private List<String> categoriasList = new ArrayList<>();
    private View viewCondicaoNovo, viewCondicaoUsado;
    private TextView novo, usado;
    private Button botaoANUNCIAR;
    private Boolean condicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnunciarProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa o FirebaseStorage
        storage = FirebaseStorage.getInstance();
        imagem = findViewById(R.id.imageView11Anuncio);

        // Carregar imagem padrão com Glide
        String url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8lRbS7eKYzDq-Ftxc1p8G_TTw2unWBMEYUw&s";
        Glide.with(this).load(url).into(imagem);

        // Ação de clique na imagem para abrir a câmera
        imagem.setOnClickListener(v -> {
            contador++;
            Intent intent = new Intent(AnunciarProdutoActivity.this, CameraActivity.class);
            intent.putExtra("produtoAnuncio", true);
            startActivity(intent);
        });

        // Obtenção do usuário Firebase atual
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Verificar se uma imagem foi salva no Firebase e carregá-la
        Intent intent1 = getIntent();
        boolean anuncioProduto = intent1.getBooleanExtra("salvouFirebase", false);
        if (anuncioProduto && user != null) {
            String caminhoImagem = "produtos/" + user.getEmail() + "_null.jpg";
            new Handler().postDelayed(() -> carregarImagemDoFirebase(caminhoImagem), 5500);
        }

        // Chamada para carregar as categorias da API
        chamarAPICategoria();

        // Mostrar dropdown ao clicar no campo de categoria
        binding.spnCategoriaInput.setOnClickListener(v -> binding.spnCategoriaInput.showDropDown());

        // Definir ação ao selecionar uma categoria
        binding.spnCategoriaInput.setOnItemClickListener((parent, view, position, id) -> {
            categoriaPosition = position + 1;
        });

        // Configurar seleção de condição (Novo/Usado)
        viewCondicaoNovo = findViewById(R.id.viewCondicaoNovo);
        viewCondicaoUsado = findViewById(R.id.viewCondicaoUsado);
        novo = findViewById(R.id.textView31Novo);
        usado = findViewById(R.id.textView32Usado);

        viewCondicaoNovo.setOnClickListener(v -> {
            viewCondicaoNovo.setTag("selecionado");
            viewCondicaoNovo.setBackgroundResource(R.drawable.condicao_selecionado_design);
            novo.setTextColor(getResources().getColor(R.color.white));
            condicao = true;

            viewCondicaoUsado.setTag(null);
            viewCondicaoUsado.setBackgroundResource(R.drawable.condicao_design);
            usado.setTextColor(Color.parseColor("#0A262C"));
        });

        viewCondicaoUsado.setOnClickListener(v -> {
            viewCondicaoUsado.setTag("selecionado");
            viewCondicaoUsado.setBackgroundResource(R.drawable.condicao_selecionado_design);
            usado.setTextColor(getResources().getColor(R.color.white));
            condicao = false;

            viewCondicaoNovo.setTag(null);
            viewCondicaoNovo.setBackgroundResource(R.drawable.condicao_design);
            novo.setTextColor(Color.parseColor("#0A262C"));
        });

        // Ação ao clicar no botão Anunciar
        botaoANUNCIAR = findViewById(R.id.botaoANUNCIAR);
        botaoANUNCIAR.setOnClickListener(v -> {
            if (user == null) {
                Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação do nome do produto
            String nomeProduto = binding.edtNomeProdutoInput.getText().toString();
            if (nomeProduto.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o nome do produto.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação do preço do produto
            String inputPreco = binding.edtPrecoInput.getText().toString();
            double valor;
            try {
                valor = Double.parseDouble(inputPreco);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor, insira um preço válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação da descrição do produto
            String descricaoProduto = binding.edtDescricaoInput.getText().toString();
            if (descricaoProduto.isEmpty()) {
                Toast.makeText(this, "Por favor, insira a descrição do produto.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação se uma categoria foi selecionada
            if (categoriaPosition == null) {
                Toast.makeText(this, "Por favor, selecione uma categoria.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação se a condição (novo ou usado) foi selecionada
            if (condicao == null) {
                Toast.makeText(this, "Por favor, selecione a condição do produto (Novo ou Usado).", Toast.LENGTH_SHORT).show();
                return;
            }

            // Se tudo estiver válido, prosseguir com o upload da imagem e criação do produto
            StorageReference oldImageRef = storage.getReference().child("produtos/" + user.getEmail() + "_null.jpg");

            File localFile;
            try {
                localFile = File.createTempFile("tempImage", "jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            oldImageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                StorageReference newImageRef = storage.getReference().child("produtos/" + user.getEmail() + "_" + nomeProduto + ".jpg");
                Uri fileUri = Uri.fromFile(localFile);

                newImageRef.putFile(fileUri).addOnSuccessListener(taskSnapshot1 -> {
                    newImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageData = uri.toString();
                        Toast.makeText(this, "URL: " + imageData, Toast.LENGTH_SHORT).show();

                        Produto produto = new Produto(
                                "22",
                                nomeProduto,
                                imageData,
                                valor,
                                0,
                                descricaoProduto,
                                1,
                                condicao,
                                user.getUid(),
                                (int) (Math.random() * 3 + 1),
                                List.of(categorias.get(categoriaPosition - 1))
                        );

                        chamarAPIAddProduto(produto);
                    });
                    oldImageRef.delete();
                });
            });
        });
    }

        private void chamarAPICategoria() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CategoriaApi categoriaApi = retrofit.create(CategoriaApi.class);
        Call<List<Categoria>> call = categoriaApi.getAllCategories();

        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful()) {
                    categorias = response.body();
                    if (categorias != null) {
                        for (Categoria categoria : categorias) {
                            categoriasList.add(categoria.getNome());
                        }
                        atualizarCategoriasDropdown();
                    }
                } else {
                    Toast.makeText(AnunciarProdutoActivity.this, "Erro ao carregar categorias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Toast.makeText(AnunciarProdutoActivity.this, "Erro na chamada de categorias: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarCategoriasDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item, R.id.dropdownText, categoriasList);
        binding.spnCategoriaInput.setAdapter(adapter);
    }

    private void chamarAPIAddProduto(Produto produto) {
        String url = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);
        Call<Produto> call = produtoApi.createProduct(produto);

        call.enqueue(new Callback<Produto>() {
            @Override
            public void onResponse(Call<Produto> call, Response<Produto> response) {
                if (response.isSuccessful()) {
                    Log.d("POST_SUCCESS", "Produto criado: " + response.body());
                } else {
                    Log.e("POST_FAILURE", "Erro ao criar produto: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Produto> call, Throwable t) {
                Log.e("POST_FAILURE", "Erro na chamada de API", t);
            }
        });
    }

    private void carregarImagemDoFirebase(String caminhoImagem) {
        StorageReference storageRef = storage.getReference().child(caminhoImagem);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this).load(uri).into(imagem));
    }
}
