package com.example.construconecta_interdisciplinar_certo.checkout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.shop.QrCodeActivity;

public class MetodosPagamento extends AppCompatActivity {
    private TextView valor2;
    private RadioButton radioButton2;
    private Button botaoContinuar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_pagamento);
        valor2 = findViewById(R.id.valor2);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton2.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#867151")));

        //pegando bundle da outra tela
        Intent intent = getIntent();
        double valorRecebido = intent.getDoubleExtra("total", 0.0);

        valor2.setText("R$ " + String.format("%.2f", valorRecebido));
        botaoContinuar = findViewById(R.id.botaoContinuar);
        botaoContinuar.setOnClickListener(v -> {
            startActivity(new Intent(MetodosPagamento.this, QrCodeActivity.class));
        });
    }
}
