package pt.ipleiria.estg.dei.vetgestlink.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.model.MetodoPagamento;

public class MetodoPagamentoJsonParser {

    //Parse list
    public static ArrayList<MetodoPagamento> parserJsonMetodosPagamento(JSONArray resposta) {

        ArrayList<MetodoPagamento> metodos = new ArrayList<>();

        for (int i = 0; i < resposta.length(); i++) {
            try {
                JSONObject jsonMetodo = resposta.getJSONObject(i);

                int id = jsonMetodo.getInt("id");
                String nome = jsonMetodo.getString("nome");
                int vigor = jsonMetodo.optInt("vigor", 0);
                boolean eliminado = jsonMetodo.optInt("eliminado", 0) == 1;

                // ⚠ Only add valid (active) methods
                if (vigor == 1 && !eliminado) {
                    MetodoPagamento metodo = new MetodoPagamento(
                            id,
                            nome,
                            vigor,
                            eliminado
                    );
                    metodos.add(metodo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return metodos;
    }

    //Parse
    public static MetodoPagamento parserJsonMetodoPagamento(String resposta) {

        MetodoPagamento metodo = null;

        try {
            JSONObject jsonMetodo = new JSONObject(resposta);

            int id = jsonMetodo.getInt("id");
            String nome = jsonMetodo.getString("nome");
            int vigor = jsonMetodo.optInt("vigor", 0);
            boolean eliminado = jsonMetodo.optInt("eliminado", 0) == 1;

            metodo = new MetodoPagamento(
                    id,
                    nome,
                    vigor,
                    eliminado
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return metodo;
    }
}
