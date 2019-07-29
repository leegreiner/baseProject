package edu.duke.rs.baseProject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class StubServletOutputStream extends ServletOutputStream {
  public ByteArrayOutputStream baos = new ByteArrayOutputStream();

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setWriteListener(WriteListener listener) {
  }

  @Override
  public void write(int b) throws IOException {
    baos.write(b);
  }

  public String toString() {
    return baos.toString();
  }
}
