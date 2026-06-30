package com.example.microcredit.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.microcredit.MainActivity;
import com.example.microcredit.R;

public class LoginActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private StringBuilder  pinSaisi = new StringBuilder();
    private boolean modeCreation = false;
    private String  premierPin   = "";
    private View dot1, dot2, dot3, dot4;
    private TextView tvSousTitre, tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        dot1        = findViewById(R.id.dot1);
        dot2        = findViewById(R.id.dot2);
        dot3        = findViewById(R.id.dot3);
        dot4        = findViewById(R.id.dot4);
        tvError     = findViewById(R.id.tvError);
        tvSousTitre = findViewById(R.id.tvSousTitre);

        if (!sessionManager.isPinConfigue()) {
            modeCreation = true;
            tvSousTitre.setText("Créez votre code PIN");
        }
        configurerBoutons();
    }

    private void configurerBoutons() {
        int[] ids = { R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9 };
        String[] chiffres = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < ids.length; i++) {
            String c = chiffres[i];
            android.widget.Button btn = findViewById(ids[i]);
            if (btn != null) btn.setOnClickListener(v -> ajouterChiffre(c));
        }
        android.widget.Button del = findViewById(R.id.btnDel);
        if (del != null) del.setOnClickListener(v -> supprimerDernier());
    }

    private void ajouterChiffre(String c) {
        if (pinSaisi.length() >= 4) return;
        pinSaisi.append(c);
        mettreAJourPoints();
        if (pinSaisi.length() == 4) {
            if (modeCreation) traiterCreation();
            else traiterVerification();
        }
    }

    private void supprimerDernier() {
        if (pinSaisi.length() > 0) {
            pinSaisi.deleteCharAt(pinSaisi.length() - 1);
            mettreAJourPoints();
            tvError.setText("");
        }
    }

    private void traiterCreation() {
        if (premierPin.isEmpty()) {
            premierPin = pinSaisi.toString();
            pinSaisi.setLength(0);
            mettreAJourPoints();
            tvSousTitre.setText("Confirmez votre code PIN");
        } else {
            if (pinSaisi.toString().equals(premierPin)) {
                sessionManager.enregistrerPin(pinSaisi.toString());
                // ✅ Direct vers Dashboard — sans agent
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                tvError.setText("Les codes ne correspondent pas.");
                premierPin = "";
                reinitialiser();
                tvSousTitre.setText("Créez votre code PIN");
            }
        }
    }

    private void traiterVerification() {
        if (sessionManager.verifierPin(pinSaisi.toString())) {
            sessionManager.enregistrerActivite();
            // ✅ Direct vers Dashboard — sans agent
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            tvError.setText("Code incorrect. Réessayez.");
            reinitialiser();
        }
    }

    private void mettreAJourPoints() {
        View[] dots = {dot1, dot2, dot3, dot4};
        for (int i = 0; i < 4; i++) {
            if (dots[i] != null)
                dots[i].setBackgroundResource(i < pinSaisi.length()
                        ? R.drawable.pin_dot_filled
                        : R.drawable.pin_dot_empty);
        }
    }

    private void reinitialiser() {
        pinSaisi.setLength(0);
        dot1.postDelayed(this::mettreAJourPoints, 500);
    }
}