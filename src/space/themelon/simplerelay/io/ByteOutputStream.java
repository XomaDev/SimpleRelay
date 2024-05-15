package space.themelon.simplerelay.io;

import java.io.IOException;
import java.io.OutputStream;

public class ByteOutputStream extends OutputStream {

  private final OutputStream sink;

  public ByteOutputStream(OutputStream sink) {
    this.sink = sink;
  }

  public void writeString(byte[] bytes) throws IOException {
    writeInt(bytes.length);
    sink.write(bytes);
  }

  public void writeInt(int n) throws IOException {
    sink.write(n >> 24);
    sink.write(n >> 16);
    sink.write(n >> 8);
    sink.write(n);
  }

  @Override
  public void write(int i) throws IOException {
    sink.write(i);
  }

  @Override
  public void write(byte[] bytes) throws IOException {
    sink.write(bytes);
  }

  @Override
  public void write(byte[] bytes, int off, int len) throws IOException {
    sink.write(bytes, off, len);
  }

  @Override
  public void flush() throws IOException {
    sink.flush();
  }

  @Override
  public void close() throws IOException {
    sink.close();
  }
}
