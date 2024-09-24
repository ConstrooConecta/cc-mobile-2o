package com.example.construconecta_interdisciplinar_certo.loginPath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.construconecta_interdisciplinar_certo.R;

public class TelaLogin4_email extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login4_email);

        // Após 3 segundos, vá para a próxima tela
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(TelaLogin4_email.this, TelaLogin5_Email.class);
            startActivity(intent);
            finish();
        }, 4000);
    }
}