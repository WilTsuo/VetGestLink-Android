package pt.ipleiria.estg.dei.vetgestlink.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.model.Animal;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;

public class AnimalJsonParser {
    public static ArrayList<Animal> parserJsonAnimal(JSONArray resposta) {
        ArrayList<Animal> listaAnimal = new ArrayList<>();
        for (int i = 0; i < resposta.length(); i++) {
            try {
                JSONObject jsonAnimal = resposta.getJSONObject(i);


                //animal.add(animal);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //return animal;
        return null;
    }
}
