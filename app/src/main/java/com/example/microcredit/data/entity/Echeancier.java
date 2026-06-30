package com.example.microcredit.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "echeanciers",
        foreignKeys = @ForeignKey(
                entity        = Credit.class,
                parentColumns = "id",
                childColumns  = "credit_id",
                onDelete      = ForeignKey.CASCADE
        ),
        indices = { @Index("credit_id") }
)
public class Echeancier {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "credit_id")
    private long creditId;

    @ColumnInfo(name = "numero_echeance")
    private int numeroEcheance;

    @ColumnInfo(name = "date_echeance")
    private String dateEcheance;

    @ColumnInfo(name = "date_paiement_reel")
    private String datePaiementReel;

    @ColumnInfo(name = "montant_capital")
    private double montantCapital;

    @ColumnInfo(name = "montant_interet")
    private double montantInteret;

    @ColumnInfo(name = "montant_echeance")
    private double montantEcheance;

    @ColumnInfo(name = "montant_paye")
    private double montantPaye;

    @ColumnInfo(name = "capital_restant_du")
    private double capitalRestantDu;

    @ColumnInfo(name = "statut")
    private String statut;

    @ColumnInfo(name = "penalite_retard")
    private double penaliteRetard;

    @ColumnInfo(name = "observation")
    private String observation;

    // Constructeur principal — utilisé par Room
    public Echeancier(long id, long creditId, int numeroEcheance,
                      String dateEcheance, String datePaiementReel,
                      double montantCapital, double montantInteret,
                      double montantEcheance, double montantPaye,
                      double capitalRestantDu, String statut,
                      double penaliteRetard, String observation) {
        this.id = id;
        this.creditId = creditId;
        this.numeroEcheance = numeroEcheance;
        this.dateEcheance = dateEcheance;
        this.datePaiementReel = datePaiementReel;
        this.montantCapital = montantCapital;
        this.montantInteret = montantInteret;
        this.montantEcheance = montantEcheance;
        this.montantPaye = montantPaye;
        this.capitalRestantDu = capitalRestantDu;
        this.statut = statut;
        this.penaliteRetard = penaliteRetard;
        this.observation = observation;
    }

    @Ignore
    public Echeancier(long creditId, int numeroEcheance, String dateEcheance,
                      double montantCapital, double montantInteret, double capitalRestantDu) {
        this.creditId = creditId;
        this.numeroEcheance = numeroEcheance;
        this.dateEcheance = dateEcheance;
        this.montantCapital = montantCapital;
        this.montantInteret = montantInteret;
        this.montantEcheance = montantCapital + montantInteret;
        this.capitalRestantDu = capitalRestantDu;
        this.montantPaye = 0.0;
        this.penaliteRetard = 0.0;
        this.statut = "EN_ATTENTE";
    }

    public boolean estPayee()                  { return montantPaye >= montantEcheance; }
    public double getMontantRestant()          { return Math.max(0, montantEcheance - montantPaye); }

    public void enregistrerPaiement(double montant, String dateReel) {
        this.montantPaye += montant;
        this.datePaiementReel = dateReel;
        if (this.montantPaye >= this.montantEcheance) this.statut = "PAYEE";
        else if (this.montantPaye > 0) this.statut = "PARTIELLEMENT_PAYEE";
    }

    public long getId()                          { return id; }
    public void setId(long id)                   { this.id = id; }
    public long getCreditId()                    { return creditId; }
    public void setCreditId(long creditId)       { this.creditId = creditId; }
    public int getNumeroEcheance()               { return numeroEcheance; }
    public void setNumeroEcheance(int n)         { this.numeroEcheance = n; }
    public String getDateEcheance()              { return dateEcheance; }
    public void setDateEcheance(String d)        { this.dateEcheance = d; }
    public String getDatePaiementReel()          { return datePaiementReel; }
    public void setDatePaiementReel(String d)    { this.datePaiementReel = d; }
    public double getMontantCapital()            { return montantCapital; }
    public void setMontantCapital(double m)      { this.montantCapital = m; }
    public double getMontantInteret()            { return montantInteret; }
    public void setMontantInteret(double m)      { this.montantInteret = m; }
    public double getMontantEcheance()           { return montantEcheance; }
    public void setMontantEcheance(double m)     { this.montantEcheance = m; }
    public double getMontantPaye()               { return montantPaye; }
    public void setMontantPaye(double m)         { this.montantPaye = m; }
    public double getCapitalRestantDu()          { return capitalRestantDu; }
    public void setCapitalRestantDu(double c)    { this.capitalRestantDu = c; }
    public String getStatut()                    { return statut; }
    public void setStatut(String statut)         { this.statut = statut; }
    public double getPenaliteRetard()            { return penaliteRetard; }
    public void setPenaliteRetard(double p)      { this.penaliteRetard = p; }
    public String getObservation()               { return observation; }
    public void setObservation(String o)         { this.observation = o; }
}