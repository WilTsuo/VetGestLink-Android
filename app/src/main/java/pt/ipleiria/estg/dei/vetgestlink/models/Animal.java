package pt.ipleiria.estg.dei.vetgestlink.models;

public class Animal {
    private int id;
    private String nome;
    private String dtanascimento;
    private float peso;
    private boolean microchip;
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

    public int getIdade() {
        // Validação da data de nascimento
        if (dtanascimento == null || dtanascimento.trim().isEmpty()) {
            return 0;
        }

        try {
            // Parse da data de nascimento (formato esperado: yyyy-MM-dd)
            String[] parts = dtanascimento.split("-");
            if (parts.length != 3) {
                return 0;
            }

            int yearOfBirth = Integer.parseInt(parts[0]);
            int monthOfBirth = Integer.parseInt(parts[1]);
            int dayOfBirth = Integer.parseInt(parts[2]);

            // Data atual
            java.util.Calendar now = java.util.Calendar.getInstance();
            java.util.Calendar birthDate = java.util.Calendar.getInstance();
            birthDate.set(yearOfBirth, monthOfBirth - 1, dayOfBirth);

            // Cálculo da diferença em milissegundos
            long diffInMillis = now.getTimeInMillis() - birthDate.getTimeInMillis();

            // Conversão para dias
            int idadeEmDias = (int) (diffInMillis / (1000 * 60 * 60 * 24));

            return Math.max(0, idadeEmDias); // Garantir que não retorna valores negativos

        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public float getPeso() { return peso; }
    public void setPeso(float peso) { this.peso = peso; }

    public boolean getMicrochip() { return microchip; }
    public void setMicrochip(boolean microchip) { this.microchip = microchip; }

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
