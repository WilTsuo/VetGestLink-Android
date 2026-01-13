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
import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;
import pt.ipleiria.estg.dei.vetgestlink.models.MetodoPagamento;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;
import pt.ipleiria.estg.dei.vetgestlink.view.adapters.MetodosPagamentoAdapter;

public class PagamentoBottomSheet extends BottomSheetDialogFragment
        implements MetodosPagamentoListener {

    private int faturaId;
    private Fatura fatura;
    private float faturaTotal;


    private TextView tvTotal;
    private ListView lvMetodos;
    private Button btnConfirmar, btnCancelar;
    private static final String ARG_FATURA_ID = "fatura_id";

    private MetodosPagamentoAdapter adapter;


    public static PagamentoBottomSheet newInstance(int faturaId, float total) {
        PagamentoBottomSheet sheet = new PagamentoBottomSheet();
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

        tvTotal.setText("€ " + String.format("%.2f", faturaTotal)
        );

        adapter = new MetodosPagamentoAdapter(
                requireContext(),
                new ArrayList<>()
        );

        lvMetodos.setAdapter(adapter);

        Singleton singleton = Singleton.getInstance(requireContext());
        singleton.setMetodosPagamentoListener(this);
        String token = getActivity()
                .getSharedPreferences("VetGestLinkPrefs", Context.MODE_PRIVATE)
                .getString("access_token", "");
        if (!token.isEmpty()) {
            singleton.getMetodosPagamento(token);
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
                        metodo.getId()
                );

        dismiss();
    }

    @Override
    public void onRefreshMetodosPagamento(ArrayList<MetodoPagamento> metodos) {
        Log.d("METODOS_PAGAMENTO", "Recebidos: " + metodos.size());

        adapter.clear();
        adapter.addAll(metodos);
        adapter.notifyDataSetChanged();
    }
}
