package com.example.lab2;

import android.content.Context;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab2.databinding.ItemEquipoBinding;

import java.util.List;

public class EquipoAdapter extends RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder> {

    public interface AdapterListener {
        void onEditar(int posicion);
        void onEliminar(int posicion);
    }

    private List<Equipo> listaEquipos;
    private Context contexto;
    private AdapterListener listener;

    public EquipoAdapter(Context contexto, List<Equipo> listaEquipos, AdapterListener listener) {
        this.contexto = contexto;
        this.listaEquipos = listaEquipos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEquipoBinding binding = ItemEquipoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EquipoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
        Equipo equipo = listaEquipos.get(position);

        holder.binding.textCodigo.setText("Código: " + equipo.getCodigo());
        holder.binding.textNombre.setText("Nombre del equipo: " + equipo.getNombre());
        holder.binding.textTipo.setText("Tipo de equipo: " + equipo.getTipo());
        holder.binding.textEstado.setText("Estado: " + equipo.getEstado());

        // Color según estado
        switch (equipo.getEstado()) {
            case "Operativo":
                holder.binding.textEstado.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "En mantenimiento":
                holder.binding.textEstado.setTextColor(Color.parseColor("#F9A825"));
                break;
            case "Fuera de servicio":
                holder.binding.textEstado.setTextColor(Color.parseColor("#C62828"));
                break;
            default:
                holder.binding.textEstado.setTextColor(Color.BLACK);
                break;
        }

        // Long click → Context Action Bar
        holder.itemView.setOnLongClickListener(v -> {
            ActionMode.Callback callback = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.setTitle("Seleccionar acción");
                    menu.add(0, 1, 0, "Editar");
                    menu.add(0, 2, 1, "Eliminar");
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int pos = holder.getAdapterPosition();
                    if (item.getItemId() == 1) {
                        listener.onEditar(pos);
                    } else if (item.getItemId() == 2) {
                        listener.onEliminar(pos);
                    }
                    mode.finish();
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) { }
            };

            if (contexto instanceof android.app.Activity) {
                ((android.app.Activity) contexto).startActionMode(callback);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaEquipos.size();
    }

    public static class EquipoViewHolder extends RecyclerView.ViewHolder {
        ItemEquipoBinding binding;

        public EquipoViewHolder(ItemEquipoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}