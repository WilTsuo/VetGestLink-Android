package pt.ipleiria.estg.dei.vetgestlink.model;

import java.io.Serializable;
public class Fatura {

    private int id;
    private float total;
    private String createdAt;       // DATETIME (YYYY-MM-DD HH:MM:SS)
    private int estado;             // 0=not paid or 1=paid
    private int metodospagamentosId;
    private int userprofilesId;
    private int eliminado;          // 0 or 1

    // Constructor
    public Fatura(int id, float total, String createdAt, int estado,
                  int metodospagamentosId, int userprofilesId, int eliminado) {

        this.id = id;
        this.total = total;
        this.createdAt = createdAt;
        this.estado = estado;
        this.metodospagamentosId = metodospagamentosId;
        this.userprofilesId = userprofilesId;
        this.eliminado = eliminado;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getTotal() { return total; }
    public void setTotal(float total) { this.total = total; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public int getMetodospagamentosId() { return metodospagamentosId; }
    public void setMetodospagamentosId(int metodospagamentosId) {
        this.metodospagamentosId = metodospagamentosId;
    }

    public int getUserprofilesId() { return userprofilesId; }
    public void setUserprofilesId(int userprofilesId) {
        this.userprofilesId = userprofilesId;
    }

    public int getEliminado() { return eliminado; }
    public void setEliminado(int eliminado) { this.eliminado = eliminado; }
}

