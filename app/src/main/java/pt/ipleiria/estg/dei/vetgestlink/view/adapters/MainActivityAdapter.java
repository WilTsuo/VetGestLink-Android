package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.Nota;

public class MainActivityAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Nota> notas;

    public MainActivityAdapter(Context context, ArrayList<Nota> notas) {
        this.context = context;
        this.notas = notas;
    }

    @Override
    public int getCount() {
        return notas.size();
    }

    @Override
    public Object getItem(int i) {
        return notas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return notas.get(i).getId(); // or i if you don’t have id yet
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view == null) {
            view = inflater.inflate(R.layout.item_nota, null);
        }

        ViewHolderNota viewHolder = (ViewHolderNota) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolderNota(view);
            view.setTag(viewHolder);
        }

        viewHolder.update(notas.get(i));
        return view;
    }


    // ViewHolder
    private class ViewHolderNota {

        private TextView tvTitulo, tvDescricao, tvData;
        private ImageButton btnEditar, btnExcluir;

        public ViewHolderNota(View view) {
            tvTitulo = view.findViewById(R.id.tv_titulo);
            tvDescricao = view.findViewById(R.id.tv_descricao);
            tvData = view.findViewById(R.id.tv_data);
            btnEditar = view.findViewById(R.id.btn_editar);
            btnExcluir = view.findViewById(R.id.btn_excluir);
        }


        public void update(Nota nota) {
            tvTitulo.setText(nota.getTitulo());
            tvDescricao.setText(nota.getNota());
            tvData.setText(nota.getCreatedAt());


            btnEditar.setOnClickListener(v ->
                    Toast.makeText(context,
                            "Editar: " + nota.getTitulo(),
                            Toast.LENGTH_SHORT).show()
            );

            btnExcluir.setOnClickListener(v ->
                    Toast.makeText(context,
                            "Excluir: " + nota.getTitulo(),
                            Toast.LENGTH_SHORT).show()
            );
        }
    }
}
