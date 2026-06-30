package com.example.microcredit.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "credits",
        foreignKeys = @ForeignKey(
                entity        = Client.class,
                parentColumns = "id",
                childColumns  = "client_id",
                onDelete      = ForeignKey.CASCADE
        ),
        indices = { @Index("client_id") }
)
public class Credit {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "client_id")
    private long clientId;

    @ColumnInfo(name = "montant_principal")
    private double montantPrincipal;

    @ColumnInfo(name = "taux_interet")
    private double tauxInteret;

    @ColumnInfo(name = "montant_total_du")
    private double montantTotalDu;

    @ColumnInfo(name = "montant_rembourse")
    private double montantRembourse;

    @ColumnInfo(name = "duree_mois")
    private int dureeMois;

    @ColumnInfo(name = "frequence_paiement")
    private String frequencePaiement;

    @ColumnInfo(name = "date_debut")
    private String dateDebut;

    @ColumnInfo(name = "date_fin_prevue")
    private String dateFinPrevue;

    @ColumnInfo(name = "statut")
    private String statut;

    @ColumnInfo(name = "type_credit")
    private String typeCredit;

    @ColumnInfo(name = "objet_credit")
    private String objetCredit;

    @ColumnInfo(name = "agent_id")
    private long agentId;

    // Constructeur principal — utilisé par Room
    public Credit(long id, long clientId, double montantPrincipal,
                  double tauxInteret, double montantTotalDu,
                  double montantRembourse, int dureeMois,
                  String frequencePaiement, String dateDebut,
                  String dateFinPrevue, String statut,
                  String typeCredit, String objetCredit, long agentId) {
        this.id = id;
        this.clientId = clientId;
        this.montantPrincipal = montantPrincipal;
        this.tauxInteret = tauxInteret;
        this.montantTotalDu = montantTotalDu;
        this.montantRembourse = montantRembourse;
        this.dureeMois = dureeMois;
        this.frequencePaiement = frequencePaiement;
        this.dateDebut = dateDebut;
        this.dateFinPrevue = dateFinPrevue;
        this.statut = statut;
        this.typeCredit = typeCredit;
        this.objetCredit = objetCredit;
        this.agentId = agentId;
    }

    @Ignore
    public Credit(long clientId, double montantPrincipal,
                  double tauxInteret, int dureeMois,
                  String frequencePaiement, String dateDebut) {
        this.clientId = clientId;
        this.montantPrincipal = montantPrincipal;
        this.tauxInteret = tauxInteret;
        this.dureeMois = dureeMois;
        this.frequencePaiement = frequencePaiement;
        this.dateDebut = dateDebut;
        this.montantRembourse = 0.0;
        this.statut = "EN_ATTENTE";
        this.montantTotalDu = calculerMontantTotal(montantPrincipal, tauxInteret, dureeMois);
    }

    public static double calculerMontantTotal(double principal, double tauxAnnuel, int dureeMois) {
        return principal * (1 + (tauxAnnuel / 100) * (dureeMois / 12.0));
    }

    public double getMontantRestant()  { return montantTotalDu - montantRembourse; }
    public double getMensualite()      { return dureeMois == 0 ? 0 : montantTotalDu / dureeMois; }
    public boolean estSolde()          { return montantRembourse >= montantTotalDu; }

    public long getId()                          { return id; }
    public void setId(long id)                   { this.id = id; }
    public long getClientId()                    { return clientId; }
    public void setClientId(long clientId)       { this.clientId = clientId; }
    public double getMontantPrincipal()          { return montantPrincipal; }
    public void setMontantPrincipal(double m)    { this.montantPrincipal = m; }
    public double getTauxInteret()               { return tauxInteret; }
    public void setTauxInteret(double t)         { this.tauxInteret = t; }
    public double getMontantTotalDu()            { return montantTotalDu; }
    public void setMontantTotalDu(double m)      { this.montantTotalDu = m; }
    public double getMontantRembourse()          { return montantRembourse; }
    public void setMontantRembourse(double m)    { this.montantRembourse = m; }
    public int getDureeMois()                    { return dureeMois; }
    public void setDureeMois(int d)              { this.dureeMois = d; }
    public String getFrequencePaiement()         { return frequencePaiement; }
    public void setFrequencePaiement(String f)   { this.frequencePaiement = f; }
    public String getDateDebut()                 { return dateDebut; }
    public void setDateDebut(String d)           { this.dateDebut = d; }
    public String getDateFinPrevue()             { return dateFinPrevue; }
    public void setDateFinPrevue(String d)       { this.dateFinPrevue = d; }
    public String getStatut()                    { return statut; }
    public void setStatut(String statut)         { this.statut = statut; }
    public String getTypeCredit()                { return typeCredit; }
    public void setTypeCredit(String t)          { this.typeCredit = t; }
    public String getObjetCredit()               { return objetCredit; }
    public void setObjetCredit(String o)         { this.objetCredit = o; }
    public long getAgentId()                     { return agentId; }
    public void setAgentId(long a)               { this.agentId = a; }
}