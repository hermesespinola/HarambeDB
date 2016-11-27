package database;

import java.io.IOException;

/**
* This is a special exeption that is thrown when something related to HarambeDB
* goes wrong
*/
public class HarambException extends IOException {
  private static final long serialVersionUID = 27L;
  public HarambException(String message) {
    super(message);
  }
  public HarambException(Exception e) {
    super(e);
  }

  public String toString() {
    return this.getMessage();
  }
}
