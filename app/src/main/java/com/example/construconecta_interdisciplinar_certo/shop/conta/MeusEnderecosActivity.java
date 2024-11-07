package com.example.construconecta_interdisciplinar_certo.shop.conta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.adapters.AdapterMeusEnderecos;
import com.example.construconecta_interdisciplinar_certo.apis.EnderecoApi;
import com.example.construconecta_interdisciplinar_certo.checkout.CadastroEnderecoActivity;
import com.example.construconecta_interdisciplinar_certo.models.Endereco;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeusEnderecosActivity extends AppCompatActivity implements AdapterMeusEnderecos.OnEnderecoChangeListener {

    private RecyclerView recyclerView;
    private TextView qtdEnderecos;
    private ProgressBar progressBar;
    private AdapterMeusEnderecos adapterEndereco;
    private List<Endereco> enderecos = new ArrayList<>();
    private EnderecoApi enderecoApi;
    private ImageButton backButton;
    private Button btnAdicionarEndereco;
    private ImageView arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_enderecos);

        recyclerView = findViewById(R.id.recyclerViewMeusEnderecos);
        qtdEnderecos = findViewById(R.id.qtdEnderecos);
        progressBar = findViewById(R.id.progressBar5);
        backButton = findViewById(R.id.backButton);
        btnAdicionarEndereco = findViewById(R.id.btnAdcEndereco);
        arrow = findViewById(R.id.arrow);

        backButton.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterEndereco = new AdapterMeusEnderecos(enderecos, this, this);
        recyclerView.setAdapter(adapterEndereco);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        enderecoApi = retrofit.create(EnderecoApi.class);
        String uid = FirebaseAuth.getInstance().getUid();

        buscarEnderecos(uid);
    }

    private void buscarEnderecos(String usuarioId) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        btnAdicionarEndereco.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);

        Call<List<Endereco>> call = enderecoApi.getEnderecosByUsuario(usuarioId);
        call.enqueue(new Callback<List<Endereco>>() {
            @Override
            public void onResponse(Call<List<Endereco>> call, Response<List<Endereco>> response) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                btnAdicionarEndereco.setVisibility(View.VISIBLE);
                arrow.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    enderecos.clear();
                    enderecos.addAll(response.body());
                    adapterEndereco.notifyDataSetChanged();
                    atualizarQuantidadeEnderecos();
                } else {
                    Log.e("MeusEnderecosActivity", "Erro: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Endereco>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                Log.e("MeusEnderecosActivity", "Falha ao buscar endereços", t);
                Toast.makeText(MeusEnderecosActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarQuantidadeEnderecos() {
        qtdEnderecos.setText(adapterEndereco.getItemCount() + " Endereço(s)");
    }

    @Override
    public void onEnderecoCountChanged(int count) {
        atualizarQuantidadeEnderecos();
    }

    public void addAddress(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("canal", "meusEnderecos");
        Intent intent = new Intent(MeusEnderecosActivity.this, CadastroEnderecoActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
