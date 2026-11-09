package sio.spring.resmat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sio.spring.resmat.entities.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
}
