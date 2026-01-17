package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;

public class MarcacaoJsonParser {

    private static final String TAG = "MarcacaoJsonParser";

    public static ArrayList<Marcacao> parseMarcacoes(JSONArray response) {
        ArrayList<Marcacao> marcacoes = new ArrayList<>();
        if (response == null) return marcacoes;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                Marcacao m = parseMarcacao(obj);
                if (m != null) {
                    marcacoes.add(m);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Erro ao processar item da lista de marcações: " + e.getMessage());
            }
        }
        return marcacoes;
    }

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
                    "", // Placeholder Nome
                    "", // Placeholder Espécie
                    "", // Placeholder Raça
                    ""  // Placeholder Género
            );

            String nome = "";
            String especie = "";
            String raca = "";
            String sexo = "";

            // 1. Tenta obter do objeto aninhado "animal" (comum em view/detalhes)
            JSONObject animalObj = response.optJSONObject("animal");

            if (animalObj != null) {
                nome = animalObj.optString("nome", "N/A");
                especie = animalObj.optString("especie", "");
                raca = animalObj.optString("raca", "");
                sexo = animalObj.optString("sexo", "");
            } else {
                // 2. Se não houver objeto aninhado, tenta ler da raiz (comum em listas)
                // Verifica chaves comuns para o nome do animal
                if (response.has("animal_nome")) {
                    nome = response.optString("animal_nome");
                } else if (response.has("nome") && !response.has("horainicio")) {
                    // Cuidado: 'nome' pode ser confuso, mas se não for marcação, assumimos animal
                    nome = response.optString("nome");
                }

                // Tenta ler outros campos da raiz se existirem
                especie = response.optString("animal_especie", response.optString("especie", ""));
                raca = response.optString("animal_raca", response.optString("raca", ""));
                sexo = response.optString("animal_sexo", response.optString("sexo", ""));
            }

            // Define os valores no objeto
            marcacao.setAnimalNome(nome != null && !nome.isEmpty() ? nome : "Sem Nome");
            marcacao.setAnimalEspecie(especie);
            marcacao.setAnimalRaca(raca != null && !raca.equals("null") ? raca : "-");

            // Lógica de Género
            if ("M".equalsIgnoreCase(sexo)) {
                marcacao.setAnimalGenero("Macho");
            } else if ("F".equalsIgnoreCase(sexo)) {
                marcacao.setAnimalGenero("Fêmea");
            } else {
                marcacao.setAnimalGenero(sexo);
            }

            return marcacao;

        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar objeto marcação: " + e.getMessage());
            return null;
        }
    }
}
