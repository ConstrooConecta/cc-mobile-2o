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

import com.example.construconecta_interdisciplinar_certo.R;
import com.example.construconecta_interdisciplinar_certo.shop.contratar.RecyclerServicosActivity;
import com.example.construconecta_interdisciplinar_certo.models.TagServicoCategoria;

import java.util.List;

public class AdapterTagServicoContratar extends RecyclerView.Adapter<AdapterTagServicoContratar.ViewHolder> {
    private List<TagServicoCategoria> listaCategorias;
    private Context context;

    public AdapterTagServicoContratar(List<TagServicoCategoria> listaCategorias, Context context) {
        this.listaCategorias = listaCategorias;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_categoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TagServicoCategoria tagServicoCategoria = listaCategorias.get(position);
        holder.textCategoria.setText(tagServicoCategoria.getNome());
        holder.imageCategoria.setImageResource(tagServicoCategoria.getImagemResId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecyclerServicosActivity.class);
                intent.putExtra("tagName", tagServicoCategoria.getNome());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCategoria;
        TextView textCategoria;

        public ViewHolder(View itemView) {
            super(itemView);
            imageCategoria = itemView.findViewById(R.id.image_categoria);
            textCategoria = itemView.findViewById(R.id.text_categoria);
        }
    }
}
