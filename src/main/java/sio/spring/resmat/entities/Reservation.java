package sio.spring.resmat.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateDebut;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateFin;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateResa = LocalDate.now();

    @Size(max = 1000)
    @Column(length = 1000)
    private String commentaire;

    @Size(max = 1000)
    @Column(length = 1000)
    private String motifRefus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emprunteur_id", nullable = false)
    private Utilisateur emprunteur;

    // Renseigne plus tard par le service lors du traitement de la demande.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestionnaire_id")
    private Utilisateur gestionnaire;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    @AssertTrue(message = "La date de fin ne peut pas preceder la date de debut.")
    public boolean isDatesValides() {
        return dateDebut == null || dateFin == null || !dateFin.isBefore(dateDebut);
    }

}
