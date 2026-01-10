package pt.ipleiria.estg.dei.vetgestlink.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Animal;

public interface AnimaisListener {
    void onRefreshListaAnimais(ArrayList<Animal> listaAnimais);
}
