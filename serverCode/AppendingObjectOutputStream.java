import java.io.*;

// Source - https://stackoverflow.com/a/1195078
// Posted by Andreas Dolk, modified by community. See post 'Timeline' for change history
// Retrieved 2026-02-27, License - CC BY-SA 3.0

public class AppendingObjectOutputStream extends ObjectOutputStream {

  public AppendingObjectOutputStream(OutputStream out) throws IOException {
    super(out);
  }

  @Override
  protected void writeStreamHeader() throws IOException {
    // do not write a header, but reset:
    // this line added after another question
    // showed a problem with the original
    reset();
  }

}