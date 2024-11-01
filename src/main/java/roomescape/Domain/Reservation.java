package roomescape.Domain;

public class Reservation {
  private Integer id;
  private String name;
  private String date;
  private String time;

  public Reservation() {}

  public Reservation(Integer id, String name, String date, String time) {
    this.id = id;
    this.name = name;
    this.date = date;
    this.time = time;
  }

  public Integer getId() {
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
}
