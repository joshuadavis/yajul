package org.yajul.net;

import java.net.*;
import java.io.*;
import java.util.Vector;
import java.util.Date;
import java.util.Iterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Provides a simple proxy for TCP/IP connections.
 */
public class SpyProxy implements Runnable
{
    /**
     * java SpyProxy [-d] serverHost serverPort [localPort]
     * @param args Command line arguments.
     */
    static public void main(String[] args)
    {
        try
        {
            boolean debugBinary = false;
            boolean debugText = false;
            int offset = 0;

            if (args.length == 0)
            {
                usage();
                System.exit(-1);
            }

            while (args[offset].charAt(0) == '-')
            {
                if (args[0].equals("-d"))
                {
                    debugBinary = true;
                    offset++;
                }
                else if (args[0].equals("-t"))
                {
                    debugText = true;
                    offset++;
                }
            }

            if (args.length - offset < 2)
            {
                usage();
                System.exit(-1);
            }

            String serverHost = args[offset++];
            String serverPort = args[offset++];
            String localPort = serverPort;

            if (args.length > offset)
                localPort = args[offset++];

            int lPort = 0;
            int sPort = 0;

            try
            {
                lPort = Integer.parseInt(localPort);
                sPort = Integer.parseInt(serverPort);
            }
            catch (NumberFormatException x)
            {
                usage();
                System.exit(-1);
            }

            SpyProxy proxy = new SpyProxy(serverHost, sPort, lPort);
            proxy.setDebugBinary(debugBinary);
            proxy.setDebugText(debugText);
            Thread thread = new Thread(proxy);
            thread.start();
        }
        catch (Exception ex)
        {
            System.err.println("Unexpected exception: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * prints out message for usage
     * @see #main(String[])
     */
    private static void usage()
    {
        System.out.println("Usage: java SpyProxy [-d] [-t] serverHost serverPort [localPort]");
        System.out.println("Where -d prints binary trace information to stdout");
        System.out.println("      -t prints text trace information to stdout (ideal for WebServices and XML over HTTP)");
        System.out.println("      serverHost is the DNS name or IP address of the target server");
        System.out.println("      serverPort is the port on the target server");
        System.out.println("      localPort is the service port for the proxy");
    }

    private boolean debugBinary;
    private boolean debugText;
    private boolean showConnections;

    /** The port the proxy will listen on. **/
    private int proxyPort;
    /** The server socket that the proxy will listen on. **/
    private ServerSocket listener;
    /** The host to forward all requests to. **/
    private InetAddress serverAddress;
    /** The port to forward all requests to. **/
    private int serverPort;

    private boolean running = true;
    private Vector openConnections;
    private EventHandler eventHandler;
    private DateFormat dateFormat;

    private int connectionsAccepted;

    /**
     * Creates a new SpyProxy object with the given underlying server
     * and the given proxy port.
     * @param serverHost the server host
     * @param serverPort the server port
     * @param proxyPort the proxy port
     * @exception java.net.UnknownHostException
     * @exception java.io.IOException
     */
    public SpyProxy(String serverHost, int serverPort, int proxyPort)
            throws UnknownHostException, IOException
    {
        super();
        serverAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;
        this.proxyPort = proxyPort;
        listener = new ServerSocket(proxyPort);
        eventHandler = new ConnectionEventHandler();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,S");
        openConnections = new Vector();
        connectionsAccepted = 0;
    }

    /**
     * turns on binary debugging messages
     * @param debugBinaryOn true to turn on.  off by default
     */
    public void setDebugBinary(boolean debugBinaryOn)
    {
        debugBinary = debugBinaryOn;
    }

    public boolean isShowConnections()
    {
        return showConnections;
    }

    public void setShowConnections(boolean showConnections)
    {
        this.showConnections = showConnections;
    }

    /**
     * returns the state of debugging binary messages
     * @return  true if on.  off by default
     */
    public boolean isDebugBinary()
    {
        return debugBinary;
    }

    /**
     * turns on text debugging messages
     * @param debugTextOn true to turn on.  off by default
     */
    public void setDebugText(boolean debugTextOn)
    {
        debugText = debugTextOn;
    }

    /**
     * returns the state of debugging text messages
     * @return  true if on.  off by default
     */
    public boolean isDebugText()
    {
        return debugText;
    }


    /**
     * returns the state of any debugging is on
     *@return true if debugText or debugBinary is true
     */
    public boolean isDebug()
    {
        return debugText || debugBinary;
    }


    /**
     * stops the server thread
     */
    public void shutdown()
    {
        for (Iterator iterator = openConnections.iterator(); iterator.hasNext();)
        {
            SpyClientConnection spyClientConnection = (SpyClientConnection) iterator.next();
            spyClientConnection.shutdown();
        }
        openConnections.clear();

        running = false;
        try
        {
            listener.close();
        }
        catch (IOException ioex)
        {
            println("IOException caught while closing listener");
        }
    }

    public int getConnectionsAccepted()
    {
        return connectionsAccepted;
    }

    public int getProxyPort()
    {
        return proxyPort;
    }

    public void run()
    {
        Socket in, out;

        while (running)
        {
            in = null;
            out = null;
            try
            {
                in = listener.accept();
                connectionsAccepted++;
                out = new Socket(serverAddress, serverPort);
                if (isDebug() || showConnections)
                {
                    println("Client "
                            + in.getInetAddress().getHostName()
                            + ":" + in.getPort() + " accepted, "
                            + " proxy socket to "
                            + out.getInetAddress().getHostName()
                            + ":" + out.getPort() + " opened");
                }
                SpyClientConnection con = new SpyClientConnection(in, out);
                con.setEventHandler(eventHandler);
                openConnections.addElement(con);
                con.start();
            }
            catch (ConnectException e)
            {
                if (in != null)
                {
                    try
                    {
                        in.close();
                    }
                    catch (IOException ioex)
                    {
                    }
                    println("Failed to open new socket to server");
                }
                else
                {
                    println("Unknown ConnectionException in SpyProxy.run loop");
                    e.printStackTrace();
                    running = false;
                }

            }
            catch (SocketException e)
            {
                if (listener.isClosed() && running == false)
                    println("Listener closed.");
                else
                    unexpected(e);
                running = false;

            }
            catch (IOException e)
            {
                unexpected(e);
                running = false;
            }
        }
    } // SpyProxy.run()

    private void unexpected(Throwable e)
    {
        println("Unexpected exception: " + e.getMessage());
        e.printStackTrace();
    }

    private void println(String message)
    {
        System.out.println(dateFormat.format(new Date()) + " | " + message);
    }

    private void print(String message)
    {
        System.out.print(dateFormat.format(new Date()) + " | " + message);
    }

    private class ConnectionEventHandler
            implements EventHandler
    {
        /**
         * @param terminatedObject terminatedObject
         */
        public void threadTerminated(Object terminatedObject)
        {
            boolean found = openConnections.removeElement(terminatedObject);
        }
    }

    /**
     * Handles a connection from a client.
     */
    private class SpyClientConnection
    {
        private Socket client;
        private Socket server;
        private Channel incoming;
        private Channel outgoing;
        private EventHandler eventHandler;
        private boolean incomingStopped = false;
        private boolean outgoingStopped = false;

        /**
         * Bogo Conversation Pair
         * @param clientSocket the client socket
         * @param serverSocket the server socket
         * @throws IOException if failed.
         */

        private SpyClientConnection(Socket clientSocket, Socket serverSocket)
                throws IOException
        {
            super();
            client = clientSocket;
            server = serverSocket;
            incoming = new Channel(client, server);
            outgoing = new Channel(server, client);
            EventHandler handler =
                    new ConnectionEventHandler(this);
            incoming.setEventHandler(handler);
            outgoing.setEventHandler(handler);
        }

        private void start()
        {
            Thread thread = new Thread(incoming);
            thread.start();
            Thread thread2 = new Thread(outgoing);
            thread2.start();
        }

        private void shutdown()
        {
            if (incoming.isAlive() || outgoing.isAlive())
            {
                incoming.shutdown();
                outgoing.shutdown();
                try
                {
                    client.close();
                }
                catch (Exception ex)
                {
                    println("Exception " + ex + " caught while closing client socket");
                    ex.printStackTrace();
                }

                try
                {
                    server.close();
                }
                catch (Exception ex)
                {
                    println("Exception " + ex + " caught while closing server socket");
                    ex.printStackTrace();
                }
            }

            client = null;
            server = null;
        }

        /**
         * @param l l
         */
        public void setEventHandler(EventHandler l)
        {
            eventHandler = l;
        }

        private class ConnectionEventHandler
                implements EventHandler
        {
            private SpyClientConnection con;

            /**
             * @param con object of type SpyClientConnection
             */
            private ConnectionEventHandler(SpyClientConnection con)
            {
                this.con = con;
            }

            /**
             * @param terminatedObject terminatedObject
             */
            public void threadTerminated(Object terminatedObject)
            {
                if (terminatedObject == incoming)
                {
                    if (isShowConnections())
                        println("Incoming stream closed.");
                    incomingStopped = true;
                }
                else if (terminatedObject == outgoing)
                {
                    if (isShowConnections())
                        println("Outgoing stream closed.");
                    outgoingStopped = true;
                }
                else
                    println("Unknown terminated object "
                            + terminatedObject.toString());

                if (eventHandler != null
                        && incomingStopped && outgoingStopped)
                {
                    eventHandler.threadTerminated(con);
                }
            }
        }
    }

    private class Channel implements Runnable
    {
        private static final int BUFSZ = 4 * 1024;

        private Thread thread;
        private boolean running = true;
        private Socket in;
        private Socket out;
        private BufferedInputStream reader;
        private BufferedOutputStream writer;
        private InetAddress inAddress, outAddress;
        private int inPort,    outPort;
        private EventHandler eventHandler;


        /**
         * Constructor
         * @param in the transmitting socket
         * @param out the receiving socket
         * @throws IOException if failed.
         */

        private Channel(Socket in, Socket out)
                throws IOException
        {
            super();
            this.in = in;
            this.out = out;
            reader = new BufferedInputStream(this.in.getInputStream());
            writer = new BufferedOutputStream(this.out.getOutputStream());
            inAddress = this.in.getInetAddress();
            outAddress = this.out.getInetAddress();
            inPort = this.in.getPort();
            outPort = this.out.getPort();
        }

        private void shutdown()
        {
            running = false;
        }

        public void run()
        {
            byte[] cbuf = new byte[BUFSZ];
            // Get the thread we've ben started on.
            thread = Thread.currentThread();
            try
            {
                int readLength = 0;
                while (running &&
                        (readLength = reader.read(cbuf, 0, BUFSZ)) != -1)
                {
                    if (debugBinary)
                        printBinaryBuf(cbuf, readLength);
                    if (debugText)
                        printTextBuf(cbuf, readLength);
                    writer.write(cbuf, 0, readLength);
                    writer.flush();
                }
// we are here because the socket was in closed
// close the corresponding recieve socket
                if (readLength == -1 && isDebug() && isShowConnections())
                    println("Socket " + inAddress.getHostName()
                            + ":" + inPort + " closed");
                try
                {
                    out.close();
                }
                catch (IOException ioex)
                {
                    println("Channel.run() could not close out socket");
                }
            }
            catch (IOException ioex)
            {
// This is OK.
                if (isDebug())
                    println("Socket " + inAddress.getHostName()
                            + ":" + inPort + " shutdown");
            }
//println("Channel.run() finishing") ;

            if (eventHandler != null)
                eventHandler.threadTerminated(this);

        }

        /**
         * method to print what's in the buffer?
         * @param cbuf array of type byte
         * @param validLength int showing what the valid length is
         */
        private void printBinaryBuf(byte[] cbuf, int validLength)
        {
            StringBuffer msg
                    = new StringBuffer("Got a message of length " + validLength
                    + " from " + inAddress.getHostName()
                    + ":" + inPort
                    + " to " + outAddress.getHostName()
                    + ":" + outPort + "\n");
            int offset = 0;
            while (offset < validLength)
            {
                int subOffset = 0;
                StringBuffer byteBuf = new StringBuffer();
                //                                         0123456789012345
                StringBuffer printBuf2 = new StringBuffer("                ");
                while (subOffset < 16)
                {
                    if (offset + subOffset < validLength)
                    {
                        // apparently - promotion from byte to int on NT (Win32)
                        // is diffent than on Solaris
                        // make sure the promoted value is 0xff or less
                        int curr = (int) cbuf[offset + subOffset] & 0xff;
                        byteBuf.append(hexString(curr, 2)
                                + " ");
                        if (32 <= curr && curr < 127)
                            printBuf2.setCharAt(subOffset, (char) curr);
                        else
                            printBuf2.setCharAt(subOffset, '.');

                    }
                    else
                        byteBuf.append("   ");

                    subOffset++;
                }

                msg.append("  " + hexString(offset, 6) + ": ");
                msg.append(byteBuf.toString());
                msg.append("[" + printBuf2 + "]");
                msg.append("\n");
                offset += subOffset;
            }
            print(msg.toString());
            System.out.flush();
        }

        /**
         * HexString method; takes an int and returns a HexString.
         * @param value the input value as an integer
         * @param minLength the minimum length
         * @return returns a HexString
         */
        private String hexString(int value, int minLength)
        {
            String hs = Integer.toHexString(value);
            int hsLength = hs.length();
            if (hsLength < minLength)
            {
                StringBuffer leadingZeros = new StringBuffer();
                int neededLength = minLength - hsLength;
                while (neededLength-- > 0)
                    leadingZeros.append("0");
                leadingZeros.append(hs);
                hs = leadingZeros.toString();
            }

            return hs;
        }

        /**
         * method to print what's in the buffer?
         * @param cbuf array of type byte
         * @param validLength int showing what the valid length is
         */
        private void printTextBuf(byte[] cbuf, int validLength)
        {
            StringBuffer msg
                    = new StringBuffer("Got a message of length " + validLength
                    + " from " + inAddress.getHostName()
                    + ":" + inPort
                    + " to " + outAddress.getHostName()
                    + ":" + outPort + "\n");
            try
            {
                msg.append(new String(cbuf, 0, validLength, "UTF-8"));
            }
            catch (UnsupportedEncodingException x)
            {
                msg.append("Could not print message due to UnsupportedEncodingException");
            }
            println(msg.toString());
            System.out.flush();
        }

        public void setEventHandler(EventHandler l)
        {
            eventHandler = l;
        }

        public boolean isAlive()
        {
            return (thread != null) ? thread.isAlive() : false;
        }

    } // Channel definition


    interface EventHandler
    {
        /**
         * @param terminatedObject terminatedObject
         */
        void threadTerminated(Object terminatedObject);
    }

}










