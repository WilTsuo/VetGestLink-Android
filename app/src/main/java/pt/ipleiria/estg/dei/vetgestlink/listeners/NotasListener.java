package pt.ipleiria.estg.dei.vetgestlink.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Nota;

public interface NotasListener {
    void onRefreshListaNotas(ArrayList<Nota> listaNotas);
}
