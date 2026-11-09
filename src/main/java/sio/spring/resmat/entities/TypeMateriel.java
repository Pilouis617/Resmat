package sio.spring.resmat.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TypeMateriel {
    // On conserve le type UUID de ton entite d'origine.
    @Id
    private UUID id = UUID.randomUUID();

    @NotBlank
    @Size(max = 150)
    @Column(length = 150, nullable = false)
    private String nom;

    @Size(max = 65000)
    @Column(length = 65000)
    private String description;

    // 0 signifie qu'aucune limite n'est definie.
    @PositiveOrZero
    private int dureeMaxEmprunt;

    @Column(nullable = false)
    private boolean auCatalogue = true;

}
