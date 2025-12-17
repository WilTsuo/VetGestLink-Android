package pt.ipleiria.estg.dei.vetgestlink.model;

public class Nota {

    private int id;
    private String nota;
    private String createdAt;
    private String updatedAt;

    private String animalNome;
    private String autor;
    //Helper field
    private String titulo;


    public Nota(int id, String nota, String createdAt, String updatedAt, String animalNome, String autor, String titulo) {
        this.id = id;
        this.nota = nota;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.animalNome = animalNome;
        this.autor = autor;
        this.titulo = titulo;
    }

    // Constructor
    public Nota(int id, String nota, String createdAt) {
        this.id = id;
        this.nota = nota;
        this.createdAt = createdAt;
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

    //helper method
    public String getTitulo() {
        if (titulo != null && !titulo.isEmpty()) {
            return titulo;
        }

        // Smart fallback if not manually set
        if (animalNome != null && !animalNome.isEmpty()) {
            return animalNome;
        }

        return "Nota";
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
