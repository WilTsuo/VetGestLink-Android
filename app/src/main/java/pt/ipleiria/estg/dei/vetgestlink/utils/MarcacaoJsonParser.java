package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;

public class MarcacaoJsonParser {

    private static final String TAG = "MarcacaoJsonParser";

    /**
     * Converte um JSONArray de marcações numa lista de objetos Marcacao.
     */
    public static ArrayList<Marcacao> parseMarcacoes(JSONArray response) {
        ArrayList<Marcacao> marcacoes = new ArrayList<>();
        if (response == null) return marcacoes;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                Marcacao m = parseMarcacao(obj); // Reutiliza o método auxiliar
                if (m != null) {
                    marcacoes.add(m);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Erro ao processar item da lista de marcações: " + e.getMessage());
            }
        }
        return marcacoes;
    }

    /**
     * Método auxiliar para converter um único JSONObject em Marcacao.
     * Usado tanto pela lista quanto pelos detalhes.
     */
    public static Marcacao parseMarcacao(JSONObject response) {
        try {
            Marcacao marcacao = new Marcacao(
                    response.optInt("id", 0),
                    response.optString("data", ""),
                    response.optString("horainicio", ""),
                    response.optString("horafim", ""),
                    response.optString("estado", ""),
                    response.optInt("duracao_minutos", 0),
                    response.optString("diagnostico", ""),
                    response.optString("servico_nome", ""),
                    "", // Placeholder Animal Nome
                    ""  // Placeholder Animal Espécie
            );

            // Parse do objeto Animal aninhado
            JSONObject animalObj = response.optJSONObject("animal");
            if (animalObj != null) {
                marcacao.setAnimalNome(animalObj.optString("nome", "N/A"));

                String especie = animalObj.optString("especie", "");
                String raca = animalObj.optString("raca", "");

                if (!raca.isEmpty() && !raca.equalsIgnoreCase("null")) {
                    marcacao.setAnimalEspecie(especie + " (" + raca + ")");
                } else {
                    marcacao.setAnimalEspecie(especie);
                }
            }
            return marcacao;

        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar objeto marcação: " + e.getMessage());
            return null;
        }
    }
}
