package com.example.lab2;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.databinding.ActivityMainBinding;
import com.example.lab2.databinding.ItemEquipoBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public static List<Equipo> listaEquipos = new ArrayList<>();

    private List<Equipo> listaFiltrada = new ArrayList<>();

    private String filtroTipo = "Todos";
    private String filtroEstado = "Todos";

    private static final int CODIGO_FORM = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarSpinnersFiltro();
        aplicarFiltros();

        binding.fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FormActivity.class);
            startActivityForResult(intent, CODIGO_FORM);
        });
    }

    private void configurarSpinnersFiltro() {
        List<String> opcionesTipo = new ArrayList<>();
        opcionesTipo.add("Todos");
        opcionesTipo.add("Multímetro");
        opcionesTipo.add("OTDR");
        opcionesTipo.add("Medidor de potencia");
        opcionesTipo.add("Analizador de espectro");
        opcionesTipo.add("Pinza amperimétrica");

        ArrayAdapter<String> adaptadorTipo = new ArrayAdapter<>(this,
                R.layout.spinner_item, opcionesTipo);
        adaptadorTipo.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerFiltroTipo.setAdapter(adaptadorTipo);

        List<String> opcionesEstado = new ArrayList<>();
        opcionesEstado.add("Todos");
        opcionesEstado.add("Operativo");
        opcionesEstado.add("En mantenimiento");
        opcionesEstado.add("Fuera de servicio");

        ArrayAdapter<String> adaptadorEstado = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesEstado);
        adaptadorEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFiltroEstado.setAdapter(adaptadorEstado);

        binding.spinnerFiltroTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroTipo = opcionesTipo.get(position);
                aplicarFiltros();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        binding.spinnerFiltroEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroEstado = opcionesEstado.get(position);
                aplicarFiltros();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void aplicarFiltros() {
        listaFiltrada.clear();
        for (Equipo e : listaEquipos) {
            boolean coincideTipo = filtroTipo.equals("Todos") || e.getTipo().equals(filtroTipo);
            boolean coincideEstado = filtroEstado.equals("Todos") || e.getEstado().equals(filtroEstado);
            if (coincideTipo && coincideEstado) {
                listaFiltrada.add(e);
            }
        }

        binding.contenedorEquipos.removeAllViews();

        if (listaFiltrada.isEmpty()) {
            binding.textSinRegistros.setVisibility(View.VISIBLE);
            binding.scrollEquipos.setVisibility(View.GONE);
        } else {
            binding.textSinRegistros.setVisibility(View.GONE);
            binding.scrollEquipos.setVisibility(View.VISIBLE);

            for (int i = 0; i < listaFiltrada.size(); i++) {
                final int posicion = i;
                Equipo equipo = listaFiltrada.get(i);
                ItemEquipoBinding itemBinding = ItemEquipoBinding.inflate(getLayoutInflater());

                itemBinding.textCodigo.setText("Código: " + equipo.getCodigo());
                itemBinding.textNombre.setText("Nombre del equipo: " + equipo.getNombre());
                itemBinding.textTipo.setText("Tipo de equipo: " + equipo.getTipo());
                itemBinding.textEstado.setText("Estado: " + equipo.getEstado());

                switch (equipo.getEstado()) {
                    case "Operativo":
                        itemBinding.textEstado.setTextColor(Color.parseColor("#2E7D32"));
                        break;
                    case "En mantenimiento":
                        itemBinding.textEstado.setTextColor(Color.parseColor("#F9A825"));
                        break;
                    case "Fuera de servicio":
                        itemBinding.textEstado.setTextColor(Color.parseColor("#C62828"));
                        break;
                    default:
                        itemBinding.textEstado.setTextColor(Color.BLACK);
                        break;
                }

                itemBinding.getRoot().setOnLongClickListener(v -> {
                    startActionMode(new ActionMode.Callback() {
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
                            if (item.getItemId() == 1) {
                                editarEquipo(posicion);
                            } else if (item.getItemId() == 2) {
                                eliminarEquipo(posicion);
                            }
                            mode.finish();
                            return true;
                        }
                        @Override
                        public void onDestroyActionMode(ActionMode mode) { }
                    });
                    return true;
                });

                binding.contenedorEquipos.addView(itemBinding.getRoot());
            }
        }
    }

    private void editarEquipo(int posicion) {
        Equipo equipoAEditar = listaFiltrada.get(posicion);
        int indiceReal = listaEquipos.indexOf(equipoAEditar);
        Intent intent = new Intent(MainActivity.this, FormActivity.class);
        intent.putExtra("modo", "editar");
        intent.putExtra("indice", indiceReal);
        intent.putExtra("codigo", equipoAEditar.getCodigo());
        intent.putExtra("nombre", equipoAEditar.getNombre());
        intent.putExtra("tipo", equipoAEditar.getTipo());
        intent.putExtra("estado", equipoAEditar.getEstado());
        intent.putExtra("observaciones", equipoAEditar.getObservaciones());
        startActivityForResult(intent, CODIGO_FORM);
    }

    private void eliminarEquipo(int posicion) {
        Equipo equipoAEliminar = listaFiltrada.get(posicion);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Eliminar equipo")
                .setMessage("¿Está seguro que desea eliminar este equipo?")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    listaEquipos.remove(equipoAEliminar);
                    aplicarFiltros();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Actualizar").setIcon(android.R.drawable.ic_menu_rotate)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            filtroTipo = "Todos";
            filtroEstado = "Todos";
            binding.spinnerFiltroTipo.setSelection(0);
            binding.spinnerFiltroEstado.setSelection(0);
            aplicarFiltros();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_FORM && resultCode == RESULT_OK) {
            aplicarFiltros();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicarFiltros();
    }
}
