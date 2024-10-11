package com.example.construconecta_interdisciplinar_certo.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;
import com.example.construconecta_interdisciplinar_certo.ui.MainActivity;

public class SplashScreenConstroo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_constroo);

            // Após 8 segundos, vá para a próxima tela
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreenConstroo.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }, 5000);

    }
}