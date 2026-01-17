package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;

public class AnimalJsonParser {

    private static final String TAG = "AnimalJsonParser";

    /**
     * Converte um JSONArray de animais numa lista de objetos Animal.
     * Filtra apenas os animais ativos.
     */
    public static List<Animal> parseAnimais(JSONArray jsonArray) {
        List<Animal> animais = new ArrayList<>();
        if (jsonArray == null) return animais;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);

                // Apenas processa se o animal estiver ativo
                if (obj.optBoolean("ativo", true)) {
                    Animal animal = new Animal();
                    animal.setId(obj.optInt("id", 0));
                    animal.setNome(obj.optString("nome", "Sem Nome"));
                    animal.setEspecie(obj.optString("especie", "N/A"));
                    animal.setRaca(obj.optString("raca", "N/A"));
                    animal.setIdade(obj.optString("idade", "N/A"));
                    animal.setPeso(obj.optDouble("peso", 0.0));
                    animal.setSexo(obj.optString("sexo", "N/A"));
                    animal.setMicrochip(obj.optInt("microchip", 0));
                    animal.setFotoUrl(obj.optString("foto_url", ""));
                    animal.setDtanascimento(obj.optString("datanascimento", ""));

                    animais.add(animal);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Erro ao processar animal no índice " + i + ": " + e.getMessage());
            }
        }
        return animais;
    }

    /**
     * Converte um JSONArray numa lista simplificada de Animais (apenas ID e Nome).
     * Útil para dropdowns ou filtros.
     */
    public static List<Animal> parseAnimaisNome(JSONArray jsonArray) {
        List<Animal> animais = new ArrayList<>();
        if (jsonArray == null) return animais;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                Animal animal = new Animal();
                animal.setId(obj.optInt("id", 0));
                animal.setNome(obj.optString("nome", "Sem Nome"));
                animais.add(animal);
            } catch (JSONException e) {
                Log.e(TAG, "Erro ao processar nome do animal: " + e.getMessage());
            }
        }
        return animais;
    }
}
