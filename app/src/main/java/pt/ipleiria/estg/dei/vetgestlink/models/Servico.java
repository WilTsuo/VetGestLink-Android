package pt.ipleiria.estg.dei.vetgestlink.models;


public class Servico {

    private int id;
    private String nome;
    private float valor;
    private boolean eliminado;

    //Constructor

    public Servico(int id, String nome, float valor, boolean eliminado) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.eliminado = eliminado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public float getValor() { return valor; }
    public void setValor(float valor) { this.valor = valor; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    @Override
    public String toString() {
        return nome;
    }
}
