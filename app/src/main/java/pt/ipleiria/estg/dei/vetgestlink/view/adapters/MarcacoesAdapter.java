package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;

public class MarcacoesAdapter extends RecyclerView.Adapter<MarcacoesAdapter.MarcacaoViewHolder> {

    private Context context;
    private ArrayList<Marcacao> marcacoes;
    private OnItemClickListener listener;

    // 1. Interface para gerir o clique
    public interface OnItemClickListener {
        void onItemClick(Marcacao marcacao);
    }

    // 2. Método Setter para o Listener (chamado no Fragment)
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MarcacoesAdapter(Context context, ArrayList<Marcacao> marcacoes) {
        this.context = context;
        this.marcacoes = marcacoes;
    }

    @NonNull
    @Override
    public MarcacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_marcacao_card, parent, false);
        return new MarcacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarcacaoViewHolder holder, int position) {
        Marcacao marcacao = marcacoes.get(position);

        // --- Configuração dos Dados ---
        holder.tvConsultaTitulo.setText(marcacao.getServicoNome() != null ? marcacao.getServicoNome() : "Serviço");

        // Exibe o nome do animal
        String animalInfo = marcacao.getAnimalNome() != null ? marcacao.getAnimalNome() : "N/A";
        holder.tvVeterinario.setText("Paciente: " + animalInfo);

        // Formatação Data/Hora
        String dataHora = marcacao.getData() + " | " + marcacao.getHoraInicio();
        holder.tvDataHora.setText(dataHora);

        // --- Lógica de Observações ---
        String diag = marcacao.getDiagnostico();
        if (diag != null && !diag.isEmpty() && !diag.equals("null")) {
            holder.observacoesContainer.setVisibility(View.VISIBLE);
            if (holder.tvObservacoes != null) {
                holder.tvObservacoes.setText(diag);
            }
        } else {
            holder.observacoesContainer.setVisibility(View.GONE);
        }

        // --- Configuração Visual do Estado ---
        configurarEstadoVisual(holder, marcacao.getEstado());

        // --- EVENTO DE CLIQUE (CRUCIAL) ---
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(marcacao);
            }
        });
    }

    private void configurarEstadoVisual(MarcacaoViewHolder holder, String estado) {
        String estadoLower = estado != null ? estado.toLowerCase() : "";

        int corFundo;
        int corTextoIcone;
        String textoEstado;
        int iconResId;

        switch (estadoLower) {
            case "realizada":
            case "concluida":
                corFundo = Color.parseColor("#DEF7EC"); // Verde Claro
                corTextoIcone = Color.parseColor("#03543F"); // Verde Escuro
                textoEstado = "Realizada";
                iconResId = R.drawable.ic_check; // Certifique-se que este ícone existe
                break;

            case "cancelada":
                corFundo = Color.parseColor("#FDE8E8"); // Vermelho Claro
                corTextoIcone = Color.parseColor("#9B1C1C"); // Vermelho Escuro
                textoEstado = "Cancelada";
                iconResId = R.drawable.ic_uncheck; // Certifique-se que este ícone existe
                break;

            case "pendente":
            default:
                corFundo = Color.parseColor("#FEF3C7"); // Amarelo/Laranja Claro
                corTextoIcone = Color.parseColor("#92400E"); // Castanho
                textoEstado = "Pendente";
                iconResId = R.drawable.ic_clock; // Certifique-se que este ícone existe
                break;
        }

        // Aplicar Textos e Ícones
        holder.tvBadgeStatus.setText(textoEstado);
        holder.tvBadgeStatus.setTextColor(corTextoIcone);

        // Configurar Ícone
        try {
            holder.ivBadgeIcon.setImageResource(iconResId);
            holder.ivBadgeIcon.setColorFilter(corTextoIcone, PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            // Fallback caso o ícone não exista
            holder.ivBadgeIcon.setVisibility(View.GONE);
        }

        // Aplicar Cor de Fundo (Shape)
        Drawable background = holder.badgeStatusContainer.getBackground();
        if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(corFundo);
        } else {
            holder.badgeStatusContainer.setBackgroundColor(corFundo);
        }
    }

    @Override
    public int getItemCount() {
        return marcacoes != null ? marcacoes.size() : 0;
    }

    public void updateList(ArrayList<Marcacao> novaLista) {
        this.marcacoes = novaLista;
        notifyDataSetChanged();
    }

    public static class MarcacaoViewHolder extends RecyclerView.ViewHolder {
        TextView tvConsultaTitulo, tvVeterinario, tvDataHora, tvBadgeStatus, tvObservacoes;
        ImageView ivBadgeIcon;
        View badgeStatusContainer, observacoesContainer;

        public MarcacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvConsultaTitulo = itemView.findViewById(R.id.tvConsultaTitulo);
            tvVeterinario = itemView.findViewById(R.id.tvVeterinario);
            tvDataHora = itemView.findViewById(R.id.tvDataHora);

            // Badge Status
            badgeStatusContainer = itemView.findViewById(R.id.badgeStatus);
            tvBadgeStatus = itemView.findViewById(R.id.tvBadgeStatus);
            ivBadgeIcon = itemView.findViewById(R.id.ivBadgeIcon);

            // Observações
            observacoesContainer = itemView.findViewById(R.id.observacoesContainer);
            tvObservacoes = itemView.findViewById(R.id.tvObservacoes);

            // Fallback simples se o ID tvObservacoes não for encontrado diretamente
            if (tvObservacoes == null && observacoesContainer instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) observacoesContainer;
                for (int i = 0; i < group.getChildCount(); i++) {
                    View v = group.getChildAt(i);
                    if (v instanceof TextView) {
                        tvObservacoes = (TextView) v;
                        break;
                    }
                }
            }
        }
    }
}
