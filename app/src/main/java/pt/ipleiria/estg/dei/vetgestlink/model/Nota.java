package pt.ipleiria.estg.dei.vetgestlink.model;

/**
 * Model for Nota (matches database table + includes API/UI extra fields)
 */
public class Nota {

    // ----- Fields from the database table "notas" -----
    private int id;
    private String nota;
    private String createdAt;
    private String updatedAt;
    private int userprofilesId;
    private int animaisId;

    // ----- EXTRA fields from API (not saved locally) -----
    private String animalNome;  // not in DB → provided by API
    private String autor;       // not in DB → provided by API

    // ----- UI Helper field -----
    private String titulo;      // extracted preview text

    // Constructor for DB + parser
    public Nota(int id, String nota, String createdAt) {
        this.id = id;
        this.nota = nota;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public int getUserprofilesId() { return userprofilesId; }
    public void setUserprofilesId(int userprofilesId) { this.userprofilesId = userprofilesId; }

    public int getAnimaisId() { return animaisId; }
    public void setAnimaisId(int animaisId) { this.animaisId = animaisId; }

    public String getAnimalNome() { return animalNome; }
    public void setAnimalNome(String animalNome) { this.animalNome = animalNome; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    // ----- UI Helpers -----

    public String getTitulo() {
        if (titulo != null && !titulo.isEmpty())
            return titulo;

        if (nota == null || nota.isEmpty())
            return "";

        int maxLength = Math.min(50, nota.length());
        String extracted = nota.substring(0, maxLength);

        if (nota.length() > 50) {
            int lastSpace = extracted.lastIndexOf(' ');
            if (lastSpace > 0) {
                extracted = extracted.substring(0, lastSpace);
            }
        }

        return extracted;
    }

    public String getDescricao() {
        return nota;
    }

    public String getData() {
        if (createdAt == null || createdAt.isEmpty())
            return "";

        try {
            String[] parts = createdAt.split(" ")[0].split("-");
            String[] meses = {"Jan","Fev","Mar","Abr","Mai","Jun","Jul","Ago","Set","Out","Nov","Dez"};

            return parts[2] + " " + meses[Integer.parseInt(parts[1]) - 1] + " " + parts[0];
        } catch (Exception e) {
            return createdAt;
        }
    }

    @Override
    public String toString() {
        return getTitulo();
    }
}
