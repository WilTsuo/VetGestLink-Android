package pt.ipleiria.estg.dei.vetgestlink.models;

public class Nota {

    private int id;
    private String nota;
    private String createdAt;
    private String updatedAt;

    private String animalNome;
    private String autor;
    //Helper field
    private String titulo;



    // Constructor
    public Nota(int id, String nota, String createdAt, String updatedAt, String animalNome, String autor, String titulo) {
        this.id = id;
        this.nota = nota;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.animalNome = animalNome;
        this.autor = autor;
        this.titulo = titulo;
    }


    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
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

    public String getAnimalNome() {
        return animalNome;
    }

    public void setAnimalNome(String animalNome) {
        this.animalNome = animalNome;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    //region - Helper Methods

    /**
     * Retorna um título extraído da nota (primeiras palavras ou primeiros 50 caracteres)
     */
    public String getTitulo() {
        if (titulo != null && !titulo.isEmpty()) return titulo;

        if (nota != null && !nota.isEmpty()) {
            int maxLength = Math.min(50, nota.length());
            String tituloExtraido = nota.substring(0, maxLength);

            if (nota.length() > 50) {
                int lastSpace = tituloExtraido.lastIndexOf(' ');
                if (lastSpace > 0) tituloExtraido = tituloExtraido.substring(0, lastSpace);
            }

            return tituloExtraido;
        }

        return "";
    }

    /**
     * Retorna a descrição completa da nota
     */
    public String getDescricao() {
        return nota;
    }

    /**
     * Retorna a data formatada em "DD MMM YYYY"
     */
    public String getData() {
        if (createdAt == null || createdAt.isEmpty()) return "";

        try {
            String[] parts = createdAt.split(" ");
            String[] dateParts = parts[0].split("-");

            if (dateParts.length == 3) {
                String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                        "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
                int mes = Integer.parseInt(dateParts[1]);
                return dateParts[2] + " " + meses[mes - 1] + " " + dateParts[0];
            }
        } catch (Exception e) {
            // Caso falhe, retorna a data original
            return createdAt;
        }

        return createdAt;
    }

    @Override
    public String toString() {
        return getTitulo();
    }
    //endregion
}
