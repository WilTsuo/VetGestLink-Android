package pt.ipleiria.estg.dei.vetgestlink.model;

import java.io.Serializable;

/**
 * Model para Nota do Animal
 * Baseado na tabela 'notas' do banco de dados
 */
public class Nota implements Serializable {

    private int id;
    private String nota; // Conteúdo da nota (VARCHAR 500)
    private String createdAt; // Formato: YYYY-MM-DD HH:MM:SS
    private String updatedAt; // Formato: YYYY-MM-DD HH:MM:SS
    private int userprofilesId;
    private int animaisId;

    // Campos auxiliares (não vêm do banco diretamente)
    private String nomeAnimal;
    private String titulo; // Extraído dos primeiros caracteres da nota

    public Nota() {}

    public Nota(int id, String nota, String createdAt) {
        this.id = id;
        this.nota = nota;
        this.createdAt = createdAt;
    }

    // Getters e Setters
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

    // Métodos auxiliares

    /**
     * Retorna um título extraído da nota (primeiras palavras)
     */
    public String getTitulo() {
        if (titulo != null && !titulo.isEmpty()) {
            return titulo;
        }
        if (nota != null && !nota.isEmpty()) {
            // Extrair título dos primeiros 50 caracteres
            int maxLength = Math.min(50, nota.length());
            String tituloExtraido = nota.substring(0, maxLength);
            if (nota.length() > 50) {
                // Tentar cortar em uma palavra completa
                int lastSpace = tituloExtraido.lastIndexOf(' ');
                if (lastSpace > 0) {
                    tituloExtraido = tituloExtraido.substring(0, lastSpace);
                }
            }
            return tituloExtraido;
        }
        return "";
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Retorna a descrição (conteúdo completo da nota)
     */
    public String getDescricao() {
        return nota;
    }

    /**
     * Retorna a data formatada (DD MMM YYYY)
     */
    public String getData() {
        if (createdAt != null && !createdAt.isEmpty()) {
            // Formato esperado: YYYY-MM-DD HH:MM:SS
            // Retornar apenas a data formatada
            try {
                String[] partes = createdAt.split(" ");
                if (partes.length > 0) {
                    String[] dataPartes = partes[0].split("-");
                    if (dataPartes.length == 3) {
                        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                                        "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
                        int mes = Integer.parseInt(dataPartes[1]);
                        return dataPartes[2] + " " + meses[mes - 1] + " " + dataPartes[0];
                    }
                }
            } catch (Exception e) {
                return createdAt;
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return titulo != null ? titulo : getTitulo();
    }
}

