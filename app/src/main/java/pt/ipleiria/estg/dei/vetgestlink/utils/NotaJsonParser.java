package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
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
                String textoNota = jsonNota.getString("nota");
                String createdAt = jsonNota.optString("created_at", "");
                String updatedAt = jsonNota.optString("updated_at", "");
                int userprofilesId = jsonNota.optInt("userprofiles_id", -1);
                int animaisId = jsonNota.optInt("animais_id", -1);

                Nota n = new Nota(id, textoNota, createdAt);
                n.setUpdatedAt(updatedAt);
                n.setUserprofilesId(userprofilesId);
                n.setAnimaisId(animaisId);

                notas.add(n);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return notas;
    }
    public static Nota parserJsonNota(String resposta){
        Nota auxNota =null;
        try {
            JSONObject jsonNota = new JSONObject(resposta);

            int id = jsonNota.getInt("id");
            String textoNota = jsonNota.getString("nota");
            String createdAt = jsonNota.optString("created_at", "");
            String updatedAt = jsonNota.optString("updated_at", "");
            int userprofilesId = jsonNota.optInt("userprofiles_id", -1);
            int animaisId = jsonNota.optInt("animais_id", -1);

            auxNota = new Nota(id, textoNota, createdAt);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return auxNota;
    }


}
