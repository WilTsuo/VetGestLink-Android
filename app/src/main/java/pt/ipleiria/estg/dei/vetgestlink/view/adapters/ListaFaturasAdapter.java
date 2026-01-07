package pt.ipleiria.estg.dei.vetgestlink.view.adapters;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.model.Fatura;
public class ListaFaturasAdapter extends ArrayAdapter<Fatura> {
    private Context context;
    private ArrayList<Fatura> faturas;

    public ListaFaturasAdapter(Context context, ArrayList<Fatura> faturas) {
        super(context, 0, faturas);
        this.context = context;
        this.faturas = faturas;
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
        TextView tvDataEmissao = convertView.findViewById(R.id.tvIssueDate);
        Button btnPagar = convertView.findViewById(R.id.btnPay);

        tvNumero.setText("Fatura #" + fatura.getId());
        tvDescricao.setText("Itens: " + fatura.getNumeroItens());
        tvTotal.setText("€ " + String.format("%.2f", fatura.getTotal()));
        tvDataEmissao.setText("Emitida: " + fatura.getCreatedAt());

        if (fatura.isEstado()) { // PAGA
            tvEstado.setText("Pago");
            tvEstado.setTextColor(Color.parseColor("#2E7D32"));
            btnPagar.setVisibility(View.GONE);
        } else { // PENDENTE
            tvEstado.setText("Pendente");
            tvEstado.setTextColor(Color.parseColor("#FBC02D"));
            btnPagar.setVisibility(View.VISIBLE);
        }

        btnPagar.setOnClickListener(v -> {
            // 👉 Aqui depois abres o BottomSheetDialog
            // passando a fatura selecionada
        });

        return convertView;
    }
}
