package pt.ipleiria.estg.dei.vetgestlink.models;


public class UserProfile {
    
    private int id;
    private String nomecompleto;
    private String username;
    private String nif;
    private String telemovel;
    private String dtanascimento; // Formato: YYYY-MM-DD
    private String dtaregisto; // Formato: YYYY-MM-DD HH:MM:SS
    private String userEmail; // Join com user
    private String userUsername; // Join com user
    private boolean eliminado;
    
    // Morada (pode ter várias, mas pegamos a principal)
    private String moradaRua;
    private String moradaNporta;
    private String moradaAndar;
    private String moradaCdpostal;
    private String moradaLocalidade;
    private String moradaCidade;

    /**
     * resposta de login experada (exemplo):
     * {
     *   "success": true,
     *   "message": "Login bem-sucedido",
     *   "token": "ak_cliente_000000000000000000000",
     *   "user": {
     *     "id": 21,
     *     "username": "cliente",
     *     "email": "cliente@vetgest.pt"
     *   }
     * }
    **/

    // Construtor vazio
    public UserProfile() {}
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNomecompleto() { return nomecompleto; }
    public void setNomecompleto(String nomecompleto) { this.nomecompleto = nomecompleto; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return userEmail; }
    public void setEmail(String email) { this.userEmail = email; }

    public String getContacto() { return telemovel; }
    public void setContacto(String contacto) { this.telemovel = contacto; }

    public String getFotoUrl() { return null; } // TODO: implementar campo foto
    public void setFotoUrl(String fotoUrl) { /* TODO */ }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }
    
    public String getTelemovel() { return telemovel; }
    public void setTelemovel(String telemovel) { this.telemovel = telemovel; }
    
    public String getDtanascimento() { return dtanascimento; }
    public void setDtanascimento(String dtanascimento) { this.dtanascimento = dtanascimento; }
    
    public String getDtaregisto() { return dtaregisto; }
    public void setDtaregisto(String dtaregisto) { this.dtaregisto = dtaregisto; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getUserUsername() { return userUsername; }

    public String getUsername() { return username; }
    public void setUserUsername(String userUsername) { this.userUsername = userUsername; }
    
    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
    
    // Morada
    public String getMoradaRua() { return moradaRua; }
    public void setMoradaRua(String moradaRua) { this.moradaRua = moradaRua; }
    
    public String getMoradaNporta() { return moradaNporta; }
    public void setMoradaNporta(String moradaNporta) { this.moradaNporta = moradaNporta; }
    
    public String getMoradaAndar() { return moradaAndar; }
    public void setMoradaAndar(String moradaAndar) { this.moradaAndar = moradaAndar; }
    
    public String getMoradaCdpostal() { return moradaCdpostal; }
    public void setMoradaCdpostal(String moradaCdpostal) { this.moradaCdpostal = moradaCdpostal; }
    
    public String getMoradaLocalidade() { return moradaLocalidade; }
    public void setMoradaLocalidade(String moradaLocalidade) { this.moradaLocalidade = moradaLocalidade; }
    
    public String getMoradaCidade() { return moradaCidade; }
    public void setMoradaCidade(String moradaCidade) { this.moradaCidade = moradaCidade; }
    
    /**
     * Retorna morada completa formatada
     */
    public String getMoradaCompleta() {
        StringBuilder sb = new StringBuilder();
        if (moradaRua != null) sb.append(moradaRua);
        if (moradaNporta != null) sb.append(", ").append(moradaNporta);
        if (moradaAndar != null && !moradaAndar.isEmpty()) sb.append(", ").append(moradaAndar);
        if (moradaCdpostal != null) sb.append(", ").append(moradaCdpostal);
        if (moradaLocalidade != null) sb.append(" ").append(moradaLocalidade);
        return sb.toString();
    }
    
    /**
     * Retorna telefone formatado (+351 XXX XXX XXX)
     */
    public String getTelemovelFormatado() {
        if (telemovel != null && telemovel.length() == 9) {
            return "+351 " + telemovel.substring(0, 3) + " " + 
                   telemovel.substring(3, 6) + " " + telemovel.substring(6);
        }
        return telemovel;
    }
    
    /**
     * Retorna data de nascimento formatada (DD/MM/YYYY)
     */
    public String getDtanascimentoFormatada() {
        if (dtanascimento != null && dtanascimento.length() == 10) {
            String[] parts = dtanascimento.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        }
        return dtanascimento;
    }
    
    /**
     * Retorna iniciais do nome
     */
    public String getIniciais() {
        if (nomecompleto != null && !nomecompleto.isEmpty()) {
            String[] parts = nomecompleto.split(" ");
            if (parts.length >= 2) {
                // Convert chars to String properly
                return "" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0);
            } else {
                return nomecompleto.substring(0, Math.min(2, nomecompleto.length()));
            }
        }
        return "??";
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", nomecompleto='" + nomecompleto + '\'' +
                ", email='" + userEmail + '\'' +
                '}';
    }
}
