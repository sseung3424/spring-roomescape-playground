package roomescape.Controller;

import java.net.URI;
import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.Domain.Time;
import roomescape.Exception.NotFoundTimeException;

@RestController
public class TimeController {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public TimeController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @PostMapping("/times")
  public ResponseEntity<Time> create(@RequestBody Time time) {

    if (time.getTime() == null || time.getTime().isEmpty()) {
      throw new NotFoundTimeException("Time not found");
    }

    KeyHolder keyHolder = new GeneratedKeyHolder();
    String sql = "INSERT INTO time (time) VALUES (?)";
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
      ps.setString(1, time.getTime());
      return ps;
    }, keyHolder);

    Long id = keyHolder.getKey().longValue();

    Time savedTime = jdbcTemplate.queryForObject(
        "SELECT * FROM time WHERE id = ?",
        timeRowMapper,
        id
    );

    return ResponseEntity.created(URI.create("/times/" + savedTime.getId()))
        .body(savedTime);
  }

  @GetMapping("/times")
  public ResponseEntity<List<Time>> read() {
    List<Time> times = jdbcTemplate.query(
        "SELECT * FROM time",
        timeRowMapper
    );

    return ResponseEntity.ok().body(times);
  }

  @DeleteMapping("/times/{id}")
  public ResponseEntity<Void> delete(@PathVariable long id) {

    List<Time> times = jdbcTemplate.query(
        "SELECT * FROM time WHERE id = ?",
        timeRowMapper,
        id
    );

    if (times.isEmpty()) {
      throw new NotFoundTimeException("Time not found");
    }

    jdbcTemplate.update("DELETE FROM time WHERE id = ?", id);

    return ResponseEntity.noContent().build();
  }

  private final RowMapper<Time> timeRowMapper = (resultSet, rowNum) -> {
    return new Time(
        resultSet.getLong("id"),
        resultSet.getString("time")
    );
  };

  @ExceptionHandler(NotFoundTimeException.class)
  public ResponseEntity<Void> handleException(NotFoundTimeException e) {
    return ResponseEntity.badRequest().build();
  }
}
