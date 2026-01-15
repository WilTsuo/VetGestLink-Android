package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.listeners.OnPagarClickListener;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;

public class ListaFaturasAdapter extends ArrayAdapter<Fatura> {

    private Context context;
    private ArrayList<Fatura> faturas;
    private OnPagarClickListener listener;

    public ListaFaturasAdapter(
            Context context,
            ArrayList<Fatura> faturas,
            OnPagarClickListener listener
    ) {
        super(context, 0, faturas);
        this.context = context;
        this.faturas = faturas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_fatura, parent, false);
        }

        Fatura fatura = faturas.get(position);

        TextView tvNumero = convertView.findViewById(R.id.tvInvoiceNumber);
        TextView tvDescricao = convertView.findViewById(R.id.tvDescription);
        TextView tvTotal = convertView.findViewById(R.id.tvAmount);
        TextView tvEstado = convertView.findViewById(R.id.tvStatus);
        TextView tvMetodo = convertView.findViewById(R.id.tvMetodoPagamento);
        TextView tvData = convertView.findViewById(R.id.tvCreatedAt);
        Button btnPagar = convertView.findViewById(R.id.btnPay);

        tvNumero.setText("Fatura #" + fatura.getId());
        tvDescricao.setText("Itens: " + fatura.getNumeroItens());
        tvTotal.setText("€ " + String.format("%.2f", fatura.getTotal()));
        tvMetodo.setText("Método de pagamento: " + fatura.getMetodoPagamento());
        tvData.setText("Emitida: " + fatura.getCreatedAt());

        if (fatura.isEstado()) {
            tvEstado.setText("Pago");
            tvEstado.setTextColor(Color.parseColor("#2E7D32"));
            btnPagar.setVisibility(View.GONE);
            tvMetodo.setText("Método de pagamento: " + fatura.getMetodoPagamento());
            tvMetodo.setVisibility(View.VISIBLE);
        } else {
            tvEstado.setText("Pendente");
            tvEstado.setTextColor(Color.parseColor("#FBC02D"));
            btnPagar.setVisibility(View.VISIBLE);
            tvMetodo.setVisibility(View.GONE);

            btnPagar.setOnClickListener(v -> {
                Log.d("PAGAR_CLICK", "Botão pagar clicado: " + fatura.getId());
                if (listener != null) {
                    listener.onPagarClick(fatura);
                }
            });
        }

        return convertView;
    }
}
