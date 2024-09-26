package br.com.waltercoan.apppalestra.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Participante {
    private String id;
    @NotBlank(message = "Campo nome não pode ser em branco")
    @Size(max = 100, message = "Limite 100 caracteres")
    private String nome;
    @Size(max = 100, message = "Limite 100 caracteres")
    private String cidade;
    @Size(max = 100, message = "Limite 100 caracteres por observação")
    private String observacao;

    private String partitionKey;
    private String rowKey;
    
    public String getRowKey() {
        return rowKey;
    }
    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }
    public String getPartitionKey() {
        return partitionKey;
    }
    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    public String getObservacao() {
        return observacao;
    }
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    
}
