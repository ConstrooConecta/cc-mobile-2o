package com.example.construconecta_interdisciplinar_certo.shop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.TelaPagamentoPlanoActivity;
import com.example.construconecta_interdisciplinar_certo.apis.PagamentoPlanoApi;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityHomeBinding;
import com.example.construconecta_interdisciplinar_certo.fragments.CarrinhoFragment;
import com.example.construconecta_interdisciplinar_certo.fragments.CategoriasFragment;
import com.example.construconecta_interdisciplinar_certo.fragments.ContaFragment;
import com.example.construconecta_interdisciplinar_certo.fragments.ContratarFragment;
import com.example.construconecta_interdisciplinar_certo.fragments.HomeFragment;
import com.example.construconecta_interdisciplinar_certo.models.PagamentoPlano;
import com.example.construconecta_interdisciplinar_certo.onboarding.CameraActivity;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends BaseActivity {
    public static NavigationBarView bottomNavigationView;
    ActivityHomeBinding binding;
    int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recuperando o Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int castroConcluido = extras.getInt("castroConcluido", 0); // 0 é o valor padrão

            if (castroConcluido == 1) {
                LocalDate dataAtual = LocalDate.now();
                String dataPagamentoFormatada = dataAtual.toString();
                addPaymentPlan(new PagamentoPlano(1,
                        2,
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                        0,
                        "Gratuito",
                        dataPagamentoFormatada)
                );

                mostrarBoasVindas();
            }
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Configurando a bottom navBar
        binding.bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    //substituindo os fragments
                    int id = item.getItemId();
                    if (id == R.id.home1) {
                        // item.setIcon(R.drawable.chat_selected_icon);
                        replaceFragment(new HomeFragment());

                    } else if (id == R.id.categorias) {
                        //  item.setIcon(R.drawable.person_selected_icon);
                        replaceFragment(new CategoriasFragment());
                    } else if (id == R.id.contratar) {
                        // item.setIcon(R.drawable.home_selected_icon);
                        replaceFragment(new ContratarFragment());

                    } else if (id == R.id.carrinho) {
                        //  item.setIcon(R.drawable.person_selected_icon);
                        replaceFragment(new CarrinhoFragment());
                    } else if (id == R.id.conta) {
                        //item.setIcon(R.drawable.home_selected_icon);
                        replaceFragment(new ContaFragment());

                    }
                    return true;
                }
        );
        binding.bottomNavigationView.setSelectedItemId(R.id.home1);
    }

    //método para mudar o fragmento na navbar
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Verifica se o botão pressionado é o botão de "voltar"
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cont++;
            if (cont == 1) {
                // Mostra o Toast pedindo para o usuário clicar novamente para sair
                Toast.makeText(this, "Aperte novamente para sair", Toast.LENGTH_SHORT).show();

                // Reseta o contador após 2 segundos
                new Handler().postDelayed(() -> cont = 0, 2000);
                return true;
            } else if (cont == 2) {
                // Se o usuário apertou novamente dentro do tempo, encerra a Activity
                return super.onKeyDown(keyCode, event);
            }
        }
        return true;
    }


    private void addPaymentPlan(PagamentoPlano pagamentoPlano) {
        String url = "https://cc-api-sql-qa.onrender.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PagamentoPlanoApi api = retrofit.create(PagamentoPlanoApi.class);
        Call<PagamentoPlano> call = api.createPaymentPlan(pagamentoPlano);

        call.enqueue(new Callback<PagamentoPlano>() {
            @Override
            public void onResponse(Call<PagamentoPlano> call, Response<PagamentoPlano> response) {
                if (response.isSuccessful()) {
                    PagamentoPlano createdPlan = response.body();
                    Log.d("POST_SUCCESS", "Carrinho criado: " + createdPlan.getPagamentoPlanoId());

                } else {
                    // Tratar erro ao adicionar no banco
                    Log.d("REQUEST_BODY", pagamentoPlano.toString()); // Certifique-se de que o método toString() do seu objeto Carrinho exiba os dados corretamente
                    Log.e("POST_ERROR", "Erro ao salvar carrinho. Código: " + response.code());
                    Toast.makeText(Home.this, "Erro ao salvar Plano no banco de dados. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagamentoPlano> call, Throwable t) {
                Toast.makeText(Home.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void mostrarBoasVindas() {
        if (!isFinishing()) {
        // Criando o AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Boas-vindas!")
                .setMessage("Cadastro concluído com sucesso! Vamos tirar sua foto de perfil.")
                .setPositiveButton("Vamos lá![", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Ação ao aceitar - Redirecionar para a activity de tirar foto
                        Intent intent = new Intent(Home.this, CameraActivity.class);
                        startActivity(intent);
                    }
                })

                .setCancelable(false) // Impede que o modal seja fechado clicando fora
                .show();
    }
    }
}
