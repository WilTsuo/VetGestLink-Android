package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;

/**
 * Adapter para lista de Notas usando ListView (BaseAdapter)
 */
public class MainActivityAdapter extends BaseAdapter {

    private List<Nota> notas;
    private final OnNotaClickListener listener;
    private final LayoutInflater inflater;

    public interface OnNotaClickListener {
        void onEditClick(Nota nota);
        void onDeleteClick(Nota nota);
    }

    public MainActivityAdapter(Context context, OnNotaClickListener listener) {
        this.notas = new ArrayList<>();
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notas.size();
    }

    @Override
    public Object getItem(int position) {
        return notas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_nota, parent, false);
            holder = new ViewHolder();
            holder.tvTitulo = convertView.findViewById(R.id.tv_titulo);
            holder.tvDescricao = convertView.findViewById(R.id.tv_descricao);
            holder.tvData = convertView.findViewById(R.id.tv_data);
            holder.btnEditar = convertView.findViewById(R.id.btn_editar);
            holder.btnExcluir = convertView.findViewById(R.id.btn_excluir);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Nota nota = notas.get(position);
        holder.tvTitulo.setText(nota.getTitulo());
        holder.tvDescricao.setText(nota.getDescricao());
        holder.tvData.setText(nota.getData());

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(nota);
        });

        holder.btnExcluir.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(nota);
        });

        return convertView;
    }

    public void setNotas(List<Nota> notas) {
        this.notas = notas != null ? notas : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addNota(Nota nota) {
        notas.add(0, nota);
        notifyDataSetChanged();
    }

    public void updateNota(int position, Nota nota) {
        if (position >= 0 && position < notas.size()) {
            notas.set(position, nota);
            notifyDataSetChanged();
        }
    }

    public void removeNota(int position) {
        if (position >= 0 && position < notas.size()) {
            notas.remove(position);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        TextView tvTitulo;
        TextView tvDescricao;
        TextView tvData;
        ImageButton btnEditar;
        ImageButton btnExcluir;
    }
}
