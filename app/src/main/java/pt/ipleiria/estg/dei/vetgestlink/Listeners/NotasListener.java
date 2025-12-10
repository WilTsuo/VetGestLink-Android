package pt.ipleiria.estg.dei.vetgestlink.Listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.model.Nota;

public interface NotasListener {
    void onRefreshListaNotas(ArrayList<Nota> listaNotas);
}
