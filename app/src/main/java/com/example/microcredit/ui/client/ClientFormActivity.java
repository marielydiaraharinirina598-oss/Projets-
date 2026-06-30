package com.example.microcredit.ui.client;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.microcredit.R;
import com.example.microcredit.data.entity.Client;

public class ClientFormActivity extends AppCompatActivity {

    private ClientViewModel viewModel;
    private EditText etNom, etPrenom, etCin, etTel, etAdresse, etEmail,
            etGroupe, etProfession, etNaissance, etAdhesion;
    private Spinner  spStatut;
    private boolean  modeEdit = false;
    private long     clientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_form);

        viewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        // Liaison des champs
        etNom        = findViewById(R.id.etNom);
        etPrenom     = findViewById(R.id.etPrenom);
        etCin        = findViewById(R.id.etCin);
        etTel        = findViewById(R.id.etTel);
        etAdresse    = findViewById(R.id.etAdresse);
        etEmail      = findViewById(R.id.etEmail);
        etGroupe     = findViewById(R.id.etGroupe);
        etProfession = findViewById(R.id.etProfession);
        etNaissance  = findViewById(R.id.etNaissance);
        etAdhesion   = findViewById(R.id.etAdhesion);
        spStatut     = findViewById(R.id.spStatut);

        // Spinner statut
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"ACTIF", "INACTIF", "SUSPENDU"});
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatut.setAdapter(spAdapter);

        // Mode édition → charger les données
        String mode = getIntent().getStringExtra(ClientActivity.EXTRA_MODE);
        if ("edit".equals(mode)) {
            modeEdit = true;
            clientId = getIntent().getLongExtra(ClientActivity.EXTRA_CLIENT_ID, -1);
            chargerClient(clientId);
        }

        // Titre
        Button btnTitre = findViewById(R.id.btnTitre);
        if (btnTitre != null)
            btnTitre.setText(modeEdit ? "Modifier le client" : "Nouveau client");

        // Bouton Enregistrer
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> enregistrer());

        // Bouton Annuler
        Button btnAnnuler = findViewById(R.id.btnAnnuler);
        btnAnnuler.setOnClickListener(v -> finish());
    }

    private void chargerClient(long id) {
        viewModel.getTousLesClients().observe(this, clients -> {
            for (Client c : clients) {
                if (c.getId() == id) {
                    etNom.setText(c.getNom());
                    etPrenom.setText(c.getPrenom());
                    etCin.setText(c.getCin());
                    etTel.setText(c.getTelephone());
                    etAdresse.setText(c.getAdresse());
                    etEmail.setText(c.getEmail());
                    etGroupe.setText(c.getGroupeCommunautaire());
                    etProfession.setText(c.getProfession());
                    etNaissance.setText(c.getDateNaissance());
                    etAdhesion.setText(c.getDateAdhesion());
                    // Sélectionner le bon statut dans le spinner
                    String[] statuts = {"ACTIF", "INACTIF", "SUSPENDU"};
                    for (int i = 0; i < statuts.length; i++) {
                        if (statuts[i].equals(c.getStatut())) {
                            spStatut.setSelection(i);
                            break;
                        }
                    }
                    break;
                }
            }
        });
    }

    private void enregistrer() {
        // Validation des champs obligatoires
        String nom    = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String cin    = etCin.getText().toString().trim();
        String tel    = etTel.getText().toString().trim();

        if (nom.isEmpty())    { etNom.setError("Champ obligatoire");    return; }
        if (prenom.isEmpty()) { etPrenom.setError("Champ obligatoire"); return; }
        if (cin.isEmpty())    { etCin.setError("Champ obligatoire");    return; }
        if (tel.isEmpty())    { etTel.setError("Champ obligatoire");    return; }

        if (modeEdit) {
            // Mise à jour
            Client c = new Client(clientId, nom, prenom, cin, tel,
                    etAdresse.getText().toString().trim(),
                    etEmail.getText().toString().trim(),
                    etGroupe.getText().toString().trim(),
                    etProfession.getText().toString().trim(),
                    etAdhesion.getText().toString().trim(),
                    etNaissance.getText().toString().trim(),
                    spStatut.getSelectedItem().toString());
            viewModel.update(c);
            Toast.makeText(this, "Client modifié ✓", Toast.LENGTH_SHORT).show();
        } else {
            // Nouveau client
            Client c = new Client(nom, prenom, cin, tel,
                    etAdresse.getText().toString().trim());
            c.setEmail(etEmail.getText().toString().trim());
            c.setGroupeCommunautaire(etGroupe.getText().toString().trim());
            c.setProfession(etProfession.getText().toString().trim());
            c.setDateNaissance(etNaissance.getText().toString().trim());
            c.setDateAdhesion(etAdhesion.getText().toString().trim());
            c.setStatut(spStatut.getSelectedItem().toString());
            viewModel.insert(c);
            Toast.makeText(this, "Client ajouté ✓", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}