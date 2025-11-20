// app/src/main/java/pt/ipleiria/estg/dei/vetgestlink/view/adapters/MenuAdapter.java
package pt.ipleiria.estg.dei.vetgestlink.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MenuAdapter extends ArrayAdapter<String> {
    private final LayoutInflater inflater;

    public MenuAdapter(@NonNull Context context, List<String> menuItems) {
        super(context, android.R.layout.simple_list_item_1, menuItems);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        String item = getItem(position);
        textView.setText(item != null ? item : "");
        return convertView;
    }
}
