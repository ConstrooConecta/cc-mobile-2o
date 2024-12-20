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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.adapters.AdapterCarrinho;
import com.example.construconecta_interdisciplinar_certo.apis.CarrinhoApi;
import com.example.construconecta_interdisciplinar_certo.apis.ProdutoApi;
import com.example.construconecta_interdisciplinar_certo.checkout.MetodosPagamento;
import com.example.construconecta_interdisciplinar_certo.models.Carrinho;
import com.example.construconecta_interdisciplinar_certo.models.Produto;
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
    private double TotalExibivel = 0.0;
    private FirebaseAuth mAuth;
    private String userId;
    private TextView quantidadeItens, subto, textViewTotal, textCarrinhoVazio, textView37;
    private ImageView imagem, imageViewCarrinhoVazio;
    private ImageButton imageButton3, imageButton3Frag;
    private Button button, buttonCarrinhoVazio;
    private Handler handler = new Handler();
    private Runnable checkCartRunnable;

    public CarrinhoFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrinho, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser != null ? currentUser.getUid() : null;

        initializeViews(view);
        initializeUI();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chamarAPIRetrofitProdutos();

        button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MetodosPagamento.class);
            intent.putExtra("total", TotalExibivel);
            startActivity(intent);
        });

        imageButton3.setOnClickListener(v -> deletarCarrinho());

        handler.post(checkCartRunnable = this::verificarCarrinhoPeriodicamente);

        return view;
    }

    private void initializeViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        subto = view.findViewById(R.id.textView51);
        button = view.findViewById(R.id.buttonEscolherPagamento);
        textViewTotal = view.findViewById(R.id.subTotal);
        imagem = view.findViewById(R.id.imageView7);
        recyclerView = view.findViewById(R.id.recyclerViewCarrinho);
        quantidadeItens = view.findViewById(R.id.quantidadeItens);
        imageViewCarrinhoVazio = view.findViewById(R.id.imagemCarrinhoVazio);
        buttonCarrinhoVazio = view.findViewById(R.id.btnAdicionarEndereco);
        textCarrinhoVazio = view.findViewById(R.id.textView7);
        textView37 = view.findViewById(R.id.textView37);
        imageButton3 = view.findViewById(R.id.imageButton3Frag);
        imageButton3Frag = view.findViewById(R.id.imageButton3Frag);
    }

    private void initializeUI() {
        progressBar.setVisibility(View.VISIBLE);
        subto.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        textViewTotal.setVisibility(View.INVISIBLE);
        imagem.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        imageViewCarrinhoVazio.setVisibility(View.GONE);
        textCarrinhoVazio.setVisibility(View.GONE);
        buttonCarrinhoVazio.setVisibility(View.GONE);
        textView37.setVisibility(View.GONE);
        imageButton3Frag.setVisibility(View.GONE);
    }

    private void chamarAPIRetrofitProdutos() {
        String API = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProdutoApi produtoApi = retrofit.create(ProdutoApi.class);
        Call<List<Produto>> call = produtoApi.findAllProducts();

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                if (response.isSuccessful()) {
                    produtos = response.body();
                    if (produtos != null) {
                        chamarAPIRetrofitCarrinho(userId);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void chamarAPIRetrofitCarrinho(String userId) {
        if (userId == null) {
            exibirCarrinhoVazio();
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
                        atualizarUIComCarrinho();
                    } else {
                        exibirCarrinhoVazio();
                    }
                } else {
                    exibirCarrinhoVazio();
                }
            }

            @Override
            public void onFailure(Call<List<Carrinho>> call, Throwable t) {
                exibirCarrinhoVazio();
            }
        });
    }

    private void atualizarUIComCarrinho() {
        adapter = new AdapterCarrinho(carrinhos, produtos, new AdapterCarrinho.UpdateTotalListener() {

            @Override
            public void onUpdateTotal(double newTotal) {
                TotalExibivel = newTotal;
                textViewTotal.setText("R$ " + String.format("%.2f", TotalExibivel));
                quantidadeItens.setText(adapter.getItemCount() + " Item(ns)");
            }

            @Override
            public void onCartEmpty() {
                // Define os elementos de carrinho vazio como visíveis
                imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
                textCarrinhoVazio.setVisibility(View.VISIBLE);
                buttonCarrinhoVazio.setVisibility(View.VISIBLE);

                // Oculta os elementos de carrinho com itens
                progressBar.setVisibility(View.GONE);
                subto.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                textViewTotal.setVisibility(View.GONE);
                imagem.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                textView37.setVisibility(View.GONE);
                quantidadeItens.setVisibility(View.GONE);
                imageButton3Frag.setVisibility(View.GONE);
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        calcularTotalExibivel(carrinhos);
        textViewTotal.setText("R$ " + String.format("%.2f", TotalExibivel));
        textViewTotal.setTypeface(null, Typeface.BOLD);
        quantidadeItens.setText(adapter.getItemCount() + " Item(ns)");

        progressBar.setVisibility(View.GONE);
        subto.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        textViewTotal.setVisibility(View.VISIBLE);
        imagem.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void calcularTotalExibivel(List<Carrinho> carrinhosResponse) {
        TotalExibivel = 0.0;
        for (Carrinho item : carrinhosResponse) {
            TotalExibivel += item.getValorTotal();
        }
    }

    private void exibirCarrinhoVazio() {
        progressBar.setVisibility(View.GONE);
        imageViewCarrinhoVazio.setVisibility(View.VISIBLE);
        textCarrinhoVazio.setVisibility(View.VISIBLE);
        buttonCarrinhoVazio.setVisibility(View.VISIBLE);
        buttonCarrinhoVazio.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment novoFragment = new HomeFragment();
            fragmentTransaction.replace(CarrinhoFragment.this.getId(), novoFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    private void deletarCarrinho() {
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
                    exibirCarrinhoVazio();
                } else {
                    Log.e("CarrinhoFragment", "Erro ao deletar carrinho: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("CarrinhoFragment", "Falha na requisição para deletar carrinho: " + t.getMessage());
            }
        });
    }

    private void verificarCarrinhoPeriodicamente() {
        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "Sem conexão de rede.", Toast.LENGTH_SHORT).show();
        }
        handler.postDelayed(checkCartRunnable, 60000);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(checkCartRunnable);
    }
}
