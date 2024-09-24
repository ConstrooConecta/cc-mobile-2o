package com.example.construconecta_interdisciplinar_certo.network.model;


import java.util.Date;


public class Usuario {
    private String uid;

    //x
    private String cpf;

    private String nomeCompleto;

    //x
    private String nomeUsuario;

    private String email;
    private String senha;


    private String telefone;



    //x
    private Integer genero;

    //x
    private String dataNascimento;

    public Usuario(
            String uid,
            String cpf,
            String nomeCompleto,
            String nomeUsuario,
            String email,
            String senha,
            String telefone,
            Integer genero,
            String dataNascimento
    ) {
        this.uid = uid;
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.genero = genero;
        this.dataNascimento = dataNascimento;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Integer getGenero() {
        return genero;
    }

    public void setGenero(Integer genero) {
        this.genero = genero;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }


    @Override
    public String toString() {
        return "Usuario{" +
                "uid='" + uid + '\'' +
                ", cpf='" + cpf + '\'' +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", telefone='" + telefone + '\'' +
                ", genero=" + genero +
                ", dataNascimento=" + dataNascimento +
                '}';
    }
}
