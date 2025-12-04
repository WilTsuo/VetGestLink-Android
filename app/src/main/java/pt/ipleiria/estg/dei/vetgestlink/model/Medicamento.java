package pt.ipleiria.estg.dei.vetgestlink.model;

public class Medicamento {

    private int id;
    private String nome;
    private String descricao;
    private float preco;
    private int quantidade;
    private int categoriasId;
    private boolean eliminado;

    //Constructor

    public Medicamento(int id, String nome, String descricao, float preco, int quantidade, int categoriasId, boolean eliminado) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidade = quantidade;
        this.categoriasId = categoriasId;
        this.eliminado = eliminado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public float getPreco() { return preco; }
    public void setPreco(float preco) { this.preco = preco; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int getCategoriasId() { return categoriasId; }
    public void setCategoriasId(int categoriasId) { this.categoriasId = categoriasId; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
}
