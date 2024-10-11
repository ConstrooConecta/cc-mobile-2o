package com.example.construconecta_interdisciplinar_certo.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.construconecta_interdisciplinar_certo.R;
import com.google.android.material.textfield.TextInputLayout;

public class InputUtils {

    // Método para aplicar o estado de erro com mensagem personalizada
    public static void setError(Context context, TextInputLayout inputLayout, EditText inputField, TextView errorText, String errorMessage) {
        inputField.setError(errorMessage); // Define a mensagem de erro no campo de texto

        // Verifica se o errorText existe antes de tentar configurá-lo
        if (errorText != null) {
            errorText.setText(errorMessage);   // Define a mensagem de erro no TextView
            errorText.setVisibility(View.VISIBLE);
        }

        inputField.setEnabled(true);

        // Atualizando as cores para erro
        inputLayout.setBoxStrokeColor(context.getResources().getColor(R.color.red));
        inputLayout.setHintTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
        inputField.setTextColor(context.getResources().getColor(R.color.red));

        // Focar no campo para garantir que o erro seja exibido imediatamente
        inputField.requestFocus();
    }

    // Método para aplicar o estado normal
    public static void setNormal(Context context, TextInputLayout inputLayout, EditText inputField, TextView errorText) {
        // Verifica se o errorText existe antes de tentar escondê-lo
        if (errorText != null) {
            errorText.setVisibility(View.GONE);
        }

        inputLayout.setBoxStrokeColor(context.getResources().getColor(R.color.grey));
        inputLayout.setHintTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.grey)));
        inputField.setTextColor(context.getResources().getColor(R.color.black));
        inputField.setEnabled(true);
        inputField.setError(null, null); // Remove a mensagem de erro no campo de texto
    }


    // Método para desabilitar o input
    public static void disableInput(View view) {
        view.setEnabled(false);
    }

    // Método para habilitar o input
    public static void enableInput(View view) {
        view.setEnabled(true);
    }

}


