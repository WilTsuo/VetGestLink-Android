package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.listeners.FaturasListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.OnFaturaClickListner;
import pt.ipleiria.estg.dei.vetgestlink.listeners.OnPagarClickListener;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.ListaFaturasAdapter;

public class PagamentosFragment extends Fragment implements FaturasListener, OnPagarClickListener {

    private ListView lvFaturas;
    private Button btnTodos, btnPendente, btnPago;
    private TextView tvQuantidadePendente, tvQuantidadePaga;

    private ArrayList<Fatura> listaFaturas = new ArrayList<>();
    private ArrayList<Fatura> listaFiltrada = new ArrayList<>();
    private ListaFaturasAdapter adapter;

    public PagamentosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pagamentos, container, false);

        lvFaturas = view.findViewById(R.id.lvFaturas);
        btnTodos = view.findViewById(R.id.btnTodos);
        btnPendente = view.findViewById(R.id.btnPendente);
        btnPago = view.findViewById(R.id.btnPago);
        tvQuantidadePendente = view.findViewById(R.id.tvQuantidadePendente);
        tvQuantidadePaga = view.findViewById(R.id.tvQuantidadePaga);

        // Adapter
        adapter = new ListaFaturasAdapter(
                requireContext(),
                listaFiltrada,
                this
        );
        lvFaturas.setAdapter(adapter);


        // Singleton + listener
        Singleton singleton = Singleton.getInstance(requireContext());
        singleton.setFaturasListener(this);

        // Token
        String token = getActivity()
                .getSharedPreferences("VetGestLinkPrefs", Context.MODE_PRIVATE)
                .getString("access_token", "");

        if (!token.isEmpty()) {
            singleton.getFaturas(token);
        }

        // Filters
        btnTodos.setOnClickListener(v -> filtrar("TODOS"));
        btnPendente.setOnClickListener(v -> filtrar("PENDENTE"));
        btnPago.setOnClickListener(v -> filtrar("PAGO"));

        return view;
    }

    private void filtrar(String tipo) {
        listaFiltrada.clear();

        for (Fatura f : listaFaturas) {
            if (tipo.equals("TODOS")) {
                listaFiltrada.add(f);
            } else if (tipo.equals("PENDENTE") && !f.isEstado()) {
                listaFiltrada.add(f);
            } else if (tipo.equals("PAGO") && f.isEstado()) {
                listaFiltrada.add(f);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void atualizarResumo() {
        float totalPendente = 0;
        float totalPago = 0;

        for (Fatura f : listaFaturas) {
            if (f.isEstado()) {
                totalPago += f.getTotal();
            } else {
                totalPendente += f.getTotal();
            }
        }

        tvQuantidadePendente.setText("€ " + String.format("%.2f", totalPendente));
        tvQuantidadePaga.setText("€ " + String.format("%.2f", totalPago));
    }

    @Override
    public void onRefreshListaFaturas(ArrayList<Fatura> faturas) {
        //para debug
        Log.d("FRAG_FATURAS", "Recebidas: " + faturas.size());

        listaFaturas.clear();
        listaFaturas.addAll(faturas);

        filtrar("TODOS");
        atualizarResumo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Singleton.getInstance(requireContext()).setFaturasListener(null);
    }

    @Override
    public void onPagarClick(Fatura fatura) {

        FragmentManager fm = requireActivity().getSupportFragmentManager();

        // Prevent duplicate bottom sheets
        Fragment existing = fm.findFragmentByTag("PagamentoBottomSheet");
        if (existing != null) {
            return; // already open
        }

        PagamentoBottomSheet sheet =
                PagamentoBottomSheet.newInstance(
                        fatura.getId(),
                        fatura.getTotal()
                );

        sheet.show(fm, "PagamentoBottomSheet");
    }

}
