package pt.ipleiria.estg.dei.vetgestlink.models;

public class Animal {
    private int id;
    private String nome;
    private String dtanascimento;
    private float peso;
    private boolean microship;
    private String sexo;
    private int especiesId;
    private String especieNome;
    private int racasId;
    private String racaNome;
    private boolean eliminado;

    // Construtor Vazio necessário para o Singleton
    public Animal() {}

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

    public int getRacasId() { return racasId; }
    public void setRacasId(int racasId) { this.racasId = racasId; }

    public String getRacaNome() { return racaNome; }
    public void setRacaNome(String racaNome) { this.racaNome = racaNome; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
}
