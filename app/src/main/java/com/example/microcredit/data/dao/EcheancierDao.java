package com.example.microcredit.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.microcredit.data.entity.Echeancier;

import java.util.List;

/**
 * EcheancierDao — interface d'accès aux données de la table "echeanciers"
 * Principe SOLID (ISP) : interface dédiée uniquement à l'Echeancier
 */
@Dao
public interface EcheancierDao {

    // ─────────────────────────────────────────
    //  INSERT
    // ─────────────────────────────────────────

    /** Insère une échéance */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Echeancier echeancier);

    /** Insère toutes les échéances d'un crédit d'un seul coup */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Echeancier> echeanciers);

    // ─────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────

    @Update
    void update(Echeancier echeancier);

    /** Enregistre un paiement sur une échéance */
    @Query("UPDATE echeanciers SET " +
            "montant_paye = montant_paye + :montant, " +
            "date_paiement_reel = :datePaiement, " +
            "statut = CASE " +
            "  WHEN (montant_paye + :montant) >= montant_echeance THEN 'PAYEE' " +
            "  WHEN (montant_paye + :montant) > 0 THEN 'PARTIELLEMENT_PAYEE' " +
            "  ELSE statut END " +
            "WHERE id = :echeancierEid")
    void enregistrerPaiement(long echeancierEid, double montant, String datePaiement);

    /** Met à jour le statut d'une échéance */
    @Query("UPDATE echeanciers SET statut = :statut WHERE id = :echeancierEid")
    void updateStatut(long echeancierEid, String statut);

    // ─────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────

    @Delete
    void delete(Echeancier echeancier);

    /** Supprime toutes les échéances d'un crédit */
    @Query("DELETE FROM echeanciers WHERE credit_id = :creditId")
    void deleteByCredit(long creditId);

    // ─────────────────────────────────────────
    //  SELECT
    // ─────────────────────────────────────────

    /** Toutes les échéances d'un crédit, dans l'ordre */
    @Query("SELECT * FROM echeanciers WHERE credit_id = :creditId ORDER BY numero_echeance ASC")
    LiveData<List<Echeancier>> getEcheanciersByCredit(long creditId);

    /** Échéances non encore payées d'un crédit */
    @Query("SELECT * FROM echeanciers WHERE credit_id = :creditId " +
            "AND statut != 'PAYEE' ORDER BY numero_echeance ASC")
    LiveData<List<Echeancier>> getEcheancesNonPayees(long creditId);

    /** Prochaine échéance à payer pour un crédit */
    @Query("SELECT * FROM echeanciers WHERE credit_id = :creditId " +
            "AND statut = 'EN_ATTENTE' ORDER BY numero_echeance ASC LIMIT 1")
    LiveData<Echeancier> getProchaineEcheance(long creditId);

    /** Toutes les échéances en retard (date dépassée et non payées) */
    @Query("SELECT * FROM echeanciers WHERE statut = 'EN_ATTENTE' " +
            "AND date_echeance < :dateAujourdhui ORDER BY date_echeance ASC")
    LiveData<List<Echeancier>> getEcheancesEnRetard(String dateAujourdhui);

    /** Nombre d'échéances payées pour un crédit */
    @Query("SELECT COUNT(*) FROM echeanciers WHERE credit_id = :creditId AND statut = 'PAYEE'")
    LiveData<Integer> countEcheancesPayees(long creditId);

    /** Somme totale payée sur un crédit */
    @Query("SELECT SUM(montant_paye) FROM echeanciers WHERE credit_id = :creditId")
    LiveData<Double> getTotalPayeParCredit(long creditId);
}