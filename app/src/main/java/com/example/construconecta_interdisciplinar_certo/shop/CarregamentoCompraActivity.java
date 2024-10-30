package com.example.construconecta_interdisciplinar_certo.shop;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.construconecta_interdisciplinar_certo.R;

public class CarregamentoCompraActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewSacola;
    private int progresso = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carregamento_compra);

        progressBar = findViewById(R.id.progressBar);
        imageViewSacola = findViewById(R.id.imageViewSacola);

        // Simulação de carregamento gradual
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progresso < 100) {
                    progresso += 4;

                    // Atualiza a barra de progresso a cada 100ms
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progresso);

                            // Se o progresso atingir 100%, mudar a cor para verde
                            if (progresso == 100) {
                                imageViewSacola.setImageResource(R.drawable.compracarregada);
                                Drawable progressDrawable;
                                progressDrawable = progressBar.getProgressDrawable().mutate();
                                progressDrawable.setColorFilter(
                                        ContextCompat.getColor(CarregamentoCompraActivity.this, R.color.verde),
                                        android.graphics.PorterDuff.Mode.SRC_IN);
                                //abrindo a activity de pedidoConcluido
                                Intent intent = new Intent(CarregamentoCompraActivity.this, PedidoConcluidoActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

                    try {
                        Thread.sleep(100); // Atraso de 100ms entre cada incremento
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
