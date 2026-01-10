package pt.ipleiria.estg.dei.vetgestlink.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Lembrete;

public interface LembretesListener {
    void onRefreshListaFaturas(ArrayList<Lembrete> listaLembretes);
}
