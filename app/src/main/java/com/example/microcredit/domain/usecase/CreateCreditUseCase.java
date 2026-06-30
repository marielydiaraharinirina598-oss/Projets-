package com.example.microcredit.domain.usecase;

import com.example.microcredit.data.entity.Credit;
import com.example.microcredit.data.repository.CreditRepository;

/**
 * CreateCreditUseCase — règle métier pour créer un crédit
 *
 * Principe SOLID (SRP) : une seule responsabilité = créer un crédit valide
 * Principe SOLID (OCP) : on peut étendre sans modifier cette classe
 */
public class CreateCreditUseCase {

    private final CreditRepository repository;

    public CreateCreditUseCase(CreditRepository repository) {
        this.repository = repository;
    }

    /**
     * Crée un crédit après validation des règles métier
     * @return message d'erreur si invalide, null si tout est OK
     */
    public String execute(Credit credit) {

        // ── Validation métier ──────────────────────────
        if (credit.getMontantPrincipal() <= 0) {
            return "Le montant doit être supérieur à 0";
        }
        if (credit.getTauxInteret() < 0) {
            return "Le taux d'intérêt ne peut pas être négatif";
        }
        if (credit.getDureeMois() <= 0) {
            return "La durée doit être supérieure à 0 mois";
        }
        if (credit.getClientId() <= 0) {
            return "Client invalide";
        }

        // ── Calcul automatique du montant total ────────
        double montantTotal = Credit.calculerMontantTotal(
                credit.getMontantPrincipal(),
                credit.getTauxInteret(),
                credit.getDureeMois()
        );
        credit.setMontantTotalDu(montantTotal);
        credit.setStatut("ACTIF");

        // ── Sauvegarde ─────────────────────────────────
        repository.insert(credit);
        return null; // null = succès
    }
}