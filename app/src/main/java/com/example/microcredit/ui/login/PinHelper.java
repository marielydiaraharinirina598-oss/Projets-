package com.example.microcredit.ui.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PinHelper — utilitaire pour hasher et vérifier le PIN
 * Le PIN n'est JAMAIS stocké en clair — seulement son hash SHA-256
 */
public class PinHelper {

    private PinHelper() {}

    /** Transforme un PIN en hash SHA-256 */
    public static String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return pin; // fallback (ne devrait jamais arriver)
        }
    }

    /** Vérifie si un PIN saisi correspond au hash stocké */
    public static boolean verifierPin(String pinSaisi, String hashStocke) {
        return hashPin(pinSaisi).equals(hashStocke);
    }

    /** Vérifie qu'un PIN est valide (4 chiffres) */
    public static boolean estValide(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }
}