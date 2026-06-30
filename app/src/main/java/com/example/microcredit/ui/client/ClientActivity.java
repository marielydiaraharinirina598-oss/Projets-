package com.example.microcredit.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microcredit.R;
import com.example.microcredit.data.entity.Client;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ClientActivity extends AppCompatActivity {

    public static final String EXTRA_CLIENT_ID = "client_id";
    public static final String EXTRA_MODE      = "mode"; // "add" ou "edit"

    private ClientViewModel viewModel;
    private ClientAdapter   adapter;
    private TextView        tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerClients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClientAdapter();
        recyclerView.setAdapter(adapter);

        tvCount = findViewById(R.id.tvCount);

        // Observer la liste
        viewModel.getTousLesClients().observe(this, clients -> {
            adapter.setClients(clients);
            tvCount.setText(clients.size() + " client(s)");
        });

        // Recherche
        EditText etRecherche = findViewById(R.id.etRecherche);
        etRecherche.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                String q = s.toString().trim();
                if (q.isEmpty()) {
                    viewModel.getTousLesClients().observe(ClientActivity.this,
                            list -> adapter.setClients(list));
                } else {
                    viewModel.rechercherClients(q).observe(ClientActivity.this,
                            list -> adapter.setClients(list));
                }
            }
            public void afterTextChanged(Editable s) {}
        });

        // Clics liste : éditer / supprimer
        adapter.setListener(new ClientAdapter.OnClientClickListener() {
            @Override
            public void onEdit(Client client) {
                Intent i = new Intent(ClientActivity.this, ClientFormActivity.class);
                i.putExtra(EXTRA_CLIENT_ID, client.getId());
                i.putExtra(EXTRA_MODE, "edit");
                startActivity(i);
            }
            @Override
            public void onDelete(Client client) {
                new AlertDialog.Builder(ClientActivity.this)
                        .setTitle("Supprimer")
                        .setMessage("Supprimer " + client.getNomComplet() + " ?")
                        .setPositiveButton("Supprimer", (d, w) -> viewModel.delete(client))
                        .setNegativeButton("Annuler", null)
                        .show();
            }
        });

        // Bouton FAB → ajouter nouveau client
        FloatingActionButton fab = findViewById(R.id.fabAjouter);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, ClientFormActivity.class);
            i.putExtra(EXTRA_MODE, "add");
            startActivity(i);
        });

        // Bouton retour
        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
    }
}