package com.example.construconecta_interdisciplinar_certo.checkout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.databinding.ActivityEntregaBinding;
import com.example.construconecta_interdisciplinar_certo.ui.BaseActivity;

public class TipoDeEntregaActivity extends BaseActivity {
    private ActivityEntregaBinding binding;
    private Double total;
    private String cupom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntregaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.radioButton.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#867151")));
        binding.buttonContinue.setEnabled(false);
        binding.buttonContinue.setBackgroundResource(R.drawable.disable_button_design);

        binding.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.buttonContinue.setEnabled(isChecked); // Habilita o botão se o RadioButton estiver selecionado
            binding.buttonContinue.setBackgroundResource(R.drawable.primary_button_design);
            binding.buttonContinue.setOnClickListener(v -> nextPage());
        });

        binding.backButton.setOnClickListener(v -> finish());

        total = getIntent().getExtras() != null ? getIntent().getExtras().getDouble("total") : null;
        cupom = getIntent().getExtras() != null ? getIntent().getExtras().getString("cupom") : null;
        if (total != null) {
            binding.subTotal.setText("R$ " + String.format("%.2f", total));
        }
    }

    public void nextPage () {
        Bundle bundle = new Bundle();
        if (total != null) {
            bundle.putDouble("total", total);
            bundle.putString("cupom", cupom);
            Intent intent = new Intent(TipoDeEntregaActivity.this, PagamentoQrCodeActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
