package com.example.construconecta_interdisciplinar_certo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SplashScreenConstroo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_constroo);


        ImageView imageView = findViewById(R.id.splash);

        Glide.with(this).asGif().load(R.drawable.splashscreenconstroo).into(imageView);

            // Após 8 segundos, vá para a próxima tela
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreenConstroo.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 8000);

    }
}