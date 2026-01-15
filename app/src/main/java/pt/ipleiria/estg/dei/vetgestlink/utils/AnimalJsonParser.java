package pt.ipleiria.estg.dei.vetgestlink.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.models.Animal;

public class AnimalJsonParser {
    public static List<Animal> parseAnimais(JSONArray jsonArray) {
        List<Animal> animais = new ArrayList<>();
        if (jsonArray == null) return animais;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (obj.optBoolean("ativo", true)) {
                    Animal animal = new Animal();
                    animal.setId(obj.optInt("id", 0));
                    animal.setNome(obj.optString("nome", ""));
                    animal.setEspecie(obj.optString("especie", ""));
                    animal.setRaca(obj.optString("raca", ""));
                    animal.setIdade(obj.optString("idade", ""));
                    animal.setPeso(obj.optDouble("peso", 0.0));
                    animal.setSexo(obj.optString("sexo", ""));
                    animal.setMicrochip(obj.optInt("microchip", 0));
                    animal.setFotoUrl(obj.optString("foto_url", ""));
                    animal.setDtanascimento(obj.optString("datanascimento", ""));
                    animais.add(animal);
                }
            }
        } catch (JSONException e) {
            Log.e("Singleton", "Erro no parseAnimais: " + e.getMessage());
        }
        return animais;
    }

    public static List<Animal> parseAnimaisNome(JSONArray jsonArray) {
        List<Animal> animais = new ArrayList<>();
        if (jsonArray == null) return animais;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Animal animal = new Animal();
                animal.setId(obj.optInt("id", 0)); // Mapeia o ID necessário para o NotasFragment
                animal.setNome(obj.optString("nome", ""));
                animais.add(animal);
            }
        } catch (JSONException e) {
            Log.e("Singleton", "Erro no parseAnimaisNome: " + e.getMessage());
        }
        return animais;
    }
}
