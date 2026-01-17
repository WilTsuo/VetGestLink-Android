package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class PerfilJsonParser {

    private static final String TAG = "PerfilJsonParser";

    // Classe auxiliar estática para transportar os dados do perfil
    public static class ProfileDetails {
        public String nome;
        public String email;
        public String telefone;
        public String moradaCompleta;

        public ProfileDetails(String nome, String email, String telefone, String moradaCompleta) {
            this.nome = nome;
            this.email = email;
            this.telefone = telefone;
            this.moradaCompleta = moradaCompleta;
        }
    }

    /**
     * Converte a resposta JSON do perfil num objeto auxiliar ProfileDetails.
     * Combina dados do utilizador, perfil e morada.
     */
    public static ProfileDetails parseProfile(JSONObject response) {
        try {
            JSONObject user = response.optJSONObject("user");
            JSONObject profile = response.optJSONObject("profile");
            JSONObject morada = response.optJSONObject("morada");

            String nome = (profile != null) ? profile.optString("nomecompleto", "N/A") : "N/A";
            String email = (user != null) ? user.optString("email", "N/A") : "N/A";
            String telefone = (profile != null) ? profile.optString("telemovel", "N/A") : "N/A";

            String moradaCompleta = "";
            if (morada != null) {
                moradaCompleta = morada.optString("rua", "") + ", " +
                        morada.optString("nporta", "") + "\n" +
                        morada.optString("cdpostal", "") + " " +
                        morada.optString("localidade", "");
            }

            return new ProfileDetails(nome, email, telefone, moradaCompleta);

        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar perfil: " + e.getMessage());
            return null;
        }
    }
}
