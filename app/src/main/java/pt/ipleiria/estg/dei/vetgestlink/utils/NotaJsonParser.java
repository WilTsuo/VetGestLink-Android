package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.vetgestlink.models.Nota;

public class NotaJsonParser {

    private static final String TAG = "NotaJsonParser";

    /**
     * Converte um JSONArray de notas numa lista de objetos Nota.
     */
    public static ArrayList<Nota> parseNotas(JSONArray resposta) {
        ArrayList<Nota> notas = new ArrayList<>();
        if (resposta == null) return notas;

        for (int i = 0; i < resposta.length(); i++) {
            try {
                JSONObject jsonNota = resposta.getJSONObject(i);

                int id = jsonNota.optInt("id", 0);
                String textoNota = jsonNota.optString("nota", "");
                String createdAt = jsonNota.optString("created_at", "");
                String updatedAt = jsonNota.optString("updated_at", "");
                String animalNome = jsonNota.optString("animal_nome", "Geral");
                String autor = jsonNota.optString("autor", "Desconhecido");
                String titulo = jsonNota.optString("titulo", "");
                int userprofileId = jsonNota.optInt("userprofiles_id", -1);

                Nota n = new Nota(id, textoNota, createdAt, updatedAt, animalNome, autor, titulo, userprofileId);
                notas.add(n);

            } catch (JSONException e) {
                Log.e(TAG, "Erro ao processar nota: " + e.getMessage());
            }
        }
        return notas;
    }
}
