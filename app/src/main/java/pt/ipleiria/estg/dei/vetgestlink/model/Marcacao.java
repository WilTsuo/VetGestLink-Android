package pt.ipleiria.estg.dei.vetgestlink.model;

public class Marcacao {

    private int id;

    private String data;          // DATE (YYYY-MM-DD)
    private String horainicio;    // TIME (HH:MM:SS)
    private String horafim;       // TIME (HH:MM:SS)

    private String createdAt;     // DATETIME (YYYY-MM-DD HH:MM:SS)
    private String updatedAt;     // DATETIME (YYYY-MM-DD HH:MM:SS)

    private String diagnostico;   // nullable text
    private String estado;        // enum or varchar

    private int animaisId;        // FK to animal
    private int userprofilesId;   // FK to userprofile

    private int eliminado;        // 0 or 1 (or convert to boolean)

    // Constructor
    public Marcacao(int id, String data, String horainicio, String horafim,
                    String createdAt, String updatedAt, String diagnostico,
                    String estado, int animaisId, int userprofilesId, int eliminado) {
        this.id = id;
        this.data = data;
        this.horainicio = horainicio;
        this.horafim = horafim;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.diagnostico = diagnostico;
        this.estado = estado;
        this.animaisId = animaisId;
        this.userprofilesId = userprofilesId;
        this.eliminado = eliminado;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorainicio() { return horainicio; }
    public void setHorainicio(String horainicio) { this.horainicio = horainicio; }

    public String getHorafim() { return horafim; }
    public void setHorafim(String horafim) { this.horafim = horafim; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getAnimaisId() { return animaisId; }
    public void setAnimaisId(int animaisId) { this.animaisId = animaisId; }

    public int getUserprofilesId() { return userprofilesId; }
    public void setUserprofilesId(int userprofilesId) { this.userprofilesId = userprofilesId; }

    public int getEliminado() { return eliminado; }
    public void setEliminado(int eliminado) { this.eliminado = eliminado; }
}

