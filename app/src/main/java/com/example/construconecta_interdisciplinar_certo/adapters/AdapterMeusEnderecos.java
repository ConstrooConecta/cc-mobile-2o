package com.example.construconecta_interdisciplinar_certo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.EnderecoApi;
import com.example.construconecta_interdisciplinar_certo.models.Endereco;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.example.construconecta_interdisciplinar_certo.repositories.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterMeusEnderecos extends RecyclerView.Adapter<AdapterMeusEnderecos.ViewHolder> {

    private List<Endereco> enderecos;
    private Context context;
    private EnderecoApi enderecoApi;
    private OnEnderecoChangeListener enderecoChangeListener;

    public AdapterMeusEnderecos(List<Endereco> enderecos, Context context, OnEnderecoChangeListener listener) {
        this.enderecos = enderecos;
        this.context = context;
        this.enderecoChangeListener = listener;

        // Configuração do Retrofit para chamadas de API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cc-api-sql-qa.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        enderecoApi = retrofit.create(EnderecoApi.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meus_enderecos_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UsuarioRepository.getInstance().loadData(user.getEmail(), usuarios -> {
                if (!usuarios.isEmpty()) {
                    Usuario usuario = usuarios.get(0);
                    Endereco endereco = enderecos.get(position);

                    // Configura os textos dos TextViews
                    holder.addressNameAndNumberTxt.setText(endereco.getRua() + " - " + endereco.getNumero());
                    holder.nameAndNumberPhoneTxt.setText(usuario.getNomeCompleto() + " - " + usuario.getTelefone());
                    holder.cepTxt.setText("CEP - " + endereco.getCep());

                    // Ação para o botão de excluir
                    holder.btnExcluir.setOnClickListener(v -> {
                        holder.btnExcluir.setEnabled(false);  // Desabilita temporariamente o botão

                        new AlertDialog.Builder(context)
                                .setTitle("Confirmação de exclusão")
                                .setMessage("Tem certeza de que deseja excluir este endereço?")
                                .setPositiveButton("Sim", (dialog, which) -> {
                                    excluirEndereco(endereco.getEnderecoUsuarioId(), position, holder);
                                })
                                .setNegativeButton("Não", (dialog, which) -> holder.btnExcluir.setEnabled(true))
                                .setOnCancelListener(dialog -> holder.btnExcluir.setEnabled(true))
                                .show();
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return enderecos.size();
    }

    private void excluirEndereco(Integer enderecoId, int position, ViewHolder holder) {
        Call<Void> call = enderecoApi.deleteAddress(enderecoId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    enderecos.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, enderecos.size());
                    Toast.makeText(context, "Endereço excluído", Toast.LENGTH_SHORT).show();

                    if (enderecoChangeListener != null) {
                        enderecoChangeListener.onEnderecoCountChanged(enderecos.size());
                    }
                } else {
                    Toast.makeText(context, "Erro ao excluir o endereço", Toast.LENGTH_SHORT).show();
                    holder.btnExcluir.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Falha na exclusão do endereço", Toast.LENGTH_SHORT).show();
                holder.btnExcluir.setEnabled(true);
            }
        });
    }

    public interface OnEnderecoChangeListener {
        void onEnderecoCountChanged(int count);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView addressNameAndNumberTxt, nameAndNumberPhoneTxt, cepTxt;
        ImageButton btnExcluir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressNameAndNumberTxt = itemView.findViewById(R.id.addressNameAndNumberTxt);
            nameAndNumberPhoneTxt = itemView.findViewById(R.id.nameAndNumberPhoneTxt);
            cepTxt = itemView.findViewById(R.id.cepTxt);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
