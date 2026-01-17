package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;
import pt.ipleiria.estg.dei.vetgestlink.models.LinhaFatura;

public class FaturaJsonParser {

    private static final String TAG = "FaturaJsonParser";

    /**
     * Converte um JSONArray de faturas numa lista de objetos Fatura.
     * Ignora faturas marcadas como eliminadas.
     */
    public static ArrayList<Fatura> parseFaturas(JSONArray resposta) {
        ArrayList<Fatura> faturas = new ArrayList<>();
        if (resposta == null) return faturas;

        for (int i = 0; i < resposta.length(); i++) {
            try {
                JSONObject jsonFatura = resposta.getJSONObject(i);

                // Verifica se está eliminado antes de processar tudo
                boolean eliminado = jsonFatura.optInt("eliminado", 0) == 1;
                if (eliminado) continue;

                int id = jsonFatura.optInt("id", 0);
                float total = (float) jsonFatura.optDouble("total", 0.0);
                boolean estado = jsonFatura.optInt("estado", 0) == 1;
                String createdAt = jsonFatura.optString("created_at", "");
                String metodoPagamento = jsonFatura.optString("metodo_pagamento", "N/A");
                int numeroItens = jsonFatura.optInt("numero_itens", 0);

                Fatura f = new Fatura(id, total, createdAt, estado, false, metodoPagamento, numeroItens);
                faturas.add(f);

            } catch (JSONException e) {
                Log.e(TAG, "Erro ao processar fatura: " + e.getMessage());
            }
        }
        return faturas;
    }

    /**
     * Converte um JSONObject contendo detalhes de uma fatura (incluindo linhas e cliente).
     */
    public static Fatura parseFaturaDetalhes(JSONObject response) {
        try {
            // 1. Criar Fatura Base
            Fatura fatura = new Fatura(
                    response.optInt("id", 0),
                    (float) response.optDouble("total", 0.0),
                    response.optString("created_at", ""),
                    response.optString("estado", "0").equals("1"), // Trata string ou int
                    false,
                    response.optString("metodo_pagamento", "N/A"),
                    0
            );

            // 2. Parse do Cliente
            JSONObject clienteObj = response.optJSONObject("cliente");
            if (clienteObj != null) {
                fatura.setClienteNome(clienteObj.optString("nomecompleto", "Desconhecido"));
                fatura.setClienteNif(clienteObj.optString("nif", "N/A"));
            }

            // 3. Parse das Linhas da Fatura
            ArrayList<LinhaFatura> listaLinhas = new ArrayList<>();
            JSONArray linhasArray = response.optJSONArray("linhas");

            if (linhasArray != null) {
                for (int i = 0; i < linhasArray.length(); i++) {
                    JSONObject l = linhasArray.getJSONObject(i);
                    listaLinhas.add(new LinhaFatura(
                            l.optInt("id", 0),
                            l.optString("descricao", ""),
                            l.optString("tipo", ""),
                            l.optInt("quantidade", 0),
                            l.optDouble("preco_unitario", 0.0),
                            l.optDouble("total", 0.0)
                    ));
                }
            }
            fatura.setLinhas(listaLinhas);
            fatura.setNumeroItens(listaLinhas.size());

            return fatura;

        } catch (JSONException e) {
            Log.e(TAG, "Erro ao processar detalhes da fatura: " + e.getMessage());
            return null;
        }
    }
}
