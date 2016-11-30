package hdb;

import java.io.IOException;

/**
* This is a special exeption that is thrown when something related to HarambeDB
* goes wrong.
*
* <p>This class is a member of the
* <a href="{@docRoot}/index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
*/
public class HarambException extends RuntimeException {
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
