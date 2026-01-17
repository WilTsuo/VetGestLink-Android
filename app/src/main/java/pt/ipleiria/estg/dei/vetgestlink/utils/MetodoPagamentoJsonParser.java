package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.vetgestlink.models.MetodoPagamento;

public class MetodoPagamentoJsonParser {

    private static final String TAG = "MetodoPagamentoParser";

    /**
     * Converte um JSONArray em lista de Métodos de Pagamento.
     * Filtra apenas os métodos ativos (vigor == 1).
     */
    public static ArrayList<MetodoPagamento> parseMetodosPagamento(JSONArray resposta) {
        ArrayList<MetodoPagamento> metodos = new ArrayList<>();
        if (resposta == null) return metodos;

        for (int i = 0; i < resposta.length(); i++) {
            try {
                JSONObject jsonMetodo = resposta.getJSONObject(i);

                int id = jsonMetodo.optInt("id", 0);
                String nome = jsonMetodo.optString("nome", "Desconhecido");
                int vigor = jsonMetodo.optInt("vigor", 0);

                // Apenas adiciona métodos ativos
                if (vigor == 1) {
                    MetodoPagamento metodo = new MetodoPagamento(id, nome, vigor);
                    metodos.add(metodo);
                }

            } catch (JSONException e) {
                Log.e(TAG, "Erro ao processar método de pagamento: " + e.getMessage());
            }
        }
        return metodos;
    }
}
