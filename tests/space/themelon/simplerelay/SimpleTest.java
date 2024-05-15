package space.themelon.simplerelay;

import space.themelon.simplerelay.io.ByteInputStream;
import space.themelon.simplerelay.io.ByteOutputStream;
import space.themelon.simplerelay.io.Safety;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static java.lang.System.out;

public class SimpleTest {
  public static void main(String[] args) throws IOException {
    int port = 1090;
    new Thread(() -> {
      try {
        new SimpleRelay(port);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).start();

    Socket requestSocket = new Socket("localhost", port);

    OutputStream output = requestSocket.getOutputStream();
    output.write(0x00); // version

    ByteInputStream input = new ByteInputStream(requestSocket.getInputStream());

    // when we set port to 0, it assigns a port
    output.write(new byte[] { 0, 0, 0, 0 }); // send port

    if (input.read() == 0) {
      String errorMessage = new String(input.readString());
      out.println("Unable to allocate send port " + errorMessage);
      Safety.safeClose(requestSocket);
      return;
    }
    int sendPort = input.readInt();
    Socket sendSocket = new Socket("localhost", sendPort);
    sendSocket.getOutputStream().write("hello world".getBytes());
    sendSocket.close();

    output.write(new byte[] { 0, 0, 0, 0 }); // receive port

    if (input.read() == 0) {
      String errorMessage = new String(input.readString());
      out.println("Unable to allocate receive port " + errorMessage);
      Safety.safeClose(requestSocket);
      return;
    }
    int receivePort = input.readInt();

    out.println("Ports allocated send port " + sendPort + " and receive port " + receivePort);

    Socket receiveSocket = new Socket("localhost", receivePort);
    for (InputStream in = receiveSocket.getInputStream();;) {
      int n = in.read();
      if (n == -1) {
        break;
      }
      out.print((char) n);
    }
    requestSocket.close();
    System.exit(0);
  }
}
