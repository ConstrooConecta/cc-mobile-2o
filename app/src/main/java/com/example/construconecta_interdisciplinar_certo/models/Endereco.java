package com.example.construconecta_interdisciplinar_certo.models;

public class Endereco {
    private Integer enderecoUsuarioId;
    private String cep;
    private String uf;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String complemento;
    private String usuario;

    public Endereco(
            Integer enderecoUsuarioId,
            String cep,
            String uf,
            String cidade,
            String bairro,
            String rua,
            String numero,
            String complemento,
            String usuario
    ) {
        this.enderecoUsuarioId = enderecoUsuarioId;
        this.cep = cep;
        this.uf = uf;
        this.cidade = cidade;
        this.bairro = bairro;
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.usuario = usuario;
    }

    public Integer getEnderecoUsuarioId() { return enderecoUsuarioId; }
    public void setEnderecoUsuarioId(Integer enderecoUsuarioId) { this.enderecoUsuarioId = enderecoUsuarioId; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
