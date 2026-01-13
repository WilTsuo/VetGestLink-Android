package pt.ipleiria.estg.dei.vetgestlink.Listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.model.MetodoPagamento;

public interface MetodosPagamentoListener {
    void onRefreshMetodosPagamento(ArrayList<MetodoPagamento> listaMetodos);
}
