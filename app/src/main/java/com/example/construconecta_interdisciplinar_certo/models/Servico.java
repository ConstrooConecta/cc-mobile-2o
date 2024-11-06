package com.example.construconecta_interdisciplinar_certo.models;

import java.util.List;

public class Servico {
    private int servicoId;
    private String nomeServico;
    private String descricao;
    private double preco;
    private String usuario;
    private List<TagServico> tagServicos;

    public Servico(int servicoId, String nomeServico, String descricao, double preco, String usuario, List<TagServico> tagServicos) {
        this.servicoId = servicoId;
        this.nomeServico = nomeServico;
        this.descricao = descricao;
        this.preco = preco;
        this.usuario = usuario;
        this.tagServicos = tagServicos;
    }

    // Getters e Setters
    public int getServicoId() {
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
