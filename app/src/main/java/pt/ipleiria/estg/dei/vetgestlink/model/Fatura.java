package pt.ipleiria.estg.dei.vetgestlink.model;

import java.io.Serializable;

public class Fatura implements Serializable {

    private int id;
    private float total;
    private String createdAt;
    private int estado; // 0=not paid 1=paid
    private int metodospagamentosId;
    private int userprofilesId;
    private boolean eliminado;

    public Fatura() {}

    public Fatura(int id, float total, String createdAt, boolean eliminado) {
        this.id = id;
        this.total = total;
        this.createdAt = createdAt;
        this.eliminado = eliminado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getTotal() { return total; }
    public void setTotal(float total) { this.total = total; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public int getMetodospagamentosId() { return metodospagamentosId; }
    public void setMetodospagamentosId(int metodospagamentosId) { this.metodospagamentosId = metodospagamentosId; }

    public int getUserprofilesId() { return userprofilesId; }
    public void setUserprofilesId(int userprofilesId) { this.userprofilesId = userprofilesId; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
}
