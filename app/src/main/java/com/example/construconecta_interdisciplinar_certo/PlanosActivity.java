package com.example.construconecta_interdisciplinar_certo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.construconecta_interdisciplinar_certo.databinding.ActivityPlanosBinding;

public class PlanosActivity extends AppCompatActivity {
    private ActivityPlanosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPlanosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //recuperar valor passado por intent, um boolean
        boolean PremiumComprado = getIntent().getBooleanExtra("premium", false);
        if (PremiumComprado) {
            binding.upgradePremium.setVisibility(View.GONE);
        }else {
            binding.obtido.setVisibility(View.GONE);
        }

        binding.upgradePremium.setOnClickListener(v -> {
            Intent intent = new Intent(this, TelaPagamentoPlanoActivity.class);
            startActivity(intent);
            finish();

        });
        binding.btnSaveSair.setOnClickListener(v -> finish());
    }
}