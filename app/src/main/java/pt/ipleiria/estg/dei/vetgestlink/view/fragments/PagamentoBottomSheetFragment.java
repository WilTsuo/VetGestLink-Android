package pt.ipleiria.estg.dei.vetgestlink.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.listeners.MetodosPagamentoListener;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.MetodoPagamento;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.PagamentoBottomSheetAdapter;

public class PagamentoBottomSheetFragment extends BottomSheetDialogFragment
        implements MetodosPagamentoListener {

    private int faturaId;
    private float faturaTotal;

    private TextView tvTotal;
    private ListView lvMetodos;
    private Button btnConfirmar, btnCancelar;

    // Alterado para usar o seu novo adapter
    private PagamentoBottomSheetAdapter adapter;

    public static PagamentoBottomSheetFragment newInstance(int faturaId, float total) {
        PagamentoBottomSheetFragment sheet = new PagamentoBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("fatura_id", faturaId);
        args.putFloat("fatura_total", total);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            faturaId = getArguments().getInt("fatura_id");
            faturaTotal = getArguments().getFloat("fatura_total");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.bottom_sheet_pagar,
                container,
                false
        );

        tvTotal = view.findViewById(R.id.tvTotal);
        lvMetodos = view.findViewById(R.id.lvPaymentMethods);
        btnConfirmar = view.findViewById(R.id.btnConfirmar);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        tvTotal.setText("€ " + String.format("%.2f", faturaTotal));

        // Inicializar o Adapter correto
        adapter = new PagamentoBottomSheetAdapter(
                requireContext(),
                new ArrayList<>()
        );
        lvMetodos.setAdapter(adapter);

        // --- CORREÇÃO PRINCIPAL ---
        // Configurar o clique na lista para atualizar a seleção no adapter
        lvMetodos.setOnItemClickListener((parent, view1, position, id) -> {
            adapter.setSelectedPosition(position);
        });

        // Configuração do Singleton
        Singleton singleton = Singleton.getInstance(requireContext());
        singleton.setMetodosPagamentoListener(this);

        String token = getActivity()
                .getSharedPreferences("VetGestLinkPrefs", Context.MODE_PRIVATE)
                .getString("access_token", "");

        if (!token.isEmpty()) {
            singleton.getMetodosPagamento(token, null);
        }

        btnCancelar.setOnClickListener(v -> dismiss());
        btnConfirmar.setOnClickListener(v -> confirmarPagamento());

        return view;
    }

    private void confirmarPagamento() {
        MetodoPagamento metodo = adapter.getSelecionado();

        if (metodo == null) {
            Toast.makeText(
                    getContext(),
                    "Selecione um método de pagamento",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Singleton.getInstance(requireContext())
                .pagarFatura(
                        faturaId,
                        metodo.getId(),
                        requireContext(),
                        new Singleton.MessageCallback() {
                            @Override
                            public void onSuccess(String message) {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
    }

    @Override
    public void onRefreshMetodosPagamento(ArrayList<MetodoPagamento> metodos) {
        Log.d("METODOS_PAGAMENTO", "Recebidos: " + metodos.size());
        adapter.clear();
        adapter.addAll(metodos);
        adapter.notifyDataSetChanged();
    }
}
