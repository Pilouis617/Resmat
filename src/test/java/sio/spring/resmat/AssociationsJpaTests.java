package sio.spring.resmat;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import sio.spring.resmat.entities.*;
import sio.spring.resmat.repositories.*;

@SpringBootTest
@Transactional
class AssociationsJpaTests {
    @Autowired UtilisateurRepository utilisateurs;
    @Autowired TypeMaterielRepository types;
    @Autowired MaterielRepository materiels;
    @Autowired ReservationRepository reservations;
    @Autowired EmpruntRepository emprunts;
    @Autowired EntityManager entityManager;

    private Reservation creerReservation() {
        Utilisateur user = new Utilisateur();
        user.setNom("Benoit");
        user.setPrenom("Pierre-Louis");
        user.setEmail("pierre-louis@example.test");
        user.setLogin("pilouis");
        user.setMotDePasse("valeur-de-test-non-utilisable-pour-connexion");
        utilisateurs.save(user);

        TypeMateriel type = new TypeMateriel();
        type.setNom("Camera");
        type = types.save(type);

        Materiel materiel = new Materiel();
        materiel.setNom("Camera de la salle multimedia");
        materiel.setTypeMateriel(type);
        materiels.save(materiel);

        Reservation reservation = new Reservation();
        reservation.setEmprunteur(user);
        reservation.setMateriel(materiel);
        reservation.setDateDebut(LocalDate.now());
        reservation.setDateFin(LocalDate.now().plusDays(2));
        return reservations.saveAndFlush(reservation);
    }

    private Emprunt nouvelEmprunt(Reservation reservation) {
        Emprunt emprunt = new Emprunt();
        emprunt.setReservation(reservation);
        emprunt.setDateRemise(LocalDate.now());
        emprunt.setDateRetourPrevue(reservation.getDateFin());
        return emprunt;
    }

    @Test
    void enregistrerEtRelireLesCinqEntitesAvecLeursAssociations() {
        Reservation reservation = creerReservation();
        reservation.setStatut(StatutReservation.ACCEPTEE);
        Emprunt emprunt = emprunts.saveAndFlush(nouvelEmprunt(reservation));
        Long id = emprunt.getId();
        entityManager.clear();

        Emprunt relu = emprunts.findById(id).orElseThrow();
        assertEquals("pilouis", relu.getReservation().getEmprunteur().getLogin());
        assertEquals(Role.ROLE_USER, relu.getReservation().getEmprunteur().getRole());
        assertEquals("Camera", relu.getReservation().getMateriel().getTypeMateriel().getNom());
        assertEquals(StatutReservation.ACCEPTEE, relu.getReservation().getStatut());
        assertNotNull(relu.getReservation().getMateriel().getTypeMateriel().getId());
        assertEquals(1, utilisateurs.count());
        assertEquals(1, types.count());
        assertEquals(1, materiels.count());
        assertEquals(1, reservations.count());
        assertEquals(1, emprunts.count());
    }

    @Test
    void refuserDeuxEmpruntsPourLaMemeReservation() {
        Reservation reservation = creerReservation();
        emprunts.saveAndFlush(nouvelEmprunt(reservation));
        assertThrows(DataIntegrityViolationException.class,
                () -> emprunts.saveAndFlush(nouvelEmprunt(reservation)));
    }

    @Test
    void conserverLeMaterielReferenceDansHistorique() {
        Reservation reservation = creerReservation();
        Long id = reservation.getMateriel().getId();
        entityManager.clear();
        assertThrows(DataIntegrityViolationException.class, () -> {
            materiels.deleteById(id);
            materiels.flush();
        });
    }

    @Test
    void refuserUneReservationAvecDatesInversees() {
        Reservation reservation = creerReservation();
        reservation.setDateFin(reservation.getDateDebut().minusDays(1));
        assertThrows(ConstraintViolationException.class, () -> reservations.flush());
    }

    @Test
    void accepterUneReservationSurUneSeuleJournee() {
        Reservation reservation = creerReservation();
        reservation.setDateFin(reservation.getDateDebut());
        assertDoesNotThrow(() -> reservations.flush());
    }

    @Test
    void distinguerUnRetardUnRetourEtUneEcheanceAujourdhui() {
        Emprunt emprunt = new Emprunt();
        emprunt.setDateRetourPrevue(LocalDate.now().minusDays(1));
        assertTrue(emprunt.isEnRetard());
        emprunt.setDateRetourEffective(LocalDate.now());
        assertFalse(emprunt.isEnRetard());
        emprunt.setDateRetourEffective(null);
        emprunt.setDateRetourPrevue(LocalDate.now());
        assertFalse(emprunt.isEnRetard());
    }
}
