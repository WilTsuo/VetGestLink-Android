package pt.ipleiria.estg.dei.vetgestlink.models;


public class Especie {

    private int id;
    private String nome;
    private boolean eliminado;

    //Constructor

    public Especie(int id, String nome, boolean eliminado) {
        this.id = id;
        this.nome = nome;
        this.eliminado = eliminado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    @Override
    public String toString() { return nome; }
}
