package pt.ipleiria.estg.dei.vetgestlink.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;

public class MarcacaoJsonParser {

    public static ArrayList<Marcacao> parserJsonMarcacoes(String response) {
        ArrayList<Marcacao> marcacoes = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Usar optInt e optString previne o erro quando a API não envia certos campos
                Marcacao marcacao = new Marcacao(
                        jsonObject.optInt("id"),
                        jsonObject.optString("data"),
                        jsonObject.optString("horainicio"),
                        jsonObject.optString("horafim"),
                        jsonObject.optString("estado"),
                        jsonObject.optInt("duracao_minutos", 0), // Se faltar, assume 0
                        jsonObject.isNull("diagnostico") ? null : jsonObject.optString("diagnostico"),
                        jsonObject.optString("servico_nome"),
                        jsonObject.optString("animal_nome"),
                        jsonObject.optString("animal_especie", "") // Se faltar, assume vazio
                );
                marcacoes.add(marcacao);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return marcacoes;
    }
}