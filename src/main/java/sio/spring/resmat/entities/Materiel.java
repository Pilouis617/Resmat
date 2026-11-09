package sio.spring.resmat.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Materiel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nom;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    // Stock total : les reservations acceptees serviront a calculer la disponibilite.
    @Positive
    @Column(nullable = false)
    private int quantite = 1;

    // null : utiliser la limite du type de materiel, 0 : aucune limite.
    @PositiveOrZero
    private Integer dureeMaxEmprunt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutMateriel statut = StatutMateriel.DISPONIBLE;

    // Plusieurs materiels peuvent appartenir au meme type (categorie).
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_materiel_id", nullable = false)
    private TypeMateriel typeMateriel;

}
