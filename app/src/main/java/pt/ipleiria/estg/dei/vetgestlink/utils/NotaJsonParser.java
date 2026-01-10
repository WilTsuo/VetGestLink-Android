package pt.ipleiria.estg.dei.vetgestlink.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;

public class NotaJsonParser {

    public static ArrayList<Nota> parserJsonNotas(JSONArray resposta) {
        ArrayList<Nota> notas = new ArrayList<>();

        for (int i = 0; i < resposta.length(); i++) {
            try {
                JSONObject jsonNota = resposta.getJSONObject(i);

                int id = jsonNota.getInt("id");
                String textoNota = jsonNota.optString("nota", "");
                String createdAt = jsonNota.optString("created_at", "");
                String updatedAt = jsonNota.optString("updated_at", "");
                String animalNome = jsonNota.optString("animal_nome", "");
                String autor = jsonNota.optString("autor", "");
                String titulo = jsonNota.optString("titulo", "");
                Integer userprofileId = jsonNota.optInt("userprofiles_id", -1);

                Nota n = new Nota(id, textoNota,  createdAt,  updatedAt,  animalNome,  autor,  titulo, userprofileId);

                notas.add(n);

            } catch (Exception ignored) {}
        }
        return notas;
    }

    public static Nota parserJsonNota(String resposta) {
        try {
            JSONObject json = new JSONObject(resposta);

            int id = json.getInt("id");
            String textoNota = json.optString("nota", "");
            String createdAt = json.optString("created_at", "");
            String updatedAt = json.optString("updated_at", "");
            String animalNome = json.optString("animal_nome", "");
            String autor = json.optString("autor", "");
            String titulo = json.optString("titulo", "");
            Integer userprofileId = json.optInt("userprofiles_id", -1);


            Nota n = new Nota(id, textoNota,  createdAt,  updatedAt,  animalNome,  autor,  titulo, userprofileId);

            return n;

        } catch (Exception e) {
            return null;
        }
    }
}
