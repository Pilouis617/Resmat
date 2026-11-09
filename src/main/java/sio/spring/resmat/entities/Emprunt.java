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
public class Emprunt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateRemise;

    @NotNull
    @Column(nullable = false)
    private LocalDate dateRetourPrevue;

    private LocalDate dateRetourEffective;

    @Size(max = 1000)
    @Column(length = 1000)
    private String commentaires;

    private boolean degradation;

    // Une reservation donne lieu au maximum a un emprunt.
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Transient
    public boolean isEnRetard() {
        return dateRetourEffective == null && dateRetourPrevue != null
                && dateRetourPrevue.isBefore(LocalDate.now());
    }

    @AssertTrue(message = "Le retour effectif ne peut pas preceder la remise.")
    public boolean isRetourValide() {
        return dateRetourEffective == null || dateRemise == null
                || !dateRetourEffective.isBefore(dateRemise);
    }

}
