package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.model.MetodoPagamento;

public class MetodosPagamentoAdapter extends ArrayAdapter<MetodoPagamento> {

    private Context context;
    private ArrayList<MetodoPagamento> metodos;
    private int selectedPosition = -1;

    public MetodosPagamentoAdapter(
            Context context,
            ArrayList<MetodoPagamento> metodos
    ) {
        super(context, 0, metodos);
        this.context = context;
        this.metodos = metodos;
    }

    public MetodoPagamento getSelecionado() {
        if (selectedPosition == -1) return null;
        return metodos.get(selectedPosition);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_metodo_pagamento, parent, false);
        }

        MetodoPagamento metodo = metodos.get(position);

        TextView tvNome = convertView.findViewById(R.id.tvNomeMetodo);

        tvNome.setText(metodo.getNome());

        if (position == selectedPosition) {
            convertView.setBackgroundColor(
                    Color.parseColor("#E8F5E9")
            );
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        convertView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

        return convertView;
    }
}
