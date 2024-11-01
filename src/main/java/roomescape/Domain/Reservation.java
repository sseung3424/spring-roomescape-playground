package roomescape.Domain;

public class Reservation {
  private Long id;
  private String name;
  private String date;
  private String time;

  public Reservation() {}

  public Reservation(Long id, String name, String date, String time) {
    this.id = id;
    this.name = name;
    this.date = date;
    this.time = time;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDate() {
    return date;
  }

  public String getTime() {
    return time;
  }

  public static Reservation toEntity (Reservation reservation, Long id) {
    return new Reservation(
        id, reservation.getName(), reservation.getDate(), reservation.getTime()
    );
  }
}
