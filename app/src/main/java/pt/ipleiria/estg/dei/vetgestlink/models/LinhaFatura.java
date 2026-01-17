package pt.ipleiria.estg.dei.vetgestlink.models;

public class LinhaFatura {
    private int id;
    private String descricao;
    private String tipo;
    private int quantidade;
    private double precoUnitario;
    private double total;

    public LinhaFatura(int id, String descricao, String tipo, int quantidade, double precoUnitario, double total) {
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.total = total;
    }

    public String getDescricao() { return descricao; }
    public int getQuantidade() { return quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }
    public double getTotal() { return total; }
}
