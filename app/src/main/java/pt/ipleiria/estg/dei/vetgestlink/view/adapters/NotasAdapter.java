package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;

public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.NotaViewHolder> {

    private List<Nota> notas;

    public NotasAdapter(List<Nota> notas) {
        this.notas = notas;
    }

    public void updateList(List<Nota> novasNotas) {
        this.notas = novasNotas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false);
        return new NotaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        Nota n = notas.get(position);
        // Ajuste de acordo com a classe Nota: tenta mostrar título se existir, caso contrário usa a propriedade 'nota'
        try {
            holder.tvTitulo.setText(n.getTitulo() != null ? n.getTitulo() : "");
        } catch (NoSuchMethodError | Exception e) {
            holder.tvTitulo.setText("");
        }
        try {
            holder.tvDescricao.setText(n.getNota() != null ? n.getNota() : "");
        } catch (NoSuchMethodError | Exception e) {
            holder.tvDescricao.setText("");
        }
        try {
            holder.tvData.setText(n.getData() != null ? n.getData() : "");
        } catch (NoSuchMethodError | Exception e) {
            holder.tvData.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return notas != null ? notas.size() : 0;
    }

    static class NotaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvDescricao;
        TextView tvData;
        NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvDescricao = itemView.findViewById(R.id.tv_descricao);
            tvData = itemView.findViewById(R.id.tv_data);
        }
    }
}
