package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.models.Lembrete;

public class LembreteJsonParser {

    private static final String TAG = "LembreteJsonParser";

    /**
     * Converte um JSONArray de lembretes numa lista de objetos Lembrete.
     */
    public static List<Lembrete> parseLembretes(JSONArray jsonArray) {
        List<Lembrete> lista = new ArrayList<>();
        if (jsonArray == null) return lista;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);

                int id = obj.optInt("id", 0);
                String descricao = obj.optString("descricao", "");
                String createdAt = obj.optString("created_at", "");
                String updatedAt = obj.optString("updated_at", "");
                int userprofilesId = obj.optInt("userprofiles_id", 0);

                Lembrete l = new Lembrete(id, descricao, createdAt, updatedAt, userprofilesId);
                lista.add(l);

            } catch (JSONException e) {
                Log.e(TAG, "Erro ao processar lembrete: " + e.getMessage());
            }
        }
        return lista;
    }
}
