package sio.spring.resmat.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateResa;

    private String commentaire;
    private String avis;
    private String motivationAvis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "emprunteur_id")
    private Utilisateur emprunteur;

    // 0,1 sur votre schéma : renseigné quand un gestionnaire traite la demande
    @ManyToOne
    @JoinColumn(name = "gestionnaire_id")
    private Utilisateur gestionnaire;

    @ManyToOne(optional = false)
    @JoinColumn(name = "materiel_id")
    private Materiel materiel;
}