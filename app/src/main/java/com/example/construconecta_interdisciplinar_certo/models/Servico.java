package com.example.construconecta_interdisciplinar_certo.models;

import java.util.List;

public class Servico {
    private int servicoId;
    private String nomeServico;
    private String descricao;
    private double preco;
    private String usuario;
    private List<TagServico> tagServicos;

    // Getters e Setters
    public int getServicoId() {
        return servicoId;
    }

    public void setServicoId(int servicoId) {
        this.servicoId = servicoId;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<TagServico> getTagServicos() {
        return tagServicos;
    }

    public void setTagServicos(List<TagServico> tagServicos) {
        this.tagServicos = tagServicos;
    }
}