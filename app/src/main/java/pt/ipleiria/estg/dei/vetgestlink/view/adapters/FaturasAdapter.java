package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.listeners.OnPagarClickListener;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Fatura;

public class FaturasAdapter extends ArrayAdapter<Fatura> {

    private Context context;
    private ArrayList<Fatura> faturas;
    private OnPagarClickListener listener;
    private boolean apiAvailable = true;

    public FaturasAdapter(Context context, ArrayList<Fatura> faturas, OnPagarClickListener listener){
        super(context, 0, faturas);
        this.context = context;
        this.faturas = faturas;
        this.listener = listener;
    }

    // setter pra atualizar disponibilidade da API em runtime
    public void setApiAvailable(boolean apiAvailable) {
        this.apiAvailable = apiAvailable;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_fatura, parent, false);
        }

        Fatura fatura = faturas.get(position);

        // Referências aos elementos do layout
        TextView tvNumero = convertView.findViewById(R.id.tvInvoiceNumber);
        TextView tvDescricao = convertView.findViewById(R.id.tvDescription);
        TextView tvTotal = convertView.findViewById(R.id.tvAmount);
        TextView tvMetodo = convertView.findViewById(R.id.tvMetodoPagamento);
        TextView tvData = convertView.findViewById(R.id.tvCreatedAt);
        Button btnPagar = convertView.findViewById(R.id.btnPagar);

        // Elementos do Badge de Status
        ConstraintLayout badgeContainer = convertView.findViewById(R.id.badgeStatusContainer);
        ImageView ivIcon = convertView.findViewById(R.id.ivStatusIcon);
        TextView tvEstado = convertView.findViewById(R.id.tvStatus);

        // Preenchimento dos dados básicos
        tvNumero.setText("Fatura #" + fatura.getId());
        tvDescricao.setText("Linhas na fatura: " + fatura.getNumeroItens());
        tvTotal.setText("€ " + String.format("%.2f", fatura.getTotal()));
        tvData.setText("Emitida: " + fatura.getCreatedAt());

        // Lógica de Estado (Pago vs Pendente)
        if (fatura.isEstado()) {
            // --- ESTADO: PAGO (Verde) ---
            tvEstado.setText("Pago");

            int corVerde = Color.parseColor("#2E7D32");
            tvEstado.setTextColor(corVerde);
            ivIcon.setColorFilter(corVerde);

            // Ícone de check (certifique-se que tem o drawable ic_check ou use ic_clock)
            ivIcon.setImageResource(R.drawable.ic_check);

            // Fundo Verde
            badgeContainer.setBackgroundResource(R.drawable.badge_pago_bg);

            tvMetodo.setText("Método de pagamento: " + fatura.getMetodoPagamento());
            tvMetodo.setVisibility(View.VISIBLE);
            btnPagar.setVisibility(View.GONE);

        } else {
            // --- ESTADO: PENDENTE (Azul - Estilo Marcação) ---
            tvEstado.setText("Pendente");

            int corAzul = Color.parseColor("#193CB8");
            tvEstado.setTextColor(corAzul);
            ivIcon.setColorFilter(corAzul);

            // Ícone de relógio
            ivIcon.setImageResource(R.drawable.ic_clock);

            // Fundo Azul/Padrão
            badgeContainer.setBackgroundResource(R.drawable.badge_pendente_bg);

            tvMetodo.setVisibility(View.GONE);
            btnPagar.setVisibility(View.VISIBLE);

            // aplica estado (enabled + alpha) conforme disponibilidade da API
            btnPagar.setEnabled(apiAvailable);
            btnPagar.setAlpha(apiAvailable ? 1f : 0.5f);

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
