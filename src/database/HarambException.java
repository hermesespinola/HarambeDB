package database;

import java.io.IOException;

public class HarambException extends IOException {
  private static final long serialVersionUID = 27L;
  public HarambException(String message) {
    super(message);
  }
  public HarambException(Exception e) {
    super(e.getMessage());
  }
}
