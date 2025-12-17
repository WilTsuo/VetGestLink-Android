package pt.ipleiria.estg.dei.vetgestlink.Listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.model.Lembrete;

public interface LembretesListener {
    void onRefreshListaFaturas(ArrayList<Lembrete> listaLembretes);
}
