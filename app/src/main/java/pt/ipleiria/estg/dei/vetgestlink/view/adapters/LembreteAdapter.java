
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
import pt.ipleiria.estg.dei.vetgestlink.models.Lembrete;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;

public class LembreteAdapter extends RecyclerView.Adapter<LembreteAdapter.LembreteViewHolder> {

    private List<Lembrete> lembretes;
    private int currentUserId;
    private OnLembreteChangedListener listener;

    private static final String PREFS_NAME = "VetGestLinkPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    public interface OnLembreteChangedListener {
        void onLembreteChanged();
    }

    public LembreteAdapter(List<Lembrete> lembretes, int currentUserId) {
        this.lembretes = lembretes;
        this.currentUserId = currentUserId;
    }

    public void setOnLembreteChangedListener(OnLembreteChangedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LembreteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lembrete, parent, false);
        return new LembreteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LembreteViewHolder holder, int position) {
        Lembrete lembrete = lembretes.get(position);

        holder.tvDescricao.setText(lembrete.getDescricao() != null ? lembrete.getDescricao() : "");
        holder.tvData.setText(lembrete.getCreatedAt() != null ? lembrete.getCreatedAt() : "");

        holder.btnEditar.setOnClickListener(v -> showEditDialog(holder.itemView.getContext(), lembrete));
        holder.btnExcluir.setOnClickListener(v -> showDeleteDialog(holder.itemView.getContext(), lembrete));
    }

    @Override
    public int getItemCount() {
        return lembretes != null ? lembretes.size() : 0;
    }

    private void showEditDialog(Context context, Lembrete lembrete) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_nota, null);

        TextInputEditText etDescricao = dialogView.findViewById(R.id.et_descricao);
        etDescricao.setText(lembrete.getDescricao());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Editar Lembrete")
                .setNegativeButton("Cancelar", null)
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
            Singleton.getInstance(context).atualizarLembrete(token, lembrete.getId(), descricao, new Singleton.MessageCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(context, "Lembrete atualizado", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onLembreteChanged();
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

    private void showDeleteDialog(Context context, Lembrete lembrete) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar Lembrete")
                .setMessage("Tem certeza que deseja eliminar este lembrete?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    String token = getAccessToken(context);
                    Singleton.getInstance(context).deletarLembrete(token, lembrete.getId(), new Singleton.MessageCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(context, "Lembrete eliminado", Toast.LENGTH_SHORT).show();
                            if (listener != null) {
                                listener.onLembreteChanged();
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
        return prefs.getString(KEY_ACCESS_TOKEN, "");
    }

    static class LembreteViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescricao;
        TextView tvData;
        ImageButton btnEditar;
        ImageButton btnExcluir;

        LembreteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescricao = itemView.findViewById(R.id.tv_descricao);
            tvData = itemView.findViewById(R.id.tv_data);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnExcluir = itemView.findViewById(R.id.btn_excluir);
        }
    }
}
