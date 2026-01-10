package pt.ipleiria.estg.dei.vetgestlink.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;

public interface MarcacoesListener {
    void onRefreshListaMarcacoes(ArrayList<Marcacao> listaMarcacoes);
}
