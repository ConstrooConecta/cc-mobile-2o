package com.example.construconecta_interdisciplinar_certo.models;

public class Categoria {
    Integer categoriaId;
    String nome;

    public Categoria(
            Integer categoriaId,
            String nome
    ) {
        this.categoriaId = categoriaId;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
