package space.themelon.simplerelay.io;

import java.io.Closeable;
import java.io.IOException;

import static java.lang.System.out;

public class Safety {

  public static final boolean DEBUG = true;

  public interface IORunnable<T> {
    T run() throws IOException, InterruptedException;
  }

  public static Object ioSafe(IORunnable<?> runnable) {
    try {
      return runnable.run();
    } catch (IOException | InterruptedException e) {
      if (DEBUG) {
        e.printStackTrace();
      } else {
        out.println(e.getMessage());
      }
      return null;
    }
  }

  public static void ioSafeThread(IORunnable<Void> runnable) {
    ioSafeThread(runnable, null);
  }

  public static void ioSafeThread(IORunnable<Void> runnable, Runnable onError) {
    new Thread(() -> {
      try {
        runnable.run();
      } catch (IOException | InterruptedException e) {
        if (onError != null) {
          onError.run();
        } else if (DEBUG) {
          e.printStackTrace();
        } else {
          out.println(e.getMessage());
        }
      }
    }).start();
  }

  public static void safeClose(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException ignored) {

    }
  }
}
