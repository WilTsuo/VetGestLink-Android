package pt.ipleiria.estg.dei.vetgestlink.models;

public class Animal {
    private int id;
    private String nome;
    private String especie;
    private String raca;
    private String idade;
    private double peso;
    private String sexo;
    private int microchip;
    private String foto_url;
    private String datanascimento;

    public Animal() {}

    public Animal(String nome, String especie, String raca, String idade, double peso, String sexo, int microchip, String foto_url, String datanascimento) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.peso = peso;
        this.sexo = sexo;
        this.microchip = microchip;
        this.foto_url = foto_url;
        this.datanascimento = datanascimento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    // Getter atualizado
    public String getIdade() { return idade; }
    // Setter atualizado
    public void setIdade(String idade) { this.idade = idade; }

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
}
