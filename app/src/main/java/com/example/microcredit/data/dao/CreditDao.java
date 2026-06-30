package com.example.microcredit.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.microcredit.data.entity.Credit;

import java.util.List;

/**
 * CreditDao — interface d'accès aux données de la table "credits"
 * Principe SOLID (ISP) : interface dédiée uniquement au Crédit
 */
@Dao
public interface CreditDao {

    // ─────────────────────────────────────────
    //  INSERT
    // ─────────────────────────────────────────

    /** Insère un crédit, retourne l'id généré */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Credit credit);

    // ─────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────

    @Update
    void update(Credit credit);

    /** Met à jour uniquement le statut d'un crédit */
    @Query("UPDATE credits SET statut = :statut WHERE id = :creditId")
    void updateStatut(long creditId, String statut);

    /** Met à jour le montant remboursé après un paiement */
    @Query("UPDATE credits SET montant_rembourse = montant_rembourse + :montant WHERE id = :creditId")
    void ajouterRemboursement(long creditId, double montant);

    // ─────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────

    @Delete
    void delete(Credit credit);

    // ─────────────────────────────────────────
    //  SELECT
    // ─────────────────────────────────────────

    /** Tous les crédits */
    @Query("SELECT * FROM credits ORDER BY date_debut DESC")
    LiveData<List<Credit>> getAllCredits();

    /** Crédits d'un client précis */
    @Query("SELECT * FROM credits WHERE client_id = :clientId ORDER BY date_debut DESC")
    LiveData<List<Credit>> getCreditsByClient(long clientId);

    /** Un crédit par son id */
    @Query("SELECT * FROM credits WHERE id = :creditId")
    LiveData<Credit> getCreditById(long creditId);

    /** Crédits actifs uniquement */
    @Query("SELECT * FROM credits WHERE statut = 'ACTIF' ORDER BY date_debut DESC")
    LiveData<List<Credit>> getCreditsActifs();

    /** Crédits en retard (statut EN_RETARD) */
    @Query("SELECT * FROM credits WHERE statut = 'EN_RETARD'")
    LiveData<List<Credit>> getCreditsEnRetard();

    /** Nombre total de crédits actifs */
    @Query("SELECT COUNT(*) FROM credits WHERE statut = 'ACTIF'")
    LiveData<Integer> countCreditsActifs();

    /** Somme totale des montants prêtés */
    @Query("SELECT SUM(montant_principal) FROM credits WHERE statut != 'ANNULE'")
    LiveData<Double> getTotalMontantPrete();

    /** Somme totale encore due */
    @Query("SELECT SUM(montant_total_du - montant_rembourse) FROM credits WHERE statut = 'ACTIF'")
    LiveData<Double> getTotalRestantDu();
}