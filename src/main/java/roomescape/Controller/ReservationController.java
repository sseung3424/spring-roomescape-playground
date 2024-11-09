package roomescape.Controller;

import java.net.URI;
import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
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
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public ReservationController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @PostMapping("/reservations")
  public ResponseEntity<Reservation> create(@RequestBody Reservation reservation) {

    if (reservation.getName() == null || reservation.getName().isEmpty() ||
        reservation.getDate() == null || reservation.getDate().isEmpty() ||
        reservation.getTime() == null || reservation.getTime().isEmpty()) {
      throw new NotFoundReservationException("Reservation not found");
    }

    KeyHolder keyHolder = new GeneratedKeyHolder();
    String sql = "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)";
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
      ps.setString(1, reservation.getName());
      ps.setString(2, reservation.getDate());
      ps.setString(3, reservation.getTime());
      return ps;
    }, keyHolder);

    Long id = keyHolder.getKey().longValue();

    Reservation savedReservation = jdbcTemplate.queryForObject(
        "SELECT * FROM reservation WHERE id = ?",
        reservationRowMapper,
        id
    );

    return ResponseEntity.created(URI.create("/reservations/" + savedReservation.getId()))
        .body(savedReservation);
  }

  @GetMapping("/reservations")
  public ResponseEntity<List<Reservation>> read() {
    List<Reservation> reservations = jdbcTemplate.query(
        "SELECT * FROM reservation",
        reservationRowMapper
    );

    return ResponseEntity.ok().body(reservations);
  }

  @DeleteMapping("/reservations/{id}")
  public ResponseEntity<Void> delete(@PathVariable long id) {

    List<Reservation> reservations = jdbcTemplate.query(
        "SELECT * FROM reservation WHERE id = ?",
        reservationRowMapper,
        id
    );

    if (reservations.isEmpty()) {
      throw new NotFoundReservationException("Reservation not found");
    }

    jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", id);

    return ResponseEntity.noContent().build();
  }

  private final RowMapper<Reservation> reservationRowMapper = (resultSet, rowNum) -> {
    return new Reservation(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("date"),
        resultSet.getString("time")
    );
  };

  @ExceptionHandler(NotFoundReservationException.class)
  public ResponseEntity<Void> handleException(NotFoundReservationException e) {
    return ResponseEntity.badRequest().build();
  }
}
