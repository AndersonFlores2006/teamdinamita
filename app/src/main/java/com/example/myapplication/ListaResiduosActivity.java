package com.example.myapplication;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ListaResiduosActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listView;
    private ResiduosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_residuos); // Asegúrate de que el XML sea correcto

        // Inicializar la base de datos
        dbHelper = new DatabaseHelper(this);

        // Obtener el ListView del layout
        listView = findViewById(R.id.list_residuos);

        // Obtener la lista de residuos desde la BD
        ArrayList<String> residuos = dbHelper.getAllResiduos();

        // Verificar si la lista está vacía
        if (residuos.isEmpty()) {
            Toast.makeText(this, "No hay residuos en la base de datos", Toast.LENGTH_SHORT).show();
        }

        // Configurar el adaptador
        adapter = new ResiduosAdapter(this, residuos);
        listView.setAdapter(adapter);

        // Agregar evento al hacer clic en un residuo
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String residuoSeleccionado = residuos.get(position);
            Toast.makeText(this, "Seleccionaste: " + residuoSeleccionado, Toast.LENGTH_SHORT).show();
        });
    }
}
