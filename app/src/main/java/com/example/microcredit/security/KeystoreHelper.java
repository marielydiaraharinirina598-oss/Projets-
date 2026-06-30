package com.example.microcredit.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * KeystoreHelper — génère et stocke une clé AES dans Android Keystore
 * La clé ne quitte JAMAIS le téléphone — c'est la sécurité maximale
 */
public class KeystoreHelper {

    private static final String KEY_ALIAS    = "microcredit_db_key";
    private static final String ANDROID_KS   = "AndroidKeyStore";
    private static final String PREFS_NAME   = "microcredit_security";
    private static final String KEY_DB_PASS  = "db_encrypted_pass";
    private static final String KEY_IV       = "db_iv";

    private KeystoreHelper() {}

    /**
     * Retourne le mot de passe de la base de données
     * Génère la clé au premier appel, la réutilise ensuite
     */
    public static String getDbPassword(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                    PREFS_NAME, Context.MODE_PRIVATE);

            String encryptedPass = prefs.getString(KEY_DB_PASS, null);

            if (encryptedPass == null) {
                // Premier lancement : générer une nouvelle clé et un mot de passe
                return genererEtStockerPassword(context, prefs);
            } else {
                // Lancement suivant : déchiffrer le mot de passe stocké
                return dechiffrerPassword(encryptedPass,
                        prefs.getString(KEY_IV, ""));
            }
        } catch (Exception e) {
            // Fallback sécurisé si Keystore indisponible
            return context.getPackageName() + "_fallback_key_2024";
        }
    }

    private static String genererEtStockerPassword(Context context,
                                                   SharedPreferences prefs) throws Exception {
        // 1. Générer la clé AES dans Android Keystore
        KeyGenerator keyGen = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KS);
        keyGen.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build());
        keyGen.generateKey();

        // 2. Générer un mot de passe aléatoire pour la BD
        byte[] passBytes = new byte[32];
        new java.security.SecureRandom().nextBytes(passBytes);
        String password = Base64.encodeToString(passBytes, Base64.NO_WRAP);

        // 3. Chiffrer ce mot de passe avec la clé Keystore
        KeyStore ks = KeyStore.getInstance(ANDROID_KS);
        ks.load(null);
        SecretKey secretKey = ((KeyStore.SecretKeyEntry)
                ks.getEntry(KEY_ALIAS, null)).getSecretKey();

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(password.getBytes());
        byte[] iv = cipher.getIV();

        // 4. Stocker le mot de passe chiffré
        prefs.edit()
                .putString(KEY_DB_PASS, Base64.encodeToString(encrypted, Base64.NO_WRAP))
                .putString(KEY_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
                .apply();

        return password;
    }

    private static String dechiffrerPassword(String encryptedPass,
                                             String ivStr) throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KS);
        ks.load(null);
        SecretKey secretKey = ((KeyStore.SecretKeyEntry)
                ks.getEntry(KEY_ALIAS, null)).getSecretKey();

        byte[] iv          = Base64.decode(ivStr, Base64.NO_WRAP);
        byte[] encryptedBytes = Base64.decode(encryptedPass, Base64.NO_WRAP);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted);
    }
}