package com.example.microcredit;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.microcredit.data.repository.ClientRepository;
import com.example.microcredit.data.repository.CreditRepository;
import com.example.microcredit.ui.client.ClientActivity;
import com.example.microcredit.ui.credit.CreditActivity;
import com.example.microcredit.ui.login.LoginActivity;
import com.example.microcredit.ui.login.SessionManager;
import com.example.microcredit.ui.retard.RetardActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SessionManager   sessionManager;
    private ClientRepository clientRepository;
    private CreditRepository creditRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 🔒 Bloquer screenshots
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

        sessionManager   = new SessionManager(this);
        clientRepository = new ClientRepository(getApplication());
        creditRepository = new CreditRepository(getApplication());

        // Stats tableau de bord
        TextView tvNbClients = findViewById(R.id.tvNbClients);
        TextView tvNbCredits  = findViewById(R.id.tvNbCredits);

        clientRepository.countClients().observe(this, n ->
                tvNbClients.setText(n != null ? String.valueOf(n) : "0"));

        creditRepository.getCreditsActifs().observe(this, list ->
                tvNbCredits.setText(list != null ? String.valueOf(list.size()) : "0"));

        // ✅ Bottom Navigation Bar — 4 icônes alignées en bas
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_clients) {
                startActivity(new Intent(this, ClientActivity.class));
                return true;
            } else if (id == R.id.nav_credits) {
                startActivity(new Intent(this, CreditActivity.class));
                return true;
            } else if (id == R.id.nav_echeancier) {
                // Affiche la liste des crédits pour choisir lequel voir
                startActivity(new Intent(this, CreditActivity.class));
                return true;
            } else if (id == R.id.nav_paiement) {
                startActivity(new Intent(this, RetardActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🔒 Auto-lock 5 minutes
        if (sessionManager.isPinConfigue() && sessionManager.isSessionExpiree()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            sessionManager.enregistrerActivite();
        }
    }
}