package com.example.construconecta_interdisciplinar_certo.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.models.Endereco;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.repositories.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AdapterEndereco extends RecyclerView.Adapter<AdapterEndereco.EnderecoViewHolder> {

    private final List<Endereco> enderecos;
    private final OnEnderecoSelectedListener listener;
    private int selectedPosition = -1; // Posição do endereço selecionado

    public AdapterEndereco(List<Endereco> enderecos, OnEnderecoSelectedListener listener) {
        this.enderecos = enderecos;
        this.listener = listener; // Inicializa o listener
    }

    @NonNull
    @Override
    public EnderecoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_endereco, parent, false);
        return new EnderecoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnderecoViewHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UsuarioRepository.getInstance().loadData(user.getEmail(), usuarios -> {
                if (!usuarios.isEmpty()) {
                    Usuario usuario = usuarios.get(0);
                    Endereco endereco = enderecos.get(position);
                    holder.addressNameAndNumberTxt.setText(endereco.getRua() + " - " + endereco.getNumero());
                    holder.nameAndNumberPhoneTxt.setText(usuario.getNomeCompleto() + " - " + usuario.getTelefone());
                    holder.cepTxt.setText("CEP - " + endereco.getCep());

                    // Configura o estado do RadioButton
                    holder.radioButton3.setChecked(selectedPosition == position);
                    holder.radioButton3.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#867151")));

                    holder.radioButton3.setOnClickListener(v -> {
                        selectedPosition = holder.getAdapterPosition(); // Atualiza a posição selecionada
                        listener.onEnderecoSelected(endereco); // Notifica a Activity sobre a seleção
                        notifyDataSetChanged(); // Atualiza a lista para refletir a seleção
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return enderecos.size();
    }

    static class EnderecoViewHolder extends RecyclerView.ViewHolder {
        TextView addressNameAndNumberTxt;
        TextView nameAndNumberPhoneTxt;
        TextView cepTxt;
        RadioButton radioButton3;

        EnderecoViewHolder(View itemView) {
            super(itemView);
            addressNameAndNumberTxt = itemView.findViewById(R.id.addressNameAndNumberTxt);
            nameAndNumberPhoneTxt = itemView.findViewById(R.id.nameAndNumberPhoneTxt);
            cepTxt = itemView.findViewById(R.id.cepTxt);
            radioButton3 = itemView.findViewById(R.id.radioButtonEndereco);
        }
    }
}
