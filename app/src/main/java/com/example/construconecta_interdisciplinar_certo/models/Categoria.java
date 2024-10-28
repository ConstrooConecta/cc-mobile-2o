package com.example.construconecta_interdisciplinar_certo.models;

public class Categoria {
    int categoriaId;
    String nome;

    public Categoria(int categoriaId, String nome) {
        this.categoriaId = categoriaId;
        this.nome = nome;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "categoriaId=" + categoriaId +
                ", nome='" + nome + '\'' +
                '}';
    }
}
