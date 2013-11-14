package org.yajul.io;

import junit.framework.TestCase;
import org.yajul.util.Copier;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Tests org.yajul.io classes:
 * <ul>
 * <li>StreamCopier</li>
 * <li>EchoInputStream</li>
 * <li>TeeOutputStream</li>
 * <li>ByteCountingInputStream</li>
 * <li>ByteCountingOutputStream</li>
 * </ul>
 * User: josh
 * Date: Sep 22, 2002
 * Time: 12:38:27 AM
 */
public class StreamCopierTest extends TestCase {
    private static final Logger log = Logger.getLogger(StreamCopierTest.class.getName());

    private static final byte[] BYTES = "12345678901234567890".getBytes();

    public StreamCopierTest(String name) {
        super(name);
    }



    private static class CopyCallbacks implements Copier.Callback {
        private boolean startCalled;
        private boolean endCalled;
        private boolean beforeWriteCalled;

        public void startOfStream() {
            startCalled = true;
        }

        public boolean beforeWrite(byte[] buf, int length, int total) {
            beforeWriteCalled = true;
            return true;
        }

        public boolean beforeWrite(char[] buf, int length, int total) {
            return true;
        }

        public void endOfStream(int total) {
            endCalled = true;
        }

        public boolean isStartCalled() {
            return startCalled;
        }

        public boolean isEndCalled() {
            return endCalled;
        }

        public boolean isBeforeWriteCalled() {
            return beforeWriteCalled;
        }
    }

    public void testStreamCopy() {
        byte[] a = BYTES;
        InputStream in = new ByteArrayInputStream(a);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        final CopyCallbacks callbacks = new CopyCallbacks();
        StreamCopier copier = new StreamCopier(in, out, 16, Copier.UNLIMITED, callbacks);
        copier.run();
        TestCase.assertTrue(Arrays.equals(a, out.toByteArray()));
        TestCase.assertTrue(callbacks.isStartCalled());
        TestCase.assertTrue(callbacks.isBeforeWriteCalled());
        TestCase.assertTrue(callbacks.isEndCalled());
    }

    public void testEchoInputStream() throws IOException {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream echo = new ByteArrayOutputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream in = new EchoInputStream(input, echo);
        StreamCopier.unsyncCopy(in, output, 8);
        TestCase.assertTrue(Arrays.equals(bytes, output.toByteArray()));
        TestCase.assertTrue(Arrays.equals(bytes, echo.toByteArray()));
    }

    public void test2OutputStreams() throws IOException {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream echo = new ByteArrayOutputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OutputStream out = new TeeOutputStream(output, echo);
        StreamCopier.unsyncCopy(input, out, 8);
        byte[] outputbytes = output.toByteArray();
        TestCase.assertTrue(Arrays.equals(bytes, outputbytes));
        byte[] echobytes = echo.toByteArray();
        TestCase.assertTrue(Arrays.equals(bytes, echobytes));
    }

    public void test3OutputStreams() throws IOException {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream[] streams = new ByteArrayOutputStream[3];
        for (int i = 0; i < streams.length; i++) {
            streams[i] = new ByteArrayOutputStream();
        }

        OutputStream out = new TeeOutputStream(streams);
        StreamCopier.unsyncCopy(input, out, 8);

        for (ByteArrayOutputStream stream : streams) {
            byte[] outputbytes = stream.toByteArray();
            TestCase.assertTrue(Arrays.equals(bytes, outputbytes));
        }
    }

    public void testByteCountingInputStream() throws IOException {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteCountingInputStream in = new ByteCountingInputStream(input);
        StreamCopier.unsyncCopy(in, output, 8);
        TestCase.assertTrue(Arrays.equals(bytes, output.toByteArray()));
        TestCase.assertEquals(bytes.length, in.getByteCount());
    }

    public void testByteCountingOutputStream() throws IOException {
        byte[] bytes = BYTES;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteCountingOutputStream out = new ByteCountingOutputStream(output);
        StreamCopier.unsyncCopy(input, out, 8);
        TestCase.assertTrue(Arrays.equals(bytes, output.toByteArray()));
        TestCase.assertEquals(bytes.length, out.getByteCount());
    }

    public void testHexDumpOutputStream() throws IOException {
        byte[] bytes = new byte[50];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) i;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HexDumpOutputStream out = new HexDumpOutputStream(output, 16);
        StreamCopier.unsyncCopy(input, out, 8);
        out.flush();
        // TODO: Test the result.
    }


    public void testStreamCopierAsync() throws Exception {
        final int limit = 100;

        SlowOutput slow = new SlowOutput(limit,50);
        Thread slowWriter = new Thread(slow);

        ByteArrayOutputStream copy = new ByteArrayOutputStream();
        StreamCopier copier = new StreamCopier(slow.getSlowInput(), copy);
        Thread t = new Thread(copier);

        log.info("Starting copier...");
        t.start();

        boolean complete = copier.waitForComplete(100);
        TestCase.assertFalse(complete);

        log.info("Starting writer...");
        slowWriter.start();

        log.info("Waiting for copier...");
        copier.waitForComplete(-1);

        log.info("Copying complete.");

        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        SlowOutput.writeStuff(limit, new PrintStream(expected), 0);
        byte[] expectedBytes = expected.toByteArray();
        byte[] copyBytes = copy.toByteArray();

        TestCase.assertTrue(Arrays.equals(expectedBytes, copyBytes));
    }

}
