package org.yajul.net;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Test AbstractSocketListener
 * <br/>
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: 6/28/11
 * Time: 8:09 AM
 */
public class SocketServerTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(SocketServerTest.class);

    private class EchoConnection extends AbstractClientConnection {
        public EchoConnection(AbstractSocketListener listener, Socket socket) throws IOException {
            super(listener, socket);
        }

        @Override
        public void start() {
            getListener().getExecutor().submit(new Runnable() {
                public void run() {
                    try {
                        InputStream in = getInputStream();
                        BufferedReader inbuf = new BufferedReader(new InputStreamReader(in));
                        OutputStream out = getOutputStream();
                        PrintWriter outbuf = new PrintWriter(new OutputStreamWriter(out));
                        //noinspection InfiniteLoopStatement
                        for (; ; ) {
                            log.info("Reading...");
                            String line = inbuf.readLine();
                            if (line == null) {
                                log.info("Done.");
                                break;
                            }
                            log.info("line=" + line);
                            outbuf.println("Echo: " + line);
                            outbuf.flush();
                        }
                    } catch (IOException e) {
                        log.error("Unexpected: " + e, e);
                    }
                }
            });
        }
    }

    private class EchoServer extends AbstractSocketListener {
        public EchoServer(int port, ExecutorService executorService) throws IOException {
            super(port, executorService);
        }

        @Override
        protected AbstractClientConnection acceptClient(Socket incoming) throws IOException {
            EchoConnection echoConnection = new EchoConnection(this, incoming);
            echoConnection.start();
            return echoConnection;
        }

        @Override
        protected void unexpected(Throwable t) {
            log.error("Unexpected: " + t, t);
        }
    }

    public void testServerSocket() throws IOException {
        int port = 17777;
        ExecutorService executor = Executors.newCachedThreadPool();
        AbstractSocketListener echoServer = new EchoServer(port, executor);
        echoServer.start();

        Socket client = new Socket("localhost", port);
        InputStream in = client.getInputStream();
        BufferedReader inbuf = new BufferedReader(new InputStreamReader(in));
        OutputStream out = client.getOutputStream();
        PrintWriter outbuf = new PrintWriter(new OutputStreamWriter(out));

        outbuf.println("test 1");
        outbuf.flush();

        String line = inbuf.readLine();
        log.info("line=" + line);
        client.close();

        echoServer.shutdown();

    }
}
