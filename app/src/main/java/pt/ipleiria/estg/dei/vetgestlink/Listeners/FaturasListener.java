package pt.ipleiria.estg.dei.vetgestlink.Listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.model.Fatura;

public interface FaturasListener {
    void onRefreshListaFaturas(ArrayList<Fatura> listaFaturas);
}
