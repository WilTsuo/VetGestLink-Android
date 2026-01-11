package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;

public class PerfilAnimalAdapter extends RecyclerView.Adapter<PerfilAnimalAdapter.ViewHolderAnimal> {

    private List<Animal> animais;
    private static final String BASE_URL_IMAGENS = "http://172.22.21.220/frontend/web";

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
        Animal animal = animais.get(position);

        // Dados principais
        holder.tvNome.setText(animal.getNome());
        holder.tvRacaHeader.setText(animal.getEspecie() + " - " + animal.getRaca());

        // Novos campos baseados na imagem
        holder.tvIdade.setText(animal.getIdade() + " anos");
        holder.tvPeso.setText(animal.getPeso() + " kg");
        holder.tvEspecie.setText(animal.getEspecie());
        holder.tvGenero.setText(animal.getSexo());

        String temMicrochip = (animal.getMicrochip() == 1) ? "Sim" : "Não";
        holder.tvMicrochip.setText("Microchip: " + temMicrochip);

        // Imagem
        String urlImagem = BASE_URL_IMAGENS + animal.getFotoUrl();
        Glide.with(holder.itemView.getContext())
                .load(urlImagem)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.ivFoto);
    }

    @Override
    public int getItemCount() {
        return animais.size();
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

            // IDs para os novos campos (verifique se coincidem com o seu XML)
            tvIdade = itemView.findViewById(R.id.tvAge);
            tvPeso = itemView.findViewById(R.id.tvWeight);
            tvEspecie = itemView.findViewById(R.id.tvSpecies);
            tvGenero = itemView.findViewById(R.id.tvGender);
        }
    }
}