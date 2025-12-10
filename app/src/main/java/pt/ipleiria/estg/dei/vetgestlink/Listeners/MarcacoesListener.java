package pt.ipleiria.estg.dei.vetgestlink.Listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.model.Marcacao;

public interface MarcacoesListener {
    void onRefreshListaMarcacoes(ArrayList<Marcacao> listaMarcacoes);
}
