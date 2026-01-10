package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.model.Nota;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;

public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.NotaViewHolder> {

    private List<Nota> notas;
    private int currentUserId;
    private OnNotaChangedListener listener;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    public interface OnNotaChangedListener {
        void onNotaChanged();
    }

    public NotasAdapter(List<Nota> notas, int currentUserId) {
        this.notas = notas;
        this.currentUserId = currentUserId;
    }

    public void setOnNotaChangedListener(OnNotaChangedListener listener) {
        this.listener = listener;
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

        try {
            holder.tvTitulo.setText(n.getTitulo() != null ? n.getTitulo() : "");
        } catch (Exception e) {
            holder.tvTitulo.setText("");
        }
        try {
            holder.tvDescricao.setText(n.getNota() != null ? n.getNota() : "");
        } catch (Exception e) {
            holder.tvDescricao.setText("");
        }
        try {
            holder.tvData.setText(n.getData() != null ? n.getData() : "");
        } catch (Exception e) {
            holder.tvData.setText("");
        }

        boolean isOwner = n.getUserprofileId() == currentUserId;
        holder.btnEditar.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        holder.btnExcluir.setVisibility(isOwner ? View.VISIBLE : View.GONE);

        if (isOwner) {
            holder.btnEditar.setOnClickListener(v -> showEditDialog(holder.itemView.getContext(), n));
            holder.btnExcluir.setOnClickListener(v -> showDeleteDialog(holder.itemView.getContext(), n));
        }
    }

    @Override
    public int getItemCount() {
        return notas != null ? notas.size() : 0;
    }

    private void showEditDialog(Context context, Nota nota) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_nota, null);

        TextInputEditText etDescricao = dialogView.findViewById(R.id.et_descricao);
        etDescricao.setText(nota.getNota());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Editar Nota")
                .setNegativeButton("Cancelar", (d, which) -> {})
                .setPositiveButton("Guardar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setOnClickListener(v -> {
            String descricao = etDescricao.getText() != null ? etDescricao.getText().toString().trim() : "";

            if (descricao.isEmpty()) {
                etDescricao.setError("Campo obrigatório");
                return;
            }

            String token = getAccessToken(context);
            Singleton.getInstance(context).atualizarNota(token, nota.getId(), descricao, new Singleton.MessageCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(context, "Nota atualizada", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onNotaChanged();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, "Erro: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showDeleteDialog(Context context, Nota nota) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar Nota")
                .setMessage("Tem certeza que deseja eliminar esta nota?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    String token = getAccessToken(context);
                    Singleton.getInstance(context).deletarNota(token, nota.getId(), new Singleton.MessageCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show();
                            if (listener != null) {
                                listener.onNotaChanged();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(context, "Erro: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    static class NotaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvDescricao;
        TextView tvData;
        ImageButton btnEditar;
        ImageButton btnExcluir;

        NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvDescricao = itemView.findViewById(R.id.tv_descricao);
            tvData = itemView.findViewById(R.id.tv_data);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnExcluir = itemView.findViewById(R.id.btn_excluir);
        }
    }
}
