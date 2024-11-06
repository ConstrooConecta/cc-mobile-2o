package com.example.construconecta_interdisciplinar_certo.shop.conta;

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

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.CategoriaApi;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityAnunciarProdutoBinding;
import com.example.construconecta_interdisciplinar_certo.models.Categoria;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.onboarding.CameraActivity;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
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

public class AnunciarProdutoActivity extends BaseActivity {
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
        setContentView(R.layout.activity_anunciar_produto);
        binding = ActivityAnunciarProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Inicializa o FirebaseStorage


        imagem = findViewById(R.id.imageView11Anuncio);
        String url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8lRbS7eKYzDq-Ftxc1p8G_TTw2unWBMEYUw&s";
        Glide.with(this).load(url).into(imagem);

        imagem.setOnClickListener(v -> {
            contador++;
            //passando um boolean pra proxima tela
            //para saber se o botão foi clicado
            //e mostrar a camera
            Intent intent = new Intent(AnunciarProdutoActivity.this, CameraActivity.class);
            intent.putExtra("produtoAnuncio", true);
            startActivity(intent);
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        //pegar o intent da outra tela, era um boolean
        Intent intent1 = getIntent();
        boolean anuncioProduto = intent1.getBooleanExtra("salvouFirebase", false);
        if (anuncioProduto) {
            Toast.makeText(this, "Vamos ver se funcionaaaaaaaaaaaa", Toast.LENGTH_LONG).show();
            //pegando a imgem do firebase storage
            String caminhoImagem = "produtos/" + user.getEmail() + "_" + "null" + ".jpg";
            //espera 3 segundos pra imagem carregar/chamar a função
            new Handler().postDelayed(() -> carregarImagemDoFirebase(caminhoImagem), 5500);
        }

        // Chamada para carregar as categorias da API
        chamarAPICategoria();

        // Ao clicar no input, mostrar o dropdown
        binding.categoriaInput.setOnClickListener(v -> binding.categoriaInput.showDropDown());

        // Ação ao selecionar uma categoria
        binding.categoriaInput.setOnItemClickListener((parent, view, position, id) -> {
            categoriaPosition = position + 1;
        });

        viewCondicaoNovo = findViewById(R.id.viewCondicaoNovo);
        viewCondicaoUsado = findViewById(R.id.viewCondicaoUsado);

        novo = findViewById(R.id.textView31Novo);
        usado = findViewById(R.id.textView32Usado);

        viewCondicaoNovo.setOnClickListener(v -> {
            //adicionar tag "selecionado" no viewCondicaoNovo
            viewCondicaoNovo.setTag("selecionado");
            viewCondicaoNovo.setBackgroundResource(R.drawable.condicao_selecionado_design);
            novo.setTextColor(getResources().getColor(R.color.white));
            condicao = true;

            viewCondicaoUsado.setTag(null);
            viewCondicaoUsado.setBackgroundResource(R.drawable.condicao_design);
            //colocando o textView "usado" com a cor com rgb="#791410"
            usado.setTextColor(Color.parseColor("#0A262C"));

        });

        viewCondicaoUsado.setOnClickListener(v -> {
            //adicionar tag "selecionado" no viewCondicaoUsado
            viewCondicaoUsado.setTag("selecionado");
            //colocar o background como o drawable/shape "condicao_selecionado_Desgign"
            viewCondicaoUsado.setBackgroundResource(R.drawable.condicao_selecionado_design);
            usado.setTextColor(getResources().getColor(R.color.white));
            condicao = false;

            viewCondicaoNovo.setTag(null);
            viewCondicaoNovo.setBackgroundResource(R.drawable.condicao_design);
            novo.setTextColor(Color.parseColor("#0A262C"));

        });

        botaoANUNCIAR = findViewById(R.id.botaoANUNCIAR);
        //pegando uid do usuario logado no firebase
        String uid = FirebaseAuth.getInstance().getUid();

        botaoANUNCIAR.setOnClickListener(v -> {
            String input = binding.editTextText6.getText().toString();
            double valor = Double.parseDouble(input);
            List<Categoria> list = new ArrayList<>();
            list.add(categorias.get(categoriaPosition - 1));
            Toast.makeText(this, "Lista" + list, Toast.LENGTH_SHORT).show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference oldImageRef = storage.getReference().child("produtos/" + user.getEmail() + "_" + "null" + ".jpg");

            File localFile;
            try {
                localFile = File.createTempFile("tempImage", "jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File finalLocalFile = localFile;
            oldImageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // O arquivo foi baixado com sucesso, agora fazemos o upload com o novo nome
                StorageReference newImageRef = storage.getReference().child("produtos/" + user.getEmail() + "_" + binding.editTextText4.getText().toString() + ".jpg");
                Uri fileUri = Uri.fromFile(finalLocalFile);

                newImageRef.putFile(fileUri).addOnSuccessListener(taskSnapshot1 -> {
                    newImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Aqui você tem a URL da imagem
                        imageData = uri.toString();
                        Toast.makeText(this, "URL: " + imageData, Toast.LENGTH_SHORT).show();
                    });
                    oldImageRef.delete().addOnSuccessListener(aVoid -> {

                    }).addOnFailureListener(exception -> {

                    });
                }).addOnFailureListener(exception -> {
                });
            }).addOnFailureListener(exception -> {
            });

            Produto produto = new Produto(
                    "22",
                    binding.editTextText4.getText().toString(),
                    imageData,
                    //fazer o editTextText6 virar "double"
                    valor,
                    0.0,
                    binding.editTextText3.getText().toString(),
                    1,
                    condicao,
                    uid,
                    //numero aleatorio entre 1 e 3
                    (int) (Math.random() * 3 + 1),
                    list
            );
            chamarAPIAddProduto(produto);
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
                        // Adiciona os nomes das categorias na lista 'categoriasList'
                        for (Categoria categoria : categorias) {
                            categoriasList.add(categoria.getNome());
                        }
                        // Atualiza o ArrayAdapter com os novos nomes das categorias
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item,
                R.id.dropdownText,
                categoriasList // A lista de categorias preenchida pela API
        );
        binding.categoriaInput.setAdapter(adapter);
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
                    Produto createdProduct = response.body();
                    Log.d("POST_SUCCESS", "Produto criado: " + createdProduct.getNomeProduto());
                } else {
                    // Tratar erro ao adicionar no banco e obter detalhes do erro
                    try {
                        String errorBody = response.errorBody().string(); // pegar o corpo do erro como string
                        Log.e("POST_ERROR", "Erro ao salvar no banco de dados: " + errorBody);
                        Toast.makeText(AnunciarProdutoActivity.this, "Erro ao salvar no banco de dados: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e("POST_ERROR", "Erro ao processar o corpo do erro.", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Produto> call, Throwable t) {
                Toast.makeText(AnunciarProdutoActivity.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarImagemDoFirebase(String caminhoImagem) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference().child(caminhoImagem);

        // Trata falhas ao obter a URL
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Usa o Glide para carregar a imagem no ImageView
            Glide.with(this)
                    .load(uri)
                    .into(imagem);
        }).addOnFailureListener(Throwable::printStackTrace);
    }
}
