package com.example.construconecta_interdisciplinar_certo.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.shop.contratar.DetalheServicoActivity;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.apis.UsuarioApi;
import com.example.construconecta_interdisciplinar_certo.models.Servico;
import com.example.construconecta_interdisciplinar_certo.models.Usuario;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterServico extends RecyclerView.Adapter<AdapterServico.ServicoViewHolder> {
    private List<Servico> servicoList;
    private Context context;

    public AdapterServico(List<Servico> servicoList, Context context) {
        this.servicoList = servicoList;
        this.context = context;
    }

    public void setFilteredList(List<Servico> Listaservicos) {
        this.servicoList = Listaservicos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServicoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_servico, parent, false);
        return new ServicoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicoViewHolder holder, int position) {
        Servico servico = servicoList.get(position);
        holder.textViewNomeServico.setText(servico.getNomeServico());
        holder.textViewCodigoServico.setText("Código " + servico.getServicoId());
        holder.textViewPrecoServico.setText("R$ " + servico.getPreco());

        servico.getUsuario();

        final String[] imageUrl = {null};

        buscarUsuarioPorUid(servico.getUsuario(), holder, url -> {
            imageUrl[0] = url;
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetalheServicoActivity.class);
                // Passando nome, descricao, preco e URL da imagem:
                intent.putExtra("nomeServico", servico.getNomeServico());
                intent.putExtra("descricao", servico.getDescricao());
                intent.putExtra("preco", servico.getPreco());
                intent.putExtra("imageUrl", imageUrl[0]); // Passa a URL da imagem para a próxima tela
                context.startActivity(intent);
            }
        });
    }

    // Modifique o método buscarUsuarioPorUid para usar uma interface de callback para retornar a URL da imagem
    private void buscarUsuarioPorUid(String uid, ServicoViewHolder holder, OnImageUrlFetchedListener listener) {
        String apiUrl = "https://cc-api-sql-qa.onrender.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsuarioApi usuarioApi = retrofit.create(UsuarioApi.class);
        Call<Usuario> call = usuarioApi.findByUid(uid);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    String email = usuario.getEmail();
                    buscarImagemNoFirebase(email, holder, listener); // Passa o listener para capturar a URL
                } else {
                    Toast.makeText(context, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                    holder.imageViewServico.setImageResource(R.drawable.imagemanuncio);
                    listener.onUrlFetched(null);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(context, "Erro na chamada de API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                holder.imageViewServico.setImageResource(R.drawable.imagemanuncio);
                listener.onUrlFetched(null);
            }
        });
    }

    // Modifique buscarImagemNoFirebase para usar o callback OnImageUrlFetchedListener
    private void buscarImagemNoFirebase(String email, ServicoViewHolder holder, OnImageUrlFetchedListener listener) {
        String imagePath = "galeria/" + email + ".jpg";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(imagePath);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String url = uri.toString();
            listener.onUrlFetched(url); // Retorna a URL usando o listener
            Glide.with(context)
                    .load(url)
                    .into(holder.imageViewServico);
        }).addOnFailureListener(e -> {
            Log.e("AdapterServico", "Erro ao carregar imagem: " + e.getMessage());
            holder.imageViewServico.setImageResource(R.drawable.imagemanuncio);
            listener.onUrlFetched(null);
        });
    }

    @Override
    public int getItemCount() {
        return servicoList.size();
    }

    // Interface de callback para retornar a URL da imagem
    interface OnImageUrlFetchedListener {
        void onUrlFetched(String imageUrl);
    }

    public static class ServicoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNomeServico, textViewCodigoServico, textViewPrecoServico;
        ImageView imageViewServico;

        public ServicoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNomeServico = itemView.findViewById(R.id.textViewNomeServico);
            textViewCodigoServico = itemView.findViewById(R.id.textViewCodigoServico);
            textViewPrecoServico = itemView.findViewById(R.id.textViewPrecoMedio);
            imageViewServico = itemView.findViewById(R.id.imageViewServico);
        }
    }
}
