package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.listeners.FaturasListener;
import pt.ipleiria.estg.dei.vetgestlink.listeners.OnPagarClickListener;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.FaturasAdapter;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.LinhasFaturaAdapter;

public class FaturasFragment extends Fragment implements FaturasListener, OnPagarClickListener {

    private ListView lvFaturas;
    private Button btnTodos, btnPendente, btnPago;
    private TextView tvQuantidadePendente, tvQuantidadePaga;

    private ArrayList<Fatura> listaFaturas = new ArrayList<>();
    private ArrayList<Fatura> listaFiltrada = new ArrayList<>();
    private FaturasAdapter adapter;

    public FaturasFragment() {}

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
        adapter = new FaturasAdapter(
                requireContext(),
                listaFiltrada,
                this
        );
        lvFaturas.setAdapter(adapter);

        // Clique na lista para ver detalhes
        lvFaturas.setOnItemClickListener((parent, view1, position, id) -> {
            Fatura faturaSelecionada = listaFiltrada.get(position);
            Singleton.getInstance(requireContext()).getFaturaDetalhesAPI(faturaSelecionada.getId(), requireContext());
        });

        // Singleton + listener
        Singleton singleton = Singleton.getInstance(requireContext());
        singleton.setFaturasListener(this);

        // Token e Carregamento Inicial
        String token = getActivity()
                .getSharedPreferences("VetGestLinkPrefs", Context.MODE_PRIVATE)
                .getString("access_token", "");

        if (!token.isEmpty()) {
            // CORREÇÃO: Adicionado Callback para garantir atualização mesmo offline
            singleton.getFaturas(token, new Singleton.FaturasCallback() {
                @Override
                public void onSuccess(ArrayList<Fatura> faturas) {
                    // Atualiza a UI assim que os dados chegarem (da API ou Cache)
                    if (isAdded()) {
                        onRefreshListaFaturas(faturas);
                    }
                }

                @Override
                public void onError(String error) {
                    // Log de erro, mas a lista pode já ter sido carregada pelo cache no Singleton
                    Log.e("FaturasFragment", "Erro ao carregar: " + error);
                }
            });
        }

        // Filters
        btnTodos.setOnClickListener(v -> filtrar("TODOS"));
        btnPendente.setOnClickListener(v -> filtrar("PENDENTE"));
        btnPago.setOnClickListener(v -> filtrar("PAGO"));

        return view;
    }

    // --- Implementação do Listener de Detalhes ---
    @Override
    public void onFaturaDetalhesLoaded(Fatura faturaDetalhada) {
        if (getContext() != null) {
            mostrarDialogDetalhes(faturaDetalhada);
        }
    }

    private void mostrarDialogDetalhes(Fatura fatura) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fatura_detalhes, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Referências UI do Dialog
        TextView tvTitulo = dialogView.findViewById(R.id.tvDialogTitulo);
        TextView tvData = dialogView.findViewById(R.id.tvDialogData);
        TextView tvCliente = dialogView.findViewById(R.id.tvClienteNome);
        TextView tvNif = dialogView.findViewById(R.id.tvClienteNif);
        TextView tvTotal = dialogView.findViewById(R.id.tvDialogTotal);
        RecyclerView rv = dialogView.findViewById(R.id.rvLinhasFatura);
        Button btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        // Preencher Dados
        tvTitulo.setText("Fatura #" + fatura.getId());
        tvData.setText(fatura.getCreatedAt());
        tvCliente.setText("Cliente: " + (fatura.getClienteNome() != null ? fatura.getClienteNome() : "N/A"));
        tvNif.setText("NIF: " + (fatura.getClienteNif() != null ? fatura.getClienteNif() : "N/A"));
        tvTotal.setText("€ " + String.format("%.2f", fatura.getTotal()));

        // Configurar RecyclerView com as linhas
        if (fatura.getLinhas() != null) {
            LinhasFaturaAdapter linhasAdapter = new LinhasFaturaAdapter(getContext(), fatura.getLinhas());
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(linhasAdapter);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // --- Métodos existentes (Filtros, Refresh, Pagar) ---

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
        atualizarBotoesFiltro(tipo);
        atualizarResumo(); // Garante que os totais atualizam ao filtrar
    }

    private void atualizarResumo() {
        float totalPendente = 0;
        float totalPago = 0;
        // Calcula com base na lista completa, não na filtrada
        for (Fatura f : listaFaturas) {
            if (f.isEstado()) totalPago += f.getTotal();
            else totalPendente += f.getTotal();
        }
        tvQuantidadePendente.setText("€ " + String.format("%.2f", totalPendente));
        tvQuantidadePaga.setText("€ " + String.format("%.2f", totalPago));
    }

    @Override
    public void onRefreshListaFaturas(ArrayList<Fatura> faturas) {
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
        Fragment existing = fm.findFragmentByTag("PagamentoBottomSheet");
        if (existing != null) return;

        PagamentoBottomSheetFragment sheet = PagamentoBottomSheetFragment.newInstance(fatura.getId(), fatura.getTotal());
        sheet.show(fm, "PagamentoBottomSheet");
    }

    private void atualizarBotoesFiltro(String tipo) {
        resetBotao(btnTodos);
        resetBotao(btnPendente);
        resetBotao(btnPago);
        switch (tipo) {
            case "TODOS": destacarBotao(btnTodos); break;
            case "PENDENTE": destacarBotao(btnPendente); break;
            case "PAGO": destacarBotao(btnPago); break;
        }
    }

    private void resetBotao(Button btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_700));
    }

    private void destacarBotao(Button btn) {
        btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green_700));
        btn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
    }
}
