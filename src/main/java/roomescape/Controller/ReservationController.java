package roomescape.Controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.Domain.Reservation;

@RestController
public class ReservationController {
  private List<Reservation> reservations = new ArrayList<>();

  public ReservationController() {
    Reservation reservation1 = new Reservation(1, "브라운", "2023-01-01", "10:00");
    Reservation reservation2 = new Reservation(2, "브라운", "2023-01-02", "11:00");
    Reservation reservation3 = new Reservation(3, "브라운", "2023-01-03", "12:00");

    reservations.add(reservation1);
    reservations.add(reservation2);
    reservations.add(reservation3);
  }

  @GetMapping("/reservations")
  public ResponseEntity<List<Reservation>> read() {
    return ResponseEntity.ok().body(reservations);
  }
}
