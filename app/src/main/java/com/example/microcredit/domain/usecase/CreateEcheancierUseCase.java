package com.example.microcredit.domain.usecase;

import com.example.microcredit.data.entity.Echeancier;
import com.example.microcredit.data.repository.CreditRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * CreateEcheancierUseCase — génère automatiquement toutes les lignes
 * du tableau de remboursement pour un crédit donné
 *
 * Exemple : crédit 1 000 000 Ar sur 6 mois à 2% mensuel
 * → génère 6 objets Echeancier avec dates et montants calculés
 */
public class CreateEcheancierUseCase {

    private final CreditRepository repository;

    public CreateEcheancierUseCase(CreditRepository repository) {
        this.repository = repository;
    }

    /**
     * Génère et sauvegarde toutes les échéances d'un crédit
     *
     * @param creditId         id du crédit
     * @param montantPrincipal montant emprunté
     * @param tauxMensuel      taux d'intérêt mensuel (ex: 2.0 pour 2%)
     * @param dureeMois        nombre de mensualités
     * @param dateDebut        date de début "YYYY-MM-DD"
     */
    public void execute(long creditId, double montantPrincipal,
                        double tauxMensuel, int dureeMois, String dateDebut) {

        List<Echeancier> echeanciers = genererEcheances(
                creditId, montantPrincipal, tauxMensuel, dureeMois, dateDebut
        );
        repository.insertEcheances(echeanciers);
    }

    /**
     * Calcule le tableau d'amortissement (méthode des annuités constantes)
     *
     * Formule mensualité constante :
     *   M = P × [r(1+r)^n] / [(1+r)^n - 1]
     *   où P = principal, r = taux mensuel, n = durée
     */
    private List<Echeancier> genererEcheances(long creditId, double principal,
                                              double tauxMensuel, int dureeMois,
                                              String dateDebut) {
        List<Echeancier> liste = new ArrayList<>();
        double r = tauxMensuel / 100.0;  // taux en décimal
        double capitalRestant = principal;

        // Calcul de la mensualité constante
        double mensualite;
        if (r == 0) {
            mensualite = principal / dureeMois;  // sans intérêt
        } else {
            mensualite = principal * (r * Math.pow(1 + r, dureeMois))
                    / (Math.pow(1 + r, dureeMois) - 1);
        }

        // Parsing de la date de début
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(dateDebut));
        } catch (Exception e) {
            cal = Calendar.getInstance();
        }

        // Génération de chaque ligne de l'échéancier
        for (int i = 1; i <= dureeMois; i++) {
            cal.add(Calendar.MONTH, 1);  // +1 mois à chaque échéance
            String dateEcheance = sdf.format(cal.getTime());

            double interetDuMois = capitalRestant * r;
            double capitalDuMois = mensualite - interetDuMois;

            // Dernière échéance : on ajuste pour éviter les erreurs d'arrondi
            if (i == dureeMois) {
                capitalDuMois = capitalRestant;
                mensualite = capitalDuMois + interetDuMois;
            }

            capitalRestant -= capitalDuMois;
            if (capitalRestant < 0) capitalRestant = 0;

            Echeancier echeancier = new Echeancier(
                    creditId,
                    i,                           // numéro d'échéance
                    dateEcheance,                // date prévue
                    arrondir(capitalDuMois),     // part capital
                    arrondir(interetDuMois),     // part intérêt
                    arrondir(capitalRestant)     // capital restant dû
            );
            liste.add(echeancier);
        }
        return liste;
    }

    /** Arrondit à 2 décimales */
    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}