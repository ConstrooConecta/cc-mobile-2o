package com.example.construconecta_interdisciplinar_certo.utils;

import android.animation.ObjectAnimator;
import android.view.View;

public class AnimationUtils {
    // Método de animação "shake" (tremer)
    public static void shakeAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500); // Duração da animação
        animator.start(); // Inicia a animação
    }
}
