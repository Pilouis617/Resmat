package sio.spring.resmat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sio.spring.resmat.entities.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
