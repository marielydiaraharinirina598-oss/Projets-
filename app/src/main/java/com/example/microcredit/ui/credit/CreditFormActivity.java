package com.example.microcredit.ui.credit;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.microcredit.R;
import com.example.microcredit.data.entity.Client;
import com.example.microcredit.data.entity.Credit;
import com.example.microcredit.data.repository.ClientRepository;
import com.example.microcredit.data.repository.CreditRepository;
import com.example.microcredit.domain.usecase.CreateEcheancierUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreditFormActivity extends AppCompatActivity {

    private EditText etMontant, etTaux, etDuree, etDateDebut, etObjet;
    private Spinner  spClient, spFrequence, spType;
    private TextView tvMensualite;

    private List<Client> listeClients = new ArrayList<>();
    private ClientRepository clientRepository;
    private CreditRepository creditRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_form);

        clientRepository = new ClientRepository(getApplication());
        creditRepository = new CreditRepository(getApplication());

        // Vues
        etMontant    = findViewById(R.id.etMontant);
        etTaux       = findViewById(R.id.etTaux);
        etDuree      = findViewById(R.id.etDuree);
        etDateDebut  = findViewById(R.id.etDateDebut);
        etObjet      = findViewById(R.id.etObjet);
        tvMensualite = findViewById(R.id.tvMensualite);
        spClient     = findViewById(R.id.spClient);
        spFrequence  = findViewById(R.id.spFrequence);
        spType       = findViewById(R.id.spType);

        // Spinner fréquence
        ArrayAdapter<String> freqAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"MENSUEL", "HEBDOMADAIRE", "JOURNALIER"});
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrequence.setAdapter(freqAdapter);

        // Spinner type
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"INDIVIDUEL", "SOLIDAIRE"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        // Charger les clients dans le spinner
        clientRepository.getClientsActifs().observe(this, clients -> {
            listeClients = clients;
            List<String> noms = new ArrayList<>();
            for (Client c : clients) noms.add(c.getNomComplet());
            ArrayAdapter<String> clientAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, noms);
            clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spClient.setAdapter(clientAdapter);
        });

        // Calcul mensualité en temps réel
        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) { calculerMensualite(); }
            public void afterTextChanged(android.text.Editable s) {}
        };
        etMontant.addTextChangedListener(watcher);
        etTaux.addTextChangedListener(watcher);
        etDuree.addTextChangedListener(watcher);

        // Boutons
        findViewById(R.id.btnSave).setOnClickListener(v -> enregistrer());
        findViewById(R.id.btnAnnuler).setOnClickListener(v -> finish());
    }

    private void calculerMensualite() {
        try {
            double montant = Double.parseDouble(etMontant.getText().toString());
            double taux    = Double.parseDouble(etTaux.getText().toString());
            int    duree   = Integer.parseInt(etDuree.getText().toString());
            if (montant > 0 && taux >= 0 && duree > 0) {
                double r = taux / 100.0;
                double mensualite;
                if (r == 0) {
                    mensualite = montant / duree;
                } else {
                    mensualite = montant * (r * Math.pow(1+r, duree)) / (Math.pow(1+r, duree) - 1);
                }
                tvMensualite.setText(String.format("Mensualité estimée : %,.0f Ar", mensualite));
            }
        } catch (NumberFormatException ignored) {
            tvMensualite.setText("Mensualité estimée : -");
        }
    }

    private void enregistrer() {
        String sMontant = etMontant.getText().toString().trim();
        String sTaux    = etTaux.getText().toString().trim();
        String sDuree   = etDuree.getText().toString().trim();
        String sDate    = etDateDebut.getText().toString().trim();

        if (sMontant.isEmpty()) { etMontant.setError("Obligatoire");   return; }
        if (sTaux.isEmpty())    { etTaux.setError("Obligatoire");      return; }
        if (sDuree.isEmpty())   { etDuree.setError("Obligatoire");     return; }
        if (sDate.isEmpty())    { etDateDebut.setError("Obligatoire"); return; }
        if (listeClients.isEmpty()) {
            Toast.makeText(this, "Aucun client actif disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        double montant = Double.parseDouble(sMontant);
        double taux    = Double.parseDouble(sTaux);
        int    duree   = Integer.parseInt(sDuree);
        long   clientId = listeClients.get(spClient.getSelectedItemPosition()).getId();
        String frequence = spFrequence.getSelectedItem().toString();
        String type      = spType.getSelectedItem().toString();
        String objet     = etObjet.getText().toString().trim();

        executor.execute(() -> {
            // 1. Insérer le crédit et récupérer son id
            Credit credit = new Credit(clientId, montant, taux, duree, frequence, sDate);
            credit.setTypeCredit(type);
            credit.setObjetCredit(objet);
            credit.setStatut("ACTIF");
            double total = Credit.calculerMontantTotal(montant, taux, duree);
            credit.setMontantTotalDu(total);

            long creditId = com.example.microcredit.data.local.AppDatabase
                    .getInstance(getApplication()).creditDao().insert(credit);

            // 2. Générer l'échéancier avec le vrai creditId
            CreateEcheancierUseCase useCase = new CreateEcheancierUseCase(creditRepository);
            useCase.execute(creditId, montant, taux, duree, sDate);

            runOnUiThread(() -> {
                Toast.makeText(this, "Crédit enregistré + échéancier généré ✓",
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}