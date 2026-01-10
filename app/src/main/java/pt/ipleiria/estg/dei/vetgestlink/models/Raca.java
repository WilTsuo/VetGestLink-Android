package pt.ipleiria.estg.dei.vetgestlink.models;


public class Raca {

    private int id;
    private String nome;
    private int especiesId;
    private boolean eliminado;

    //Constructor

    public Raca(int id, String nome, int especiesId, boolean eliminado) {
        this.id = id;
        this.nome = nome;
        this.especiesId = especiesId;
        this.eliminado = eliminado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getEspeciesId() { return especiesId; }
    public void setEspeciesId(int especiesId) { this.especiesId = especiesId; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    @Override
    public String toString() { return nome; }
}
