package com.example.construconecta_interdisciplinar_certo.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.construconecta_interdisciplinar_certo.R;

public class QrCodeActivity extends AppCompatActivity {
    Button botaoContinuarQrCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        botaoContinuarQrCode = findViewById(R.id.botaoContinuarQrCode);
        //abrir nova tela chamada qrCode
        botaoContinuarQrCode.setOnClickListener( v -> {
            startActivity(new Intent(QrCodeActivity.this, CarregamentoCompraActivity.class));

        });
    }
}