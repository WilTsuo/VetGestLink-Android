package pt.ipleiria.estg.dei.vetgestlink.models;


public class Fatura {

    private int id;
    private float total;
    private String createdAt;
    private boolean estado; // 0=not paid 1=paid
    private boolean eliminado;
    private String metodoPagamento;
    private int numeroItens;

    //Constructor

    public Fatura(int id, float total, String createdAt, boolean estado, boolean eliminado, String metodoPagamento, int numeroItens) {
        this.id = id;
        this.total = total;
        this.createdAt = createdAt;
        this.estado = estado;
        this.eliminado = eliminado;
        this.metodoPagamento = metodoPagamento;
        this.numeroItens = numeroItens;
    }


    //Getter & Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public int getNumeroItens() {
        return numeroItens;
    }

    public void setNumeroItens(int numeroItens) {
        this.numeroItens = numeroItens;
    }
}
