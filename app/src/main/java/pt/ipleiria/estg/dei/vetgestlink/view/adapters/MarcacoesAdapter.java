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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Marcacao;

public class MarcacoesAdapter extends RecyclerView.Adapter<MarcacoesAdapter.MarcacaoViewHolder> {

    private Context context;
    private ArrayList<Marcacao> marcacoes;

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

        // 1. Dados Básicos
        holder.tvConsultaTitulo.setText(marcacao.getServicoNome());

        // Usamos o campo do "Veterinário" para mostrar o Animal, já que a API traz o nome do animal
        holder.tvVeterinario.setText("Animal: " + marcacao.getAnimalNome());

        String dataHora = marcacao.getData() + " às " + marcacao.getHoraInicio();
        holder.tvDataHora.setText(dataHora);

        // 2. Lógica de Observações/Diagnóstico
        if (marcacao.getDiagnostico() != null && !marcacao.getDiagnostico().isEmpty() && !marcacao.getDiagnostico().equals("null")) {
            holder.observacoesContainer.setVisibility(View.VISIBLE);
            holder.tvObservacoes.setText(marcacao.getDiagnostico());
        } else {
            holder.observacoesContainer.setVisibility(View.GONE);
        }

        // 3. Configuração Visual do Estado (Pendente, Cancelada, Realizada)
        configurarEstadoVisual(holder, marcacao.getEstado());
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
                // Certifique-se de ter um ic_check ou use android.R.drawable.checkbox_on_background
                iconResId = R.drawable.ic_check;
                break;

            case "cancelada":
                corFundo = Color.parseColor("#FDE8E8"); // Vermelho Claro
                corTextoIcone = Color.parseColor("#9B1C1C"); // Vermelho Escuro
                textoEstado = "Cancelada";
                iconResId = R.drawable.ic_uncheck; // Ícone padrão de delete/fechar
                break;

            case "pendente":
            default:
                corFundo = Color.parseColor("#FEF3C7"); // Amarelo/Laranja Claro
                corTextoIcone = Color.parseColor("#92400E"); // Castanho
                textoEstado = "Pendente";
                // Ícone de relógio (use o seu @drawable/ic_clock se tiver, senão um padrão)
                iconResId = R.drawable.ic_clock;
                break;
        }

        // Aplicar Textos e Ícones
        holder.tvBadgeStatus.setText(textoEstado);
        holder.tvBadgeStatus.setTextColor(corTextoIcone);

        // Tenta carregar o ícone (se tiver os seus drawables específicos, troque os IDs acima)
        // Se tiver o @drawable/ic_clock no projeto, use R.drawable.ic_clock no switch
        holder.ivBadgeIcon.setImageResource(iconResId);
        holder.ivBadgeIcon.setColorFilter(corTextoIcone, PorterDuff.Mode.SRC_IN);

        // Aplicar Cor de Fundo (Shape)
        Drawable background = holder.badgeStatusContainer.getBackground();
        if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(corFundo);
        } else {
            // Fallback caso o background não seja um shape editável
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

            // Observações (Assumindo que adicionou IDs dentro do include ou layout)
            observacoesContainer = itemView.findViewById(R.id.observacoesContainer);
            // Nota: O XML fornecido cortou a parte de dentro do observacoesContainer,
            // assumi que existe um TextView lá dentro. Se não tiver ID, adicione no XML.
            // Exemplo: android:id="@+id/tvObservacoesTexto"
            tvObservacoes = itemView.findViewById(R.id.tvObservacoes);
            if (tvObservacoes == null && observacoesContainer instanceof ViewGroup) {
                // Tenta encontrar qualquer TextView dentro do container se o ID não for exato
                for(int i=0; i<((ViewGroup)observacoesContainer).getChildCount(); i++){
                    View v = ((ViewGroup)observacoesContainer).getChildAt(i);
                    if(v instanceof TextView && !((TextView)v).getText().toString().contains("Observações")) {
                        tvObservacoes = (TextView) v;
                        break;
                    }
                }
            }
        }
    }
}