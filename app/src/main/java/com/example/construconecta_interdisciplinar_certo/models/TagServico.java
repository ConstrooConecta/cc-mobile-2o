package com.example.construconecta_interdisciplinar_certo.models;

public class TagServico {
    private int tagServicoId;
    private String nome;
    private double precoMedio;

    public TagServico(
            int tagServicoId,
            String nome,
            double precoMedio
    ) {
        this.tagServicoId = tagServicoId;
        this.nome = nome;
        this.precoMedio = precoMedio;
    }

    public double getPrecoMedio() {
        return precoMedio;
    }
    public int getTagServicoId() {
        return tagServicoId;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
}