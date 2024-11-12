package com.example.construconecta_interdisciplinar_certo.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.construconecta_interdisciplinar_certo.R;

public class ButtonUtils {
    // Método para desabilitar o botão
    public static void disableButton(Context context, Button button, ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
        button.setEnabled(false);
        button.setBackgroundResource(R.drawable.disable_button_design);
        button.setText("Avançar");
    }

    // Método para habilitar o botão
    public static void enableButton(Context context, Button button, ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
        button.setEnabled(true);
        button.setBackgroundResource(R.drawable.primary_button_design);
        button.setText("Avançar");
    }

    // Método para o estado do botão quando a progressbar estiver ativa
    public static void enableButtonWithProgressBar(Context context, Button button, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        button.setBackgroundResource(R.drawable.primary_button_design);
        button.setText("");
    }

    public static void disableButtonWithProgressBar(Context context, Button button, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        button.setBackgroundResource(R.drawable.disable_button_design);
        button.setText("");
    }
}
