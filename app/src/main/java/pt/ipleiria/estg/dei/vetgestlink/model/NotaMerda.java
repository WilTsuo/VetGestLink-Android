package pt.ipleiria.estg.dei.vetgestlink.model;

/**
 * Model para Nota do Animal
 * Baseado na tabela 'notas' do banco de dados
 */
public class NotaMerda {

    private int id;
    private String nota;            // Conteúdo da nota (VARCHAR 500)
    private String createdAt;       // Formato: YYYY-MM-DD HH:MM:SS
    private String updatedAt;       // Formato: YYYY-MM-DD HH:MM:SS
    private int userprofilesId;
    private int animaisId;

    // Campos auxiliares (não vêm do banco diretamente)
    private String nomeAnimal;
    private String titulo;          // Extraído dos primeiros caracteres da nota


    // Constructors
    public NotaMerda(int id, String nota, String createdAt) {
        this.id = id;
        this.nota = nota;
        this.createdAt = createdAt;
    }

    public NotaMerda() {
        this.nota = "";
        this.createdAt = "";
    }



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

    public String getNomeAnimal() { return nomeAnimal; }
    public void setNomeAnimal(String nomeAnimal) { this.nomeAnimal = nomeAnimal; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    // --------------------
    // Helper Methods
    // --------------------

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
}
