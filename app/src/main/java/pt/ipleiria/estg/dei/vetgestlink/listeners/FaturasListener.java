package pt.ipleiria.estg.dei.vetgestlink.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;

public interface FaturasListener {
    void onRefreshListaFaturas(ArrayList<Fatura> listaFaturas);
}
