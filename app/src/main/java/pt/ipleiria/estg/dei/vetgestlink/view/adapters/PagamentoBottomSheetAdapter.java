package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.vetgestlink.R;
import pt.ipleiria.estg.dei.vetgestlink.models.MetodoPagamento;

public class PagamentoBottomSheetAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MetodoPagamento> metodos;
    private int selectedPosition = -1; // -1 indica que nenhum está selecionado inicialmente

    public PagamentoBottomSheetAdapter(Context context, ArrayList<MetodoPagamento> metodos) {
        this.context = context;
        this.metodos = metodos;
    }

    @Override
    public int getCount() {
        return metodos.size();
    }

    @Override
    public Object getItem(int position) {
        return metodos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return metodos.get(position).getId();
    }

    // Método para atualizar a seleção vindo do Fragment/Activity
    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged(); // Força a lista a redesenhar para atualizar as "bolinhas"
    }

    public MetodoPagamento getSelecionado() {
        if (selectedPosition != -1 && selectedPosition < metodos.size()) {
            return metodos.get(selectedPosition);
        }
        return null;
    }

    public void clear() {
        metodos.clear();
    }

    public void addAll(ArrayList<MetodoPagamento> novosMetodos) {
        metodos.addAll(novosMetodos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_metodo_pagamento, parent, false);
        }

        MetodoPagamento metodo = metodos.get(position);

        TextView tvNome = convertView.findViewById(R.id.tvNomeMetodo);
        RadioButton rb = convertView.findViewById(R.id.rbSelecionado);
        MaterialCardView card = (MaterialCardView) convertView;

        tvNome.setText(metodo.getNome()); // Ajuste conforme o nome do atributo no seu modelo

        // Lógica da Seleção:
        // Se a posição atual for igual à selecionada, marca a bolinha
        boolean isSelected = (position == selectedPosition);
        rb.setChecked(isSelected);

        // Opcional: Mudar a cor da borda se estiver selecionado para dar destaque extra
        if (isSelected) {
            card.setStrokeColor(ContextCompat.getColor(context, R.color.primary_green)); // Use a sua cor principal
            card.setStrokeWidth(4); // Borda mais grossa
        } else {
            card.setStrokeColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            card.setStrokeWidth(1); // Borda normal
        }

        return convertView;
    }
}
