package org.yajul.net.http.client;

import org.apache.log4j.Logger;
import org.yajul.io.*;
import org.yajul.net.http.*;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * A substitute for java.net.URLConnection that gives more control over the sockets and HTTP headers.
 */
public class HTTPConnection extends HTTPSocketWrapper
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(HTTPConnection.class);

    /** The default socket timeout. */
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000; // Ten second socket timeout.


    private HTTPClient client;   // The HTTPClient that is making the connection
    private ByteCountingInputStream in;       // Used to count the number of bytes read.
    private ByteCountingOutputStream out;      // Used to count the number of bytes written.
    private ByteArrayOutputStream outTrace; // Output tracing output.
    private ByteArrayOutputStream inTrace;  // Input tracing output.

    /** The state of the connection is unknown. */
    public static final int UNKNOWN = 0;
    /** The connection has been initialized. */
    public static final int INITIALIZED = 1;
    /** The connection is open. */
    public static final int OPEN = 2;
    /** The connection is ready to send the request headers. */
    public static final int REQUEST_HEADERS = 3;
    /** The connection is ready to send the request body. */
    public static final int REQUEST_BODY = 4;
    /** The connection is ready to read the response headers. */
    public static final int RESPONSE_HEADERS = 5;
    /** The connection is ready to read the body. */
    public static final int RESPONSE_BODY = 6;
    /** The connection has been closed. */
    public static final int CLOSED = 99;

    // The internal state of the connection.
    private int state;
    private static final int RESPONSE_BUFFER_SIZE = 4096;

    /**
     * Initializes the state.
     */
    protected void initialize(HTTPClient client, String protocol, String host, int port)
    {
        this.client = client;
        if (this.client == null)
            throw new RuntimeException("An instance of HTTPClient is required!");
        super.initialize(protocol, host, port);
        state = INITIALIZED;
        setProxyInfo(this.client.getProxyInfo());
        log.info("HTTPConnection initialized.  " + getProtocol() + "://" + getHost() + ":" + getPort());
    }

    /**
     * Creates an HTTP Connection, given the client, protocol, host and port.
     * @param client A reference to the HTTPClient object.
     * @param protocol Protocol string (i.e. 'http' or 'https')
     * @param host The host name or IP address.
     * @param port The port number.
     * @exception java.net.SocketException Thrown if the socket parameters cannot be set.
     */
    public HTTPConnection(HTTPClient client, String protocol, String host, int port)
            throws java.net.SocketException
    {
        initialize(client, protocol, host, port);
    }

    /**
     * Opens the socket to the HTTP server.
     * @param protocol Protocol to use.
     * @param host Host name or IP address.
     * @param port Port number.
     * @exception java.net.SocketException Thrown if the socket parameters cannot be set.
     * @exception java.io.IOException Thrown if there was a problem creating the socket.
     */
    public void open(String protocol, String host, int port)
            throws java.net.SocketException, java.io.IOException
    {
        initialize(client, protocol, host, port);
        super.open();
        // Get the socket timeout and proxy info from the client.
        getSocket().setSoTimeout(client.getSocketTimeout());
        state = OPEN;
    }

    /** Returns the input stream. */
    public synchronized InputStream getInputStream()
    {
        return in;
    }

    /** Returns the output stream. */
    public synchronized OutputStream getOutputStream()
    {
        return out;
    }

    /**
     * Wraps the superclass method for adding buffers to the input and output streams.
     */
    public void addStreamBuffers()
    {
        InputStream in = super.getInputStream();
        OutputStream out = super.getOutputStream();
        if (log.isDebugEnabled())
        {
            log.debug("Adding stream debug traces...");
            outTrace = new ByteArrayOutputStream();
            out = new TeeOutputStream(out, outTrace);
            inTrace = new ByteArrayOutputStream();
            in = new EchoInputStream(in, inTrace);
        }
        this.in = new ByteCountingInputStream(in);
        this.out = new ByteCountingOutputStream(out);
    }

    /**
     * Sends the request to the server.  Returns the
     * server's response in Message.
     * @param req The HTTP request.
     * @exception java.io.IOException
     * @exception java.lang.Exception
     * @return The HTTP response with the input stream positioned at the beginning of the message body.
     */
    public synchronized Message send(RequestHeader req,InputStream content) throws IOException, Exception
    {
        // Open the socket.
        long start = System.currentTimeMillis();
        open();
        long openTime = System.currentTimeMillis() - start;

        try
        {
            // Reset the trace streams.
            resetTraceStreams();

            // Write the request the socket output stream.
            OutputStream os = getOutputStream();
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
            state = REQUEST_HEADERS;  // Ready to send headers.
            req.writeRequestLine(out);
            req.writeStandardHeaders(out);

            // If this connection is using a proxy AND
            // there isn't already a proxy authorization header... add the proxy authorization header.
            String proxyAuth = getBasicProxyAuthorization();
            if (proxyAuth != null && req.containsHeader(HeaderConstants.PROXY_AUTH))
            {
                HTTPHeader h = new HTTPHeader(HeaderConstants.PROXY_AUTH, proxyAuth);
                h.write(out);
            }

            // Write the rest of the headers.
            req.finishHeaders(out);
            out.flush();

            state = REQUEST_BODY;     // Ready to send the body.

            // If the message has a body, write that too.
            if (content != null)
                StreamCopier.unsyncCopy(content,os,StreamCopier.DEFAULT_BUFFER_SIZE);

            // Flush the request stream... cause the server to respond.
            os.flush();

            // Log the detailed info.
            logDetails("Resquest", req, outTrace);

            state = RESPONSE_HEADERS; // Ready to receive the response headers.

            log.info(getSocketHost() + ":" + getSocketPort() + " < " + req.getMethod() + " " + req.getRequestURI() + " " + this.out.getByteCount() + " bytes written.");

            // Wait for the response...
            start = System.currentTimeMillis();
            HTTPInputStream r = new HTTPInputStream(this.getInputStream(),RESPONSE_BUFFER_SIZE);  // Create the reader.

            // Wait for the response.
            if (log.isDebugEnabled())
                log.debug("Waiting for response from " + getSocketHost() + ":" + getSocketPort() + " ...");
            r.waitUntilReady();
            long responseTime = System.currentTimeMillis() - start;
            if (log.isDebugEnabled())
                log.debug("Reading response from " + getSocketHost() + ":" + getSocketPort() + " (" + responseTime + "ms)");

            // Read and parse the headers in the response.
            ResponseHeader h = new ResponseHeader();
            h.read(r);
            Message res = new Message(r,h);
//            res.setResponseTime(responseTime);
//            res.setOpenTime(openTime);
            state = RESPONSE_BODY;

            // If the request and the response headers indicated 'keep-alive'
            // connection mode, then set the 'keepalive' flag so the socket can
            // be re-used later.
            if (log.isDebugEnabled())
            {
                log.debug("Open time      = " + responseTime);
                log.debug("Response time  = " + openTime);
            }

            // Create a one-liner log message.
            MessageHeader header = res.getHeader();

            log.info(getSocketHost() + ":" + getSocketPort() + " > " + header.getStartLine() + " (" + responseTime + "ms) Content-length: " + header.getContentLength());

            // Log the detailed info.
            logDetails("Response", header, inTrace);

            // Return the response.
            return res;

        }
        catch (Exception e)
        {
            log.error(e, e);
            throw e;
        }
    }

    private void logDetails(String type, GenericMessageHeader header, ByteArrayOutputStream baos)
    {
        if (log.isDebugEnabled())
            log.debug("" + type + ":\n--- " + type + " Headers ---\n"
                    + header.getStartLine()
                    + "\n" + header.getHeadersAsString()
                    + "--- End of " + type + " Headers ---");

        if (baos != null)
        {
            byte[] buf = baos.toByteArray();
            if (log.isDebugEnabled())
                log.debug("\n--- " + type + " Stream ---\n"
                        + HexDumpOutputStream.toHexString(buf,buf.length)
                        + "\n--- End of " + type + " Stream ---");
            baos.reset();
        }
    }

    private void resetTraceStreams()
    {
        if (outTrace != null)
            outTrace.reset();
        if (inTrace != null)
            inTrace.reset();
    }

    /**
     * Closes the connection to the server.
     */
    public void close()
    {
        // If not connected, do nothing.
        if (!isConnected())
            return;

        if (log.isDebugEnabled())
            log.debug("Closing HTTPConnection...");

        // Close the output tracing stream.

        if (outTrace != null)
            try
            {
                outTrace.close();
            }
            catch (Exception ignore)
            {
            }
        outTrace = null;

        // Close the output stream.
        if (out != null)
            try
            {
                out.close();
            }
            catch (Exception ignore)
            {
            }
        out = null;

        // Close the input tracing stream.
        if (inTrace != null)
        {
            // If the HTTP logging and debugging are on, log the entire request.
            // logBytes("Response", (inTrace == null) ? null : inTrace.toByteArray());
            try
            {
                inTrace.close();
            }
            catch (Exception ignore)
            {
            }
        }
        inTrace = null;

        // Close the input stream.
        if (in != null)
            try
            {
                in.close();
            }
            catch (Exception ignore)
            {
            }
        in = null;


        super.close();  // Close the socket.

        state = CLOSED;
    }

    /**
     * Returns the number of bytes sent so far in the connection.
     * @return
     */
    public int getBytesSent()
    {
        return out.getByteCount();
    }

    /**
     * Returns the number of bytes received so far for the connection.
     * @return
     */
    public int getBytesReceived()
    {
        return in.getByteCount();
    }

    /**
     * Returns the HTTPClient that is using the connecion.
     * @return
     */
    public HTTPClient getClient()
    {
        return client;
    }
}
