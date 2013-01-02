package org.yajul.io;

import java.io.*;

/**
 * A slow writer...
 * <br>
 * User: josh
 * Date: 6/29/11
 * Time: 4:02 PM
 */
public class SlowOutput implements Runnable {
    private int limit;
    private PrintStream ps;
    private long sleepMillis;
    private PipedInputStream slowInput;

    public SlowOutput(int limit, long sleepMillis) throws IOException {
        this.limit = limit;
        this.sleepMillis = sleepMillis;
        slowInput = new PipedInputStream();
        final PipedOutputStream pipe = new PipedOutputStream(slowInput);
        ps = new PrintStream(pipe);
    }

    public InputStream getSlowInput() {
        return slowInput;
    }

    public void run() {
        writeStuff(limit,ps,sleepMillis);
    }

    public static void writeStuff(int limit, PrintStream ps, long sleepMillis) {
        for (int i = 0; i < limit; i++) {
            ps.println("i=" + i);
            ps.flush();
            if (sleepMillis > 0) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (Exception ignore) {
                }
            }
        }
        ps.close();
    }
}
