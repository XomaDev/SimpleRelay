package space.themelon.simplerelay;

import space.themelon.simplerelay.io.ByteInputStream;
import space.themelon.simplerelay.io.ByteOutputStream;
import space.themelon.simplerelay.io.Safety;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.System.out;
import static space.themelon.simplerelay.io.Safety.ioSafe;
import static space.themelon.simplerelay.io.Safety.ioSafeThread;

public class SimpleRelay {

  public SimpleRelay(int port) throws IOException {
    out.println("Serving on port " + port);
    try (ServerSocket server = new ServerSocket(port)) {
      for (;;) {
        Socket client = (Socket) ioSafe((Safety.IORunnable<Socket>) server::accept);
        if (client == null) {
          break;
        }
        ioSafeThread(() -> {
          serveClient(client);
          return null;
        });
      }
    }
  }

  private void serveClient(Socket client) throws IOException {
    ByteInputStream input = new ByteInputStream(client.getInputStream());
    int version = input.read();
    if (version != 0x00) {
      throw new IOException("Unknown version " + version);
    }
    int sendPort = input.readInt();
    out.println("Send port " + sendPort);

    ByteOutputStream output = new ByteOutputStream(client.getOutputStream());

    Socket senderSocket = acceptSocket(sendPort, output);
    if (senderSocket == null) {
      return;
    }
    int receivePort = input.readInt();
    out.println("Receive port " + sendPort);

    Socket receiverPort = acceptSocket(receivePort, output);
    if (receiverPort == null) {
      return;
    }
    relay(senderSocket, receiverPort);
  }

  private Socket acceptSocket(int port, ByteOutputStream bos) throws IOException {
    ServerSocket server = new ServerSocket(port);
    try {
      bos.write(1);
      bos.writeInt(server.getLocalPort());
      Socket socket = server.accept();
      server.close();
      return socket;
    } catch (IOException e) {
      out.println(e.getMessage());
      bos.write(0);
      bos.writeString(e.getMessage().getBytes());
      return null;
    }
  }

  public static void relay(Socket host, Socket client) {
    Runnable onError = () -> {
      Safety.safeClose(host);
      Safety.safeClose(client);
    };
    // [Read] Host Socket -> [Write] Client Socket
    Safety.ioSafeThread(() -> {
      relay(host.getInputStream(), client.getOutputStream());
      return null;
    }, onError);

    // [Read] Client Socket -> [Write] Host Socket
    Safety.ioSafeThread(() -> {
      relay(client.getInputStream(), host.getOutputStream());
      return null;
    }, onError);
  }

  private static void relay(InputStream input, OutputStream output) throws IOException {
    byte[] bytes = new byte[4096];
    int read;
    while ((read = input.read(bytes)) != -1) {
      output.write(bytes, 0, read);
      output.flush();
    }
    throw new IOException("Socket Closed");
  }
}
