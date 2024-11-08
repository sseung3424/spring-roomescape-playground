package roomescape.Controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.Domain.Reservation;
import roomescape.Exception.NotFoundReservationException;

@RestController
public class ReservationController {
  private List<Reservation> reservations = new ArrayList<>();
  private AtomicLong index = new AtomicLong(1);

  @PostMapping("/reservations")
  public ResponseEntity<Reservation> create(@RequestBody Reservation reservation) {
    Reservation newReservation = Reservation.toEntity(reservation, index.getAndIncrement());
    if(newReservation.getName().isEmpty() || newReservation.getDate().isEmpty()
    || newReservation.getTime().isEmpty()) {
      throw new NotFoundReservationException("Reservation not found");
    }
    reservations.add(newReservation);
    return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId()))
        .body(newReservation);
  }

  @GetMapping("/reservations")
  public ResponseEntity<List<Reservation>> read() {
    return ResponseEntity.ok().body(reservations);
  }

  @DeleteMapping("/reservations/{id}")
  public ResponseEntity<Void> delete(@PathVariable long id) {
    Reservation reservation = reservations.stream()
        .filter(it -> Objects.equals(it.getId(), id))
        .findFirst()
        .orElseThrow(() -> new NotFoundReservationException("Deletion not found"));

    reservations.remove(reservation);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(NotFoundReservationException.class)
  public ResponseEntity<Void> handleException(NotFoundReservationException e) {
    return ResponseEntity.badRequest().build();
  }
}
