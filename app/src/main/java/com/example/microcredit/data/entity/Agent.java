package com.example.microcredit.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Entité Agent — table "agents"
 * Rôles : ADMIN, SUPERVISEUR, AGENT, CAISSIER
 */
@Entity(tableName = "agents")
public class Agent {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "nom")
    private String nom;

    @ColumnInfo(name = "prenom")
    private String prenom;

    @ColumnInfo(name = "username")
    private String username;          // identifiant de connexion

    @ColumnInfo(name = "pin_hash")
    private String pinHash;           // PIN hashé SHA-256

    @ColumnInfo(name = "role")
    private String role;              // ADMIN / SUPERVISEUR / AGENT / CAISSIER

    @ColumnInfo(name = "telephone")
    private String telephone;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "statut")
    private String statut;            // ACTIF / INACTIF

    @ColumnInfo(name = "date_creation")
    private String dateCreation;

    @ColumnInfo(name = "derniere_connexion")
    private String derniereConnexion;

    // Constructeur principal — Room
    public Agent(long id, String nom, String prenom, String username,
                 String pinHash, String role, String telephone,
                 String email, String statut, String dateCreation,
                 String derniereConnexion) {
        this.id                = id;
        this.nom               = nom;
        this.prenom            = prenom;
        this.username          = username;
        this.pinHash           = pinHash;
        this.role              = role;
        this.telephone         = telephone;
        this.email             = email;
        this.statut            = statut;
        this.dateCreation      = dateCreation;
        this.derniereConnexion = derniereConnexion;
    }

    // Constructeur simplifié
    @Ignore
    public Agent(String nom, String prenom, String username,
                 String pinHash, String role) {
        this.nom      = nom;
        this.prenom   = prenom;
        this.username = username;
        this.pinHash  = pinHash;
        this.role     = role;
        this.statut   = "ACTIF";
    }

    // Méthodes utilitaires
    public String getNomComplet()  { return prenom + " " + nom; }
    public boolean isAdmin()       { return "ADMIN".equals(role); }
    public boolean isSuperviseur() { return "SUPERVISEUR".equals(role); }
    public boolean isAgent()       { return "AGENT".equals(role); }
    public boolean isCaissier()    { return "CAISSIER".equals(role); }

    public long getId()                            { return id; }
    public void setId(long id)                     { this.id = id; }
    public String getNom()                         { return nom; }
    public void setNom(String nom)                 { this.nom = nom; }
    public String getPrenom()                      { return prenom; }
    public void setPrenom(String prenom)           { this.prenom = prenom; }
    public String getUsername()                    { return username; }
    public void setUsername(String username)       { this.username = username; }
    public String getPinHash()                     { return pinHash; }
    public void setPinHash(String pinHash)         { this.pinHash = pinHash; }
    public String getRole()                        { return role; }
    public void setRole(String role)               { this.role = role; }
    public String getTelephone()                   { return telephone; }
    public void setTelephone(String telephone)     { this.telephone = telephone; }
    public String getEmail()                       { return email; }
    public void setEmail(String email)             { this.email = email; }
    public String getStatut()                      { return statut; }
    public void setStatut(String statut)           { this.statut = statut; }
    public String getDateCreation()                { return dateCreation; }
    public void setDateCreation(String d)          { this.dateCreation = d; }
    public String getDerniereConnexion()           { return derniereConnexion; }
    public void setDerniereConnexion(String d)     { this.derniereConnexion = d; }
}