package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;

/**
 * Adapter para lista de Notas dos Animais
 */
public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.NotaViewHolder> {

    private List<Nota> notas;
    private OnNotaClickListener listener;

    public interface OnNotaClickListener {
        void onEditClick(Nota nota);
        void onDeleteClick(Nota nota);
    }

    public NotasAdapter(OnNotaClickListener listener) {
        this.notas = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nota, parent, false);
        return new NotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        Nota nota = notas.get(position);
        holder.bind(nota);
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    public void setNotas(List<Nota> notas) {
        this.notas = notas;
        notifyDataSetChanged();
    }

    public void addNota(Nota nota) {
        notas.add(0, nota);
        notifyItemInserted(0);
    }

    public void updateNota(int position, Nota nota) {
        notas.set(position, nota);
        notifyItemChanged(position);
    }

    public void removeNota(int position) {
        notas.remove(position);
        notifyItemRemoved(position);
    }

    class NotaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvDescricao;
        TextView tvData;
        ImageButton btnEditar;
        ImageButton btnExcluir;

        public NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvDescricao = itemView.findViewById(R.id.tv_descricao);
            tvData = itemView.findViewById(R.id.tv_data);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnExcluir = itemView.findViewById(R.id.btn_excluir);
        }

        public void bind(Nota nota) {
            tvTitulo.setText(nota.getTitulo());
            tvDescricao.setText(nota.getDescricao());
            tvData.setText(nota.getData());

            btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(nota);
                }
            });

            btnExcluir.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(nota);
                }
            });
        }
    }
}

