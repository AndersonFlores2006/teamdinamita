package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResiduosAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> residuosList;
    private LayoutInflater inflater;
    private DatabaseHelper dbHelper;  // Instancia para acceder a la base de datos

    public ResiduosAdapter(Context context, ArrayList<String> residuosList) {
        this.context = context;
        this.residuosList = residuosList;
        this.inflater = LayoutInflater.from(context);
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return residuosList.size();
    }

    @Override
    public Object getItem(int position) {
        return residuosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView textResiduo;
        ImageView imageResiduo;
        ImageView iconEdit, iconDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_residuo, parent, false);
            holder = new ViewHolder();
            holder.textResiduo = convertView.findViewById(R.id.text_residuo);
            holder.imageResiduo = convertView.findViewById(R.id.image_residuo);
            holder.iconEdit = convertView.findViewById(R.id.icon_edit);
            holder.iconDelete = convertView.findViewById(R.id.icon_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String residuo = residuosList.get(position);

        // Extraer la cantidad y mantener el formato con "kg"
        int cantidad = extraerCantidad(residuo);
        String residuoConKg = residuo.replaceFirst("Cantidad: \\d+", "Cantidad: " + cantidad + " kg");
        holder.textResiduo.setText(residuoConKg);

        // Extraer tipo de residuo y limpiar tildes
        String tipoResiduo = extraerTipo(residuo).toLowerCase()
                .replace("á", "a").replace("é", "e")
                .replace("í", "i").replace("ó", "o")
                .replace("ú", "u").trim();

        // Asignar la imagen según el tipo
        switch (tipoResiduo) {
            case "vidrio":
                holder.imageResiduo.setImageResource(R.drawable.vidrio);
                break;
            case "plastico":
                holder.imageResiduo.setImageResource(R.drawable.plastico);
                break;
            case "papel":
                holder.imageResiduo.setImageResource(R.drawable.papel);
                break;
            case "metal":
                holder.imageResiduo.setImageResource(R.drawable.metal);
                break;
            case "organico":
                holder.imageResiduo.setImageResource(R.drawable.organico);
                break;
            default:
                holder.imageResiduo.setImageResource(R.drawable.residuos);
                break;
        }

        // Botón eliminar: elimina el residuo de la BD y actualiza la lista
        holder.iconDelete.setOnClickListener(v -> {
            int id = extraerId(residuo);
            if (id != -1) {
                new AlertDialog.Builder(context)
                        .setTitle("Eliminar residuo")
                        .setMessage("¿Seguro que deseas eliminar este residuo?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            boolean success = dbHelper.deleteResiduo(id);
                            if (success) {
                                residuosList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Residuo eliminado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(context, "Error al obtener ID", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón editar: muestra un diálogo para editar y actualiza en la BD
        holder.iconEdit.setOnClickListener(v -> mostrarDialogoEdicion(position));

        return convertView;
    }

    // Método para ordenar la lista
    public void ordenarLista(String criterio) {
        if (criterio.equalsIgnoreCase("Por Tipo") || criterio.equalsIgnoreCase("Tipo")) {
            Collections.sort(residuosList, (r1, r2) -> extraerTipo(r1).compareTo(extraerTipo(r2)));
        } else if (criterio.equalsIgnoreCase("Por Cantidad") || criterio.equalsIgnoreCase("Cantidad")) {
            Collections.sort(residuosList, Comparator.comparingInt(this::extraerCantidad));
        }
        notifyDataSetChanged();
    }

    // Extraer el tipo de residuo
    private String extraerTipo(String residuo) {
        if (residuo.contains("Tipo:")) {
            String[] partes = residuo.split("Tipo: ");
            if (partes.length > 1) {
                return partes[1].split(" \\|")[0].trim();
            }
        }
        return "";
    }

    // Extraer la cantidad de residuo de forma robusta
    private int extraerCantidad(String residuo) {
        if (residuo.contains("Cantidad:")) {
            String[] partes = residuo.split("Cantidad: ");
            if (partes.length > 1) {
                try {
                    return Integer.parseInt(partes[1].replaceAll("[^0-9]", "").trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    // Extraer el ID del residuo (asumiendo formato: "ID: <id> | Tipo: ... | Cantidad: ...")
    private int extraerId(String residuo) {
        if (residuo.contains("ID:")) {
            String[] partes = residuo.split(" \\| ");
            if (partes.length > 0) {
                String idStr = partes[0].replace("ID:", "").trim();
                try {
                    return Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }

    // Mostrar diálogo para editar residuo y actualizar permanentemente en la base de datos
    private void mostrarDialogoEdicion(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar residuo");

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_editar_residuo, null);
        EditText inputTipo = viewInflated.findViewById(R.id.edit_tipo);
        EditText inputCantidad = viewInflated.findViewById(R.id.edit_cantidad);

        // Obtener los valores actuales
        String residuo = residuosList.get(position);
        inputTipo.setText(extraerTipo(residuo));
        inputCantidad.setText(String.valueOf(extraerCantidad(residuo)));

        builder.setView(viewInflated);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoTipo = inputTipo.getText().toString().trim();
            String nuevaCantidad = inputCantidad.getText().toString().trim();

            if (!nuevoTipo.isEmpty() && !nuevaCantidad.isEmpty()) {
                int cantidad;
                try {
                    cantidad = Integer.parseInt(nuevaCantidad);
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Cantidad inválida", Toast.LENGTH_SHORT).show();
                    return;
                }
                int id = extraerId(residuo);
                if (id != -1) {
                    boolean success = dbHelper.updateResiduo(id, nuevoTipo, cantidad);
                    if (success) {
                        // Actualiza el string en la lista con el nuevo formato
                        residuosList.set(position, "ID: " + id + " | Tipo: " + nuevoTipo + " | Cantidad: " + cantidad);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Residuo actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Error al obtener ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
