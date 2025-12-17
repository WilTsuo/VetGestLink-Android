package pt.ipleiria.estg.dei.vetgestlink.model;

public class Lembrete {
    private int id;
    private String descricao;
    private String createdAt;       // Formato: YYYY-MM-DD HH:MM:SS
    private String updatedAt;       // Formato: YYYY-MM-DD HH:MM:SS
    private int userprofilesId;

    //constructor


    public Lembrete(int id, String descricao, String createdAt, String updatedAt, int userprofilesId) {
        this.id = id;
        this.descricao = descricao;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userprofilesId = userprofilesId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getUserprofilesId() {
        return userprofilesId;
    }

    public void setUserprofilesId(int userprofilesId) {
        this.userprofilesId = userprofilesId;
    }
}
