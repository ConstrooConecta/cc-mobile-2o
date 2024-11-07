package com.example.construconecta_interdisciplinar_certo.models;

public class TagServicoCategoria {
    private String nome;
    private Integer imagemResId;

    public TagServicoCategoria(
            String nome,
            Integer imagemResId
    ) {
        this.nome = nome;
        this.imagemResId = imagemResId;
    }

    public String getNome() {
        return nome;
    }

    public Integer getImagemResId() {
        return imagemResId;
    }
}
