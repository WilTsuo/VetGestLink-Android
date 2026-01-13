package pt.ipleiria.estg.dei.vetgestlink.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;

public class FaturaJsonParser {
    //Parse list of faturas
    public static ArrayList<Fatura> parserJsonFaturas(JSONArray resposta) {
        ArrayList<Fatura> faturas = new ArrayList<>();

        for (int i = 0; i < resposta.length(); i++) {
            try {
                JSONObject jsonFatura = resposta.getJSONObject(i);

                int id = jsonFatura.getInt("id");
                float total = (float) jsonFatura.getDouble("total");
                boolean estado = jsonFatura.optInt("estado", 0) == 1;;
                String createdAt = jsonFatura.optString("created_at", "");
                boolean eliminado = jsonFatura.optInt("eliminado", 0) == 1;
                String metodoPagamento = jsonFatura.optString("metodo_pagamento", "");
                int numeroItens = jsonFatura.optInt("numero_itens", 0);

                Fatura f = new Fatura(id,  total,  createdAt,  estado,  eliminado,  metodoPagamento,  numeroItens);

                if (!eliminado) {
                    faturas.add(f);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return faturas;
    }

    // Parse single fatura
    public static Fatura parserJsonFatura(String resposta) {
        Fatura f = null;
        try {
            JSONObject jsonFatura = new JSONObject(resposta);

            int id = jsonFatura.getInt("id");
            float total = (float) jsonFatura.getDouble("total");
            boolean estado = jsonFatura.optInt("estado", 0) == 1;;
            String createdAt = jsonFatura.optString("created_at", "");
            boolean eliminado = jsonFatura.optInt("eliminado", 0) == 1;
            String metodoPagamento = jsonFatura.optString("metodo_pagamento", "");
            int numeroItens = jsonFatura.optInt("numero_itens", 0);

            f = new Fatura(id,  total,  createdAt,  estado,  eliminado,  metodoPagamento,  numeroItens);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return f;
    }
}
