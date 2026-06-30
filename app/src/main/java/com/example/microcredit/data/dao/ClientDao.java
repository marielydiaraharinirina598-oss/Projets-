package com.example.microcredit.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.microcredit.data.entity.Client;

import java.util.List;

/**
 * ClientDao — interface d'accès aux données de la table "clients"
 * Room génère automatiquement le code SQL à partir des annotations
 *
 * Principe SOLID (ISP) : interface dédiée uniquement au Client
 */
@Dao
public interface ClientDao {

    // ─────────────────────────────────────────
    //  INSERT
    // ─────────────────────────────────────────

    /** Insère un nouveau client, retourne son id généré */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Client client);

    /** Insère plusieurs clients d'un coup */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Client> clients);

    // ─────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────

    /** Met à jour un client existant */
    @Update
    void update(Client client);

    // ─────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────

    /** Supprime un client */
    @Delete
    void delete(Client client);

    /** Supprime un client par son id */
    @Query("DELETE FROM clients WHERE id = :clientId")
    void deleteById(long clientId);

    // ─────────────────────────────────────────
    //  SELECT — LiveData (mise à jour auto UI)
    // ─────────────────────────────────────────

    /** Retourne tous les clients — l'UI se met à jour automatiquement */
    @Query("SELECT * FROM clients ORDER BY nom ASC")
    LiveData<List<Client>> getAllClients();

    /** Retourne un client par son id */
    @Query("SELECT * FROM clients WHERE id = :clientId")
    LiveData<Client> getClientById(long clientId);

    /** Cherche des clients par nom ou prénom */
    @Query("SELECT * FROM clients WHERE nom LIKE '%' || :recherche || '%' " +
            "OR prenom LIKE '%' || :recherche || '%' ORDER BY nom ASC")
    LiveData<List<Client>> rechercherClients(String recherche);

    /** Retourne les clients d'un groupe communautaire */
    @Query("SELECT * FROM clients WHERE groupe_communautaire = :groupe ORDER BY nom ASC")
    LiveData<List<Client>> getClientsByGroupe(String groupe);

    /** Retourne les clients actifs uniquement */
    @Query("SELECT * FROM clients WHERE statut = 'ACTIF' ORDER BY nom ASC")
    LiveData<List<Client>> getClientsActifs();

    /** Compte le nombre total de clients */
    @Query("SELECT COUNT(*) FROM clients")
    LiveData<Integer> countClients();

    /** Vérifie si un CIN existe déjà (évite les doublons) */
    @Query("SELECT COUNT(*) FROM clients WHERE cin = :cin")
    int existeCin(String cin);
}