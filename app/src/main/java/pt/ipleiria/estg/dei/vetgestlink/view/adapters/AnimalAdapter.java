package pt.ipleiria.estg.dei.vetgestlink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Animal;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    private List<Animal> animais;

    public AnimalAdapter(List<Animal> animais) {
        this.animais = animais;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        Animal animal = animais.get(position);
        holder.tvNome.setText(animal.getNome());
        holder.tvRaca.setText(animal.getRaca());
        holder.tvIdade.setText(animal.getIdade() + " anos");
        holder.tvPeso.setText(animal.getPeso() + " kg");
        holder.tvEspecie.setText(animal.getEspecie());
        holder.tvGenero.setText(animal.getSexo().equals("M") ? "Macho" : "Fêmea");
        holder.tvMicrochip.setText(animal.getMicrochip() == 1 ? "Sim" : "Não");
    }

    @Override
    public int getItemCount() {
        return animais.size();
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvRaca, tvIdade, tvPeso, tvEspecie, tvGenero, tvMicrochip;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvAnimalName);
            tvRaca = itemView.findViewById(R.id.tvAnimalBreed);
            tvIdade = itemView.findViewById(R.id.tvAge);
            tvPeso = itemView.findViewById(R.id.tvWeight);
            tvEspecie = itemView.findViewById(R.id.tvSpecies);
            tvGenero = itemView.findViewById(R.id.tvGender);
            tvMicrochip = itemView.findViewById(R.id.tvMicrochip);
        }
    }
}
