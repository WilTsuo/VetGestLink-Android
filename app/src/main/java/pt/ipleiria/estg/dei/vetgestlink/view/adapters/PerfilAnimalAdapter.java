package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;
import pt.ipleiria.estg.dei.vetgestlink.utils.Singleton;

public class PerfilAnimalAdapter extends RecyclerView.Adapter<PerfilAnimalAdapter.ViewHolderAnimal> {

    private List<Animal> animais;

    public PerfilAnimalAdapter(List<Animal> animais) {
        this.animais = animais;
    }

    @NonNull
    @Override
    public ViewHolderAnimal onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal, parent, false);
        return new ViewHolderAnimal(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAnimal holder, int position) {
        if (animais == null || position < 0 || position >= animais.size()) return;

        Animal animal = animais.get(position);

        holder.tvNome.setText(animal.getNome() != null ? animal.getNome() : "Sem Nome");

        String racaTexto = (animal.getEspecie() != null ? animal.getEspecie() : "") +
                " - " +
                (animal.getRaca() != null ? animal.getRaca() : "");
        holder.tvRacaHeader.setText(racaTexto);

        holder.tvIdade.setText(animal.getIdade() != null ? animal.getIdade() : "-");
        holder.tvPeso.setText(String.format("%.1f kg", animal.getPeso()));
        holder.tvEspecie.setText(animal.getEspecie() != null ? animal.getEspecie() : "-");

        String sexo = "N/A";
        if (animal.getSexo() != null) {
            sexo = animal.getSexo().equalsIgnoreCase("M") ? "Macho" : "Fêmea";
        }
        holder.tvGenero.setText(sexo);

        String temMicrochip = (animal.getMicrochip() == 1) ? "Sim" : "Não";
        holder.tvMicrochip.setText(temMicrochip);

        // CORREÇÃO: Obter URL base do Singleton em vez de hardcoded
        String baseUrl = Singleton.getInstance(holder.itemView.getContext()).getUrlFrontend();
        String caminhoFoto = (animal.getFotoUrl() != null) ? animal.getFotoUrl() : "";

        // Garante que não ficamos com barras duplas ou sem barra
        if (!caminhoFoto.startsWith("/") && !baseUrl.endsWith("/")) {
            caminhoFoto = "/" + caminhoFoto;
        }

        String urlCompleta = baseUrl + caminhoFoto;

        Glide.with(holder.itemView.getContext())
                .load(urlCompleta)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivFoto);
    }

    @Override
    public int getItemCount() {
        return animais != null ? animais.size() : 0;
    }

    public static class ViewHolderAnimal extends RecyclerView.ViewHolder {
        TextView tvNome, tvRacaHeader, tvMicrochip, tvIdade, tvPeso, tvEspecie, tvGenero;
        ImageView ivFoto;

        public ViewHolderAnimal(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvAnimalName);
            tvRacaHeader = itemView.findViewById(R.id.tvAnimalBreed);
            tvMicrochip = itemView.findViewById(R.id.tvMicrochip);
            ivFoto = itemView.findViewById(R.id.ivAnimal);
            tvIdade = itemView.findViewById(R.id.tvAge);
            tvPeso = itemView.findViewById(R.id.tvWeight);
            tvEspecie = itemView.findViewById(R.id.tvSpecies);
            tvGenero = itemView.findViewById(R.id.tvGender);
        }
    }
}
