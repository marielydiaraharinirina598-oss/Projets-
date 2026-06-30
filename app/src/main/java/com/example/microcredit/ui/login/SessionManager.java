package com.example.microcredit.ui.login;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager — gère la session utilisateur
 * Stocke : hash du PIN, état de connexion, timestamp dernière activité
 */
public class SessionManager {

    private static final String PREFS_NAME    = "microcredit_auth";
    private static final String KEY_PIN_HASH  = "pin_hash";
    private static final String KEY_PIN_SET   = "pin_configured";
    private static final String KEY_LAST_ACT  = "last_activity";
    private static final long   TIMEOUT_MS    = 5 * 60 * 1000L; // 5 minutes

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ─── Configuration du PIN ─────────────────

    /** Enregistre le PIN (stocké hashé) */
    public void enregistrerPin(String pin) {
        prefs.edit()
                .putString(KEY_PIN_HASH, PinHelper.hashPin(pin))
                .putBoolean(KEY_PIN_SET, true)
                .apply();
    }

    /** Vérifie si un PIN a déjà été configuré */
    public boolean isPinConfigue() {
        return prefs.getBoolean(KEY_PIN_SET, false);
    }

    /** Vérifie le PIN saisi par l'utilisateur */
    public boolean verifierPin(String pin) {
        String hashStocke = prefs.getString(KEY_PIN_HASH, "");
        return PinHelper.verifierPin(pin, hashStocke);
    }

    // ─── Gestion de session ───────────────────

    /** Enregistre l'heure de la dernière activité */
    public void enregistrerActivite() {
        prefs.edit()
                .putLong(KEY_LAST_ACT, System.currentTimeMillis())
                .apply();
    }

    /** Vérifie si la session est expirée (inactivité > 5 min) */
    public boolean isSessionExpiree() {
        long derniere = prefs.getLong(KEY_LAST_ACT, 0);
        return System.currentTimeMillis() - derniere > TIMEOUT_MS;
    }

    /** Réinitialise complètement (déconnexion) */
    public void deconnecter() {
        prefs.edit().remove(KEY_LAST_ACT).apply();
    }
}