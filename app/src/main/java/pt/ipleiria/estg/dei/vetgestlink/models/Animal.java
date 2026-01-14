package pt.ipleiria.estg.dei.vetgestlink.models;

public class Animal {
    private int id;
    private String nome;
    private String especie;
    private String raca;
    private int idade;
    private double peso;
    private String sexo;
    private int microchip;
    private String foto_url;
    private String datanascimento;
    private int userprofiles_id;
    private String created_at;
    private String updated_at;

    public Animal() {}

    public Animal(int id, String nome, String especie, String raca, int idade, double peso,
                  String sexo, String datanascimento, int microchip, String foto_url,
                  int userprofiles_id, String created_at, String updated_at) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.peso = peso;
        this.sexo = sexo;
        this.datanascimento = datanascimento;
        this.microchip = microchip;
        this.foto_url = foto_url;
        this.userprofiles_id = userprofiles_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public int getMicrochip() { return microchip; }
    public void setMicrochip(int microchip) { this.microchip = microchip; }

    public String getFotoUrl() { return foto_url; }
    public void setFotoUrl(String foto_url) { this.foto_url = foto_url; }

    public String getDtanascimento() { return datanascimento; }
    public void setDtanascimento(String datanascimento) { this.datanascimento = datanascimento; }

    public int getUserprofilesId() { return userprofiles_id; }
    public void setUserprofilesId(int userprofiles_id) { this.userprofiles_id = userprofiles_id; }

    public String getCreatedAt() { return created_at; }
    public void setCreatedAt(String created_at) { this.created_at = created_at; }

    public String getUpdatedAt() { return updated_at; }
    public void setUpdatedAt(String updated_at) { this.updated_at = updated_at; }
}
