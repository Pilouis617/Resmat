package sio.spring.resmat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sio.spring.resmat.entities.Emprunt;

public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
}
