package com.example.construconecta_interdisciplinar_certo.models;

public class TagServicoCategoria {
    private String nome;
    private int imagemResId;

    public TagServicoCategoria(
            String nome,
            int imagemResId
    ) {
        this.nome = nome;
        this.imagemResId = imagemResId;
    }

    public String getNome() {
        return nome;
    }

    public int getImagemResId() {
        return imagemResId;
    }
}
