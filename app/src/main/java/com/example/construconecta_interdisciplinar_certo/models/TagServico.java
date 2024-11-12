package com.example.construconecta_interdisciplinar_certo.models;

public class TagServico {
    private Integer tagServicoId;
    private String nome;
    private double precoMedio;

    public TagServico(
            Integer tagServicoId,
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

    public Integer getTagServicoId() {
        return tagServicoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}