import space.themelon.simplerelay.SimpleRelay;

import java.io.IOException;

import static java.lang.System.out;

public class Main {
  public static void main(String[] args) throws IOException {
    int port = 1090;
    out.println("Running on port " + port);
    new SimpleRelay(port);
  }
}