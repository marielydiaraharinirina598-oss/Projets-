package com.example.microcredit.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "clients")
public class Client {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "nom")
    private String nom;

    @ColumnInfo(name = "prenom")
    private String prenom;

    @ColumnInfo(name = "cin")
    private String cin;

    @ColumnInfo(name = "telephone")
    private String telephone;

    @ColumnInfo(name = "adresse")
    private String adresse;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "groupe_communautaire")
    private String groupeCommunautaire;

    @ColumnInfo(name = "profession")
    private String profession;

    @ColumnInfo(name = "date_adhesion")
    private String dateAdhesion;

    @ColumnInfo(name = "date_naissance")
    private String dateNaissance;

    @ColumnInfo(name = "statut")
    private String statut;

    // Constructeur principal — utilisé par Room
    public Client(long id, String nom, String prenom, String cin,
                  String telephone, String adresse, String email,
                  String groupeCommunautaire, String profession,
                  String dateAdhesion, String dateNaissance, String statut) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.telephone = telephone;
        this.adresse = adresse;
        this.email = email;
        this.groupeCommunautaire = groupeCommunautaire;
        this.profession = profession;
        this.dateAdhesion = dateAdhesion;
        this.dateNaissance = dateNaissance;
        this.statut = statut;
    }

    // Constructeur simplifié
    @Ignore
    public Client(String nom, String prenom, String cin,
                  String telephone, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.telephone = telephone;
        this.adresse = adresse;
        this.statut = "ACTIF";
    }

    public long getId()                          { return id; }
    public void setId(long id)                   { this.id = id; }
    public String getNom()                       { return nom; }
    public void setNom(String nom)               { this.nom = nom; }
    public String getPrenom()                    { return prenom; }
    public void setPrenom(String prenom)         { this.prenom = prenom; }
    public String getCin()                       { return cin; }
    public void setCin(String cin)               { this.cin = cin; }
    public String getTelephone()                 { return telephone; }
    public void setTelephone(String telephone)   { this.telephone = telephone; }
    public String getAdresse()                   { return adresse; }
    public void setAdresse(String adresse)       { this.adresse = adresse; }
    public String getEmail()                     { return email; }
    public void setEmail(String email)           { this.email = email; }
    public String getGroupeCommunautaire()       { return groupeCommunautaire; }
    public void setGroupeCommunautaire(String g) { this.groupeCommunautaire = g; }
    public String getProfession()                { return profession; }
    public void setProfession(String p)          { this.profession = p; }
    public String getDateAdhesion()              { return dateAdhesion; }
    public void setDateAdhesion(String d)        { this.dateAdhesion = d; }
    public String getDateNaissance()             { return dateNaissance; }
    public void setDateNaissance(String d)       { this.dateNaissance = d; }
    public String getStatut()                    { return statut; }
    public void setStatut(String statut)         { this.statut = statut; }
    public String getNomComplet()                { return prenom + " " + nom; }
}