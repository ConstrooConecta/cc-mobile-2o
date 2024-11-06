package com.example.construconecta_interdisciplinar_certo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.Adapters.AdapterCarrinho;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.CarrinhoApi;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.checkout.CarrinhoActivity;
import com.example.construconecta_interdisciplinar_certo.checkout.MetodosPagamento;
import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
import com.example.construconecta_interdisciplinar_certo.shop.Home;
import com.example.construconecta_interdisciplinar_certo.ui.InternetErrorActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarrinhoFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterCarrinho adapter;
    private List<Carrinho> carrinhos = new ArrayList<>();
    private List<Produto> produtos = new ArrayList<>();
    private ProgressBar progressBar;
    private double TotalExibivel = 0.0; // Adiciona a variável para o total
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Runnable verificaCarrinhoRunnable;
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private String userId = currentUser.getUid();
    private TextView quantidadeItens, subto, textViewTotal, textCarrinhoVazio,textView37;
    private ImageView imagem, imageViewCarrinhoVazio;
    private ImageButton imageButton3;


    private Button button, buttonCarrinhoVazio;

    public CarrinhoFragment() {
    }
    private Handler handler = new Handler();
    private Runnable checkCartRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrinho, container, false);
        imageButton3 = view.findViewById(R.id.imageButton3Frag);
        textView37 = view.findViewById(R.id.textView37);

        // Inicializa os componentes da interface
        progressBar = view.findViewById(R.id.progressBar);
        subto = view.findViewById(R.id.textView51);
        button = view.findViewById(R.id.button);
        textViewTotal = view.findViewById(R.id.textView52SubtotalV);
        imagem = view.findViewById(R.id.imageView7);
        recyclerView = view.findViewById(R.id.recyclerViewCarrinho);
        quantidadeItens = view.findViewById(R.id.quantidadeItens);
        imageViewCarrinhoVazio = view.findViewById(R.id.imagemCarrinhoVazio);
        buttonCarrinhoVazio = view.findViewById(R.id.buttonCarrinhoVazio);
        textCarrinhoVazio = view.findViewById(R.id.textView7);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicialmente esconde todos os elementos exceto a ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        subto.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        textViewTotal.setVisibility(View.INVISIBLE);
        imagem.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        imageViewCarrinhoVazio.setVisibility(View.GONE);
        textCarrinhoVazio.setVisibility(View.GONE);
        buttonCarrinhoVazio.setVisibility(View.GONE);

        chamarAPIRetrofitProdutos();

        button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MetodosPagamento.class);
            intent.putExtra("total", TotalExibivel);
            startActivity(intent);
        });

        // Inicializa o Handler e Runnable para verificação periódica do carrinho
        handler = new Handler();
        checkCartRunnable = () -> {
            Log.d("CarrinhoFragment", "Verificando conteúdo do carrinho...");
            chamarAPIRetrofitCarrinho(userId);
            handler.postDelayed(checkCartRunnable, 5000); // Verifica a cada 5 segundos
        };

        imageButton3.setOnClickListener(v -> {
            String API = "https://cc-api-sql-qa.onrender.com/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CarrinhoApi carrinhoApi = retrofit.create(CarrinhoApi.class);
            Call<ResponseBody> call = carrinhoApi.deleteAll_carrinho(userId);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        quantidadeItens.setText(0 + " itens");
                        textView37.setVisibility(View.GONE);
                        imageButton3.setVisibility(View.GONE);
                        imagem.setVisibility(View.GONE);
                        subto.setVisibility(View.GONE);
                        textViewTotal.setVisibility(View.GONE);
                        button.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
                        textCarrinhoVazio.setVisibility(View.VISIBLE);
                        buttonCarrinhoVazio.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    } else {
                        Log.e("CarrinhoActivity", "Erro ao deletar carrinho: " + response.code());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("CarrinhoActivity", "Erro ao deletar carrinho: " + t.getMessage());
                }
            });
        });
        textView37.setOnClickListener(v -> {
            String API = "https://cc-api-sql-qa.onrender.com/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CarrinhoApi carrinhoApi = retrofit.create(CarrinhoApi.class);
            Call<ResponseBody> call = carrinhoApi.deleteAll_carrinho(userId);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        quantidadeItens.setText(0 + " itens");
                        textView37.setVisibility(View.GONE);
                        imageButton3.setVisibility(View.GONE);
                        imagem.setVisibility(View.GONE);
                        subto.setVisibility(View.GONE);
                        textViewTotal.setVisibility(View.GONE);
                        button.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
                        textCarrinhoVazio.setVisibility(View.VISIBLE);
                        buttonCarrinhoVazio.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    } else {
                        Log.e("CarrinhoActivity", "Erro ao deletar carrinho: " + response.code());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("CarrinhoActivity", "Erro ao deletar carrinho: " + t.getMessage());
                }
            });
        });
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (userId != null) {
            handler.post(checkCartRunnable); // Inicia o Runnable
        } else {
            Log.e("CarrinhoFragment", "User ID está nulo, não foi possível iniciar o Runnable.");
        }
    }

    // Função para verificar se o carrinho está vazio e atualizar a interface


    private void chamarAPIRetrofitProdutos() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);
        Call<List<Produto>> call = produtoApi.findUser();

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                if (response.isSuccessful()) {
                    produtos = response.body();
                    if (produtos != null) {
                        // Após carregar os produtos, chama a função para carregar o carrinho
                        chamarAPIRetrofitCarrinho(userId);
                    }
                } else {
                    Toast.makeText(getContext(), "Erro ao carregar produtos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro na chamada de produtos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chamarAPIRetrofitCarrinho(String userId) {
        if (userId == null) {
            Log.e("CarrinhoFragment", "User ID está nulo ao chamar a API do carrinho.");
            return;
        }
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CarrinhoApi carrinhoApi = retrofit.create(CarrinhoApi.class);
        Call<List<Carrinho>> call = carrinhoApi.findByUserId(userId);

        call.enqueue(new Callback<List<Carrinho>>() {
            @Override
            public void onResponse(Call<List<Carrinho>> call, Response<List<Carrinho>> response) {
                if (response.isSuccessful()) {
                    List<Carrinho> carrinhosResponse = response.body();
                    if (carrinhosResponse != null && !carrinhosResponse.isEmpty()) {
                        carrinhos.clear();
                        carrinhos.addAll(carrinhosResponse);

                        // Configura o Adapter e notifica as mudanças
                        adapter = new AdapterCarrinho(carrinhos, produtos);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        // Calcula o total exibível
                        calcularTotalExibivel(carrinhosResponse);
                        //deixar exbindo apenas 2 casas decimais
                        textViewTotal.setText("Total: R$ " + String.format("%.2f", TotalExibivel));
                        textViewTotal.setTypeface(null, Typeface.BOLD);
                        //passando o textViewTotal num bundle pra proxima tela:


                        quantidadeItens.setText(adapter.getItemCount() + " itens");

                        // Agora que os dados foram carregados, podemos exibir os elementos
                        progressBar.setVisibility(View.GONE);
                        subto.setVisibility(View.VISIBLE);
                        button.setVisibility(View.VISIBLE);
                        textViewTotal.setVisibility(View.VISIBLE);
                        imagem.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE); // Exibe o RecyclerView agora que os dados estão carregados
                    } else {
                        progressBar.setVisibility(View.GONE);
                        subto.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.INVISIBLE);
                        textViewTotal.setVisibility(View.INVISIBLE);
                        imagem.setVisibility(View.INVISIBLE);
                        quantidadeItens.setText(0 + " itens");
                        Log.d("CarrinhoFragment", "Carrinho está vazio.");
                        progressBar.setVisibility(View.GONE);
                        imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
                        textCarrinhoVazio.setVisibility(View.VISIBLE);
                        textView37.setVisibility(View.GONE);
                        imageButton3.setVisibility(View.GONE);
                        buttonCarrinhoVazio.setVisibility(View.VISIBLE);
                        buttonCarrinhoVazio.setOnClickListener(v -> {
                            Intent intent = new Intent(getContext(), HomeFragment.class);
                            startActivity(intent);
                        });
                    }
                } else {
                    imageButton3.setVisibility(View.GONE);
                    textView37.setVisibility(View.GONE);
                    quantidadeItens.setText(0 + " itens");
                    progressBar.setVisibility(View.GONE);
                    subto.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    textViewTotal.setVisibility(View.INVISIBLE);
                    imagem.setVisibility(View.INVISIBLE);
                    imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
                    textCarrinhoVazio.setVisibility(View.VISIBLE);
                    buttonCarrinhoVazio.setVisibility(View.VISIBLE);
                    buttonCarrinhoVazio.setOnClickListener(v -> {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        // Substitua 'NovoFragment' pelo fragment que deseja abrir
                        Fragment novoFragment = new HomeFragment();

                        // Substitui o fragment atual pelo novo
                        //pegar meu fragment atual
                        fragmentTransaction.replace(CarrinhoFragment.this.getId(), novoFragment);
                        fragmentTransaction.addToBackStack(null); // Adiciona à pilha para navegação reversa
                        //deixar o icone de home como selecionado
                        Home.bottomNavigationView.setSelectedItemId(R.id.home1);
                        fragmentTransaction.commit();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Carrinho>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro na chamada de carrinho: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calcularTotalExibivel(List<Carrinho> carrinhosResponse) {
        TotalExibivel = 0.0; // Zera o total antes de calcular
        for (Carrinho item : carrinhosResponse) {
            TotalExibivel += item.getValorTotal(); // Some o preço de cada item
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(checkCartRunnable); // Pausa o Runnable quando o fragmento não está visível
        Log.d("CarrinhoFragment", "Runnable pausado.");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isConnectedToInternet()) {
            Intent intent = new Intent(getActivity(), InternetErrorActivity.class);
            startActivity(intent);
        } else {
            handler.post(checkCartRunnable); // Reinicia o Runnable ao retomar o fragmento
            Log.d("CarrinhoFragment", "Runnable retomado.");
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(checkCartRunnable); // Remove callbacks ao destruir o fragmento
        Log.d("CarrinhoFragment", "Runnable removido ao destruir o fragmento.");

    }
}
