package pt.ipleiria.estg.dei.vetgestlink.model;


public class Morada {

    private int id;
    private String rua;
    private String nporta;
    private String andar;       // nullable
    private String cdpostal;
    private String cidade;
    private String cxpostal;    // nullable
    private String localidade;
    private boolean principal;  // flag
    private int userprofilesId;
    private boolean eliminado;

    // Constructor

    public Morada(int id, String rua, String nporta, String andar, String cdpostal,
                  String cidade, String cxpostal, String localidade, boolean principal,
                  int userprofilesId, boolean eliminado) {

        this.id = id;
        this.rua = rua;
        this.nporta = nporta;
        this.andar = andar;
        this.cdpostal = cdpostal;
        this.cidade = cidade;
        this.cxpostal = cxpostal;
        this.localidade = localidade;
        this.principal = principal;
        this.userprofilesId = userprofilesId;
        this.eliminado = eliminado;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNporta() { return nporta; }
    public void setNporta(String nporta) { this.nporta = nporta; }

    public String getAndar() { return andar; }
    public void setAndar(String andar) { this.andar = andar; }

    public String getCdpostal() { return cdpostal; }
    public void setCdpostal(String cdpostal) { this.cdpostal = cdpostal; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getCxpostal() { return cxpostal; }
    public void setCxpostal(String cxpostal) { this.cxpostal = cxpostal; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }

    public int getUserprofilesId() { return userprofilesId; }
    public void setUserprofilesId(int userprofilesId) { this.userprofilesId = userprofilesId; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    @Override
    public String toString() {
        return rua + ", " + nporta + (andar != null ? ", " + andar : "") + ", " + cidade;
    }
}
