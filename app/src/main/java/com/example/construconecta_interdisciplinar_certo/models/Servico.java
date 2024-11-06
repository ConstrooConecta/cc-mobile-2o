package com.example.construconecta_interdisciplinar_certo.models;

import java.util.List;

public class Servico {
    private Integer servicoId;
    private String nomeServico;
    private String descricao;
    private double preco;
    private String usuario;
    private List<TagServico> tagServicos;

    // Getters e Setters
    public Integer getServicoId() {
        return servicoId;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
