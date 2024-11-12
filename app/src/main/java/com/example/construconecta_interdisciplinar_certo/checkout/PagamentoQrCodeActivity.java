package com.example.construconecta_interdisciplinar_certo.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.shop.home.Home;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;

public class PagamentoQrCodeActivity extends BaseActivity {
    Button btnConfirmPayment, btnCancellPayment;
    ImageButton botaoVoltarQrCode;
    private Double total;
    private String cupom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento_qr_code);

        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnCancellPayment = findViewById(R.id.btnCancellPayment); // Inicialize o botão
        botaoVoltarQrCode = findViewById(R.id.botaoVoltarQrCode);

        total = getIntent().getExtras() != null ? getIntent().getExtras().getDouble("total") : null;
        cupom = getIntent().getExtras() != null ? getIntent().getExtras().getString("cupom") : null;

        if (total != null) {
            ((TextView) findViewById(R.id.valorTotalPagamento)).setText("R$ " + String.format("%.2f", total));
        }

        botaoVoltarQrCode.setOnClickListener(v -> finish());

        // Configurar o listener para o botão de confirmar pagamento
        btnConfirmPayment.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putDouble("total", total);
            bundle.putString("cupom", cupom);
            if (total != null) {
                Intent intent = new Intent(PagamentoQrCodeActivity.this, CarregamentoCompraActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finishAffinity();
            }
        });

        // Configurar o listener para o botão de cancelar pagamento
        btnCancellPayment.setOnClickListener(v -> {
            // Fechar todas as atividades e retornar à tela inicial
            Intent intent = new Intent(PagamentoQrCodeActivity.this, Home.class); // Substitua HomeActivity pela sua atividade inicial
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Adicione flags para limpar a pilha
            startActivity(intent);
            finishAffinity(); // Fecha todas as atividades
        });
    }
}

