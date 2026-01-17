package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.LinhaFatura;

public class LinhasFaturaAdapter extends RecyclerView.Adapter<LinhasFaturaAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LinhaFatura> linhas;

    public LinhasFaturaAdapter(Context context, ArrayList<LinhaFatura> linhas) {
        this.context = context;
        this.linhas = linhas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_linha_fatura, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinhaFatura linha = linhas.get(position);
        holder.tvDescricao.setText(linha.getDescricao());

        // Formato: 2 x €12.50
        String qtdPreco = linha.getQuantidade() + " x €" + String.format("%.2f", linha.getPrecoUnitario());
        holder.tvQtdPreco.setText(qtdPreco);

        holder.tvTotal.setText("€ " + String.format("%.2f", linha.getTotal()));
    }

    @Override
    public int getItemCount() {
        return linhas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescricao, tvQtdPreco, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescricao = itemView.findViewById(R.id.tvDescricaoLinha);
            tvQtdPreco = itemView.findViewById(R.id.tvQtdPreco);
            tvTotal = itemView.findViewById(R.id.tvTotalLinha);
        }
    }
}
