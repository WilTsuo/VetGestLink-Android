package pt.ipleiria.estg.dei.vetgestlink.models;

public class Marcacao {

    private int id;
    private String data;          // DATE (YYYY-MM-DD)
    private String horaInicio;    // TIME (HH:MM:SS)
    private String horaFim;       // TIME (HH:MM:SS)
    private String estado;        //varchar
    private int duracaoMinutos;   // duration in minutes
    private String diagnostico;
    private String servicoNome;
    private String animalNome;
    private String animalEspecie;
    private String animalRaca;
    private String animalGenero;

    public Marcacao(int id, String data, String horaInicio, String horaFim, String estado,
                    int duracaoMinutos, String diagnostico, String servicoNome,
                    String animalNome, String animalEspecie, String animalRaca, String animalGenero) {
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
        this.animalRaca = animalRaca;
        this.animalGenero = animalGenero;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHoraInicio() {
        if (horaInicio != null && horaInicio.length() >= 5) {
            return horaInicio.substring(0, 5);
        }
        return horaInicio;
    }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFim() { return horaFim; }
    public void setHoraFim(String horaFim) { this.horaFim = horaFim; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getServicoNome() { return servicoNome; }
    public void setServicoNome(String servicoNome) { this.servicoNome = servicoNome; }

    public String getAnimalNome() { return animalNome; }
    public void setAnimalNome(String animalNome) { this.animalNome = animalNome; }

    public String getAnimalEspecie() { return animalEspecie; }
    public void setAnimalEspecie(String animalEspecie) { this.animalEspecie = animalEspecie; }

    public String getAnimalRaca() { return animalRaca; }
    public void setAnimalRaca(String animalRaca) { this.animalRaca = animalRaca; }

    public String getAnimalGenero() { return animalGenero; }
    public void setAnimalGenero(String animalGenero) { this.animalGenero = animalGenero; }
}
