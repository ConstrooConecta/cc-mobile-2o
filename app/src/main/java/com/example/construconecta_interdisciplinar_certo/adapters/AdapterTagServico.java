package com.example.construconecta_interdisciplinar_certo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.shop.contratar.RecyclerServicosActivity;
import com.example.construconecta_interdisciplinar_certo.models.TagServico;

import java.util.List;

public class AdapterTagServico extends RecyclerView.Adapter<AdapterTagServico.ServicoViewHolder> {
    private List<TagServico> servicoTagList;
    private Context context;

    public AdapterTagServico(List<TagServico> servicoTagList, Context context) {
        this.servicoTagList = servicoTagList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServicoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_servico, parent, false);
        return new ServicoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicoViewHolder holder, int position) {
        TagServico tagServico = servicoTagList.get(position);
        holder.textViewNomeServico.setText(tagServico.getNome());
        holder.textViewCodigoServico.setText("Código " + tagServico.getTagServicoId());
        holder.textViewPrecoServico.setText("R$ " + tagServico.getPrecoMedio());

        //pegar o id do tagServico, que é uma lista
        String url = null;

        int tagServicoId = tagServico.getTagServicoId();

        if (tagServicoId == 1) {
            url = "https://www.triider.com.br/_next/image?url=%2Fimg%2Ficones-do-catalogo%2Fencanador.webp&w=1440&q=75";
        } else if (tagServicoId == 2) {
            url = "https://www.triider.com.br/_next/image?url=%2Fimg%2Ficones-do-catalogo%2Feletricista.jpg&w=1440&q=75";
        } else if (tagServicoId == 3) {
            url = "https://www.triider.com.br/_next/image?url=%2Fimg%2Ficones-do-catalogo%2Finstalacao-de-piso-laminado.webp&w=768&q=75";
        } else if (tagServicoId == 4) {
            url = "https://www.triider.com.br/_next/image?url=%2Fimg%2Ficones-do-catalogo%2Ffaz-tudo.png&w=1440&q=75";

        }
        Glide.with(holder.imageViewServico.getContext()).asBitmap().load(url).into(holder.imageViewServico);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecyclerServicosActivity.class);
                intent.putExtra("tagName", tagServico.getNome());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return servicoTagList.size();
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
