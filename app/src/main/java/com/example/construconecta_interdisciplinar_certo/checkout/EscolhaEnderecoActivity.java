package com.example.construconecta_interdisciplinar_certo.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.adapters.AdapterEndereco;
import com.example.construconecta_interdisciplinar_certo.adapters.OnEnderecoSelectedListener;
import com.example.construconecta_interdisciplinar_certo.apis.EnderecoApi;
import com.example.construconecta_interdisciplinar_certo.models.Endereco;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EscolhaEnderecoActivity extends AppCompatActivity implements OnEnderecoSelectedListener {

    private RecyclerView recyclerView;
    private Button btnAvancar;
    private ImageButton backButton;
    private AdapterEndereco adapterEndereco;
    private EnderecoApi enderecoApi;
    private TextView valor2, qtdEnderecos;
    private Double total;
    private String cupom;
    private List<Endereco> enderecos = new ArrayList<>();
    private ProgressBar progressBar; // Adicione a variável do ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco_de_entrega);

        btnAvancar = findViewById(R.id.btnAvancar);
        qtdEnderecos = findViewById(R.id.qtdEnderecos);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar4); // Inicializa o ProgressBar

        // Desabilita o botão no início
        btnAvancar.setEnabled(false);

        btnAvancar.setOnClickListener(v -> nextPage());
        backButton.setOnClickListener(v -> finish());

        valor2 = findViewById(R.id.valor2);
        total = getIntent().getExtras() != null ? getIntent().getExtras().getDouble("total") : null;
        cupom = getIntent().getExtras() != null ? getIntent().getExtras().getString("cupom") : null;
        if (total != null) {
            valor2.setText("R$ " + String.format("%.2f", total));
        }

        // Inicialize o RecyclerView e o adaptador
        recyclerView = findViewById(R.id.recyclerViewEnderecos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterEndereco = new AdapterEndereco(enderecos, this);
        recyclerView.setAdapter(adapterEndereco);

        // Inicializa o serviço Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        enderecoApi = retrofit.create(EnderecoApi.class);
        String uid = FirebaseAuth.getInstance().getUid();

        // Inicia a busca de endereços
        buscarEnderecos(uid);
    }

    private void buscarEnderecos(String usuarioId) {
        // Exibe o ProgressBar enquanto os dados estão sendo carregados
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE); // Esconde o RecyclerView enquanto carrega

        Call<List<Endereco>> call = enderecoApi.getEnderecosByUsuario(usuarioId);
        call.enqueue(new Callback<List<Endereco>>() {
            @Override
            public void onResponse(Call<List<Endereco>> call, Response<List<Endereco>> response) {
                progressBar.setVisibility(View.GONE); // Esconde o ProgressBar
                recyclerView.setVisibility(View.VISIBLE); // Mostra o RecyclerView

                if (response.isSuccessful() && response.body() != null) {
                    enderecos.clear();
                    enderecos.addAll(response.body());
                    adapterEndereco.notifyDataSetChanged();
                    qtdEnderecos.setText(adapterEndereco.getItemCount() + " Endereço(s)");
                } else {
                    Log.e("EnderecoDeEntrega", "Erro: " + response.code() + " - " + response.message());
                    Toast.makeText(EscolhaEnderecoActivity.this, "Erro ao buscar endereços: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Endereco>> call, Throwable t) {
                progressBar.setVisibility(View.GONE); // Esconde o ProgressBar em caso de falha
                recyclerView.setVisibility(View.GONE); // Esconde o RecyclerView em caso de falha
                Log.e("EnderecoDeEntrega", "Falha ao buscar endereços", t);
                Toast.makeText(EscolhaEnderecoActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEnderecoSelected(Endereco endereco) {
        btnAvancar.setEnabled(true);
        btnAvancar.setBackgroundResource(R.drawable.primary_button_design);
    }

    public void nextPage() {
        Bundle bundle = new Bundle();
        if (total != null) {
            bundle.putDouble("total", total);
            bundle.putString("cupom", cupom);
            Intent intent = new Intent(EscolhaEnderecoActivity.this, TipoDeEntregaActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}

