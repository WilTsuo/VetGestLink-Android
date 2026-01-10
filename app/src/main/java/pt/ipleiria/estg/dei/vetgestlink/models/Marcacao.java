package pt.ipleiria.estg.dei.vetgestlink.models;

public class Marcacao {

    private int id;
    private String data;          // DATE (YYYY-MM-DD)
    private String horaInicio;    // TIME (HH:MM:SS)
    private String horaFim;       // TIME (HH:MM:SS)
    private String estado;        // enum or varchar
    private int duracaoMinutos;  // duration in minutes (not in original but could be useful)
    private String diagnostico;
    private String servicoNome;
    private String animalNome;
    private String animalEspecie;

    // Constructor
    public Marcacao(int id, String data, String horaInicio, String horaFim, String estado, int duracaoMinutos, String diagnostico, String servicoNome, String animalNome, String animalEspecie) {
        this.id = id;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.estado = estado;
        this.duracaoMinutos = duracaoMinutos;
        this.diagnostico = diagnostico;
        this.servicoNome = servicoNome;
        this.animalNome = animalNome;
        this.animalEspecie = animalEspecie;
    }

    // Getters
    public int getId() { return id; }
    public String getData() { return data; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFim() { return horaFim; }
    public String getEstado() { return estado; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public String getDiagnostico() { return diagnostico; }
    public String getServicoNome() { return servicoNome; }
    public String getAnimalNome() { return animalNome; }
    public String getAnimalEspecie() { return animalEspecie; }
}

