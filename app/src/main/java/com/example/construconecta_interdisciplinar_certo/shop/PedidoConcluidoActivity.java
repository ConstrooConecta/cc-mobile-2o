package com.example.construconecta_interdisciplinar_certo.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.construconecta_interdisciplinar_certo.R;

public class PedidoConcluidoActivity extends AppCompatActivity {
    private TextView textViewPedido;
    private Button botaoVoltarLoja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_concluido);
        textViewPedido = findViewById(R.id.textViewPedido);
        botaoVoltarLoja = findViewById(R.id.botaoVoltarLoja);
        botaoVoltarLoja.setOnClickListener(v -> {
            //abrir intent do fragmentHome
            Intent intent = new Intent(PedidoConcluidoActivity.this, Home.class);
            startActivity(intent);
        });
        int randomNumbers = (int) (Math.random() * 100000);
        textViewPedido.setText("Seu pedido #" + generateRandomLetters() + String.format("%05d", randomNumbers) + " foi feito com sucesso.");
    }

    String generateRandomLetters() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder letters = new StringBuilder(2);
        for (int i = 0; i < 2; i++) {
            int index = (int) (Math.random() * alphabet.length());
            letters.append(alphabet.charAt(index));
        }
        return letters.toString();
    }
}
