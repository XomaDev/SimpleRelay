package space.themelon.simplerelay.io;

import java.io.IOException;
import java.io.InputStream;

public class ByteInputStream extends InputStream {

  private final InputStream source;

  public ByteInputStream(InputStream source) {
    this.source = source;
  }

  public byte[] readString() throws IOException {
    int stringLength = readInt();
    byte[] string = new byte[stringLength];
    int nRead = read(string);
    if (nRead < stringLength) {
      throw new IOException("Unable to fully read string " + nRead + '/' + stringLength);
    }
    return string;
  }

  public int readInt() throws IOException {
    return ((byte) source.read() & 255) << 24 |
        ((byte) source.read() & 255) << 16 |
        ((byte) source.read() & 255) << 8 |
        (byte) source.read() & 255;
  }

  @Override
  public int read() throws IOException {
    return source.read();
  }

  @Override
  public int read(byte[] bytes) throws IOException {
    return source.read(bytes);
  }

  @Override
  public int read(byte[] bytes, int off, int len) throws IOException {
    return source.read(bytes, off, len);
  }

  @Override
  public long skip(long n) throws IOException {
    return source.skip(n);
  }

  @Override
  public int available() throws IOException {
    return source.available();
  }

  @Override
  public void close() throws IOException {
    source.close();
  }

  @Override
  public synchronized void mark(int n) {
    source.mark(n);
  }

  @Override
  public synchronized void reset() throws IOException {
    source.reset();
  }

  @Override
  public boolean markSupported() {
    return source.markSupported();
  }
}
