package pt.ipleiria.estg.dei.vetgestlink.models;


public class MetodoPagamento {

    private int id;
    private String nome;
    private int vigor;

    // Constructor

    public MetodoPagamento(int id, String nome, int vigor) {
        this.id = id;
        this.nome = nome;
        this.vigor = vigor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getVigor() { return vigor; }
    public void setVigor(int vigor) { this.vigor = vigor; }

    @Override
    public String toString() {
        return nome;
    }
}
