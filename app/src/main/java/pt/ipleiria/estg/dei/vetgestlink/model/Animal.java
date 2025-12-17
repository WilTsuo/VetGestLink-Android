package pt.ipleiria.estg.dei.vetgestlink.model;


/**
 * Model para Animal
 * Baseado na tabela 'animais' do banco de dados
 */
public class Animal {

    private int id;
    private String nome;
    private String dtanascimento; // Formato: YYYY-MM-DD
    private float peso;
    private boolean microship;
    private String sexo; // 'M' ou 'F'
    private int especiesId;
    private String especieNome; // Join com especies
    private int userprofilesId;
    private Integer racasId; // Pode ser NULL
    private String racaNome;
    private boolean eliminado;

    public Animal() {}

    public Animal(int id, String nome, String especieNome, String racaNome) {
        this.id = id;
        this.nome = nome;
        this.especieNome = especieNome;
        this.racaNome = racaNome;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDtanascimento() { return dtanascimento; }
    public void setDtanascimento(String dtanascimento) { this.dtanascimento = dtanascimento; }

    public float getPeso() { return peso; }
    public void setPeso(float peso) { this.peso = peso; }

    public boolean isMicroship() { return microship; }
    public void setMicroship(boolean microship) { this.microship = microship; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public int getEspeciesId() { return especiesId; }
    public void setEspeciesId(int especiesId) { this.especiesId = especiesId; }

    public String getEspecieNome() { return especieNome; }
    public void setEspecieNome(String especieNome) { this.especieNome = especieNome; }

    public int getUserprofilesId() { return userprofilesId; }
    public void setUserprofilesId(int userprofilesId) { this.userprofilesId = userprofilesId; }

    public Integer getRacasId() { return racasId; }
    public void setRacasId(Integer racasId) { this.racasId = racasId; }

    public String getRacaNome() { return racaNome; }
    public void setRacaNome(String racaNome) { this.racaNome = racaNome; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    @Override
    public String toString() {
        if (racaNome != null && !racaNome.isEmpty()) {
            return nome + " - " + racaNome;
        }
        return nome + " - " + especieNome;
    }
}

