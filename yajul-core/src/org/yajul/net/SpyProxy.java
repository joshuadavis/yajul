package org.yajul.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Provides a simple proxy for TCP/IP connections.
 */
public class SpyProxy extends AbstractServerSocketListener
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
        System.out.println("      serverHost is the host name or IP address of the target server");
        System.out.println("      serverPort is the port on the target server");
        System.out.println("      localPort is the service port for the proxy");
    }

    private boolean debugBinary;
    private boolean debugText;
    private boolean showConnections;

    /** The host to forward all requests to. **/
    private InetAddress serverAddress;
    /** The port to forward all requests to. **/
    private int serverPort;
    private DateFormat dateFormat;

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
        super(proxyPort);
        serverAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
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

    public int getProxyPort()
    {
        return getPort();
    }

    protected AbstractClientConnection acceptClient(Socket in) throws IOException
    {
        SpyClientConnection con = null;
        try
        {
            con = new SpyClientConnection(in);
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
                    unexpected(ioex);
                }
                unexpected(e);
            }
            else
            {
                unexpected(e);
            }

        }
        return con;
    }

    protected void unexpected(Throwable e)
    {
        println("Unexpected exception: " + e.getMessage());
        e.printStackTrace();
    }

    protected void serverClosed()
    {
        println("Shutting down...");
    }

    private void println(String message)
    {
        synchronized (this)
        {
            System.out.println(dateFormat.format(new Date()) + "~" + message);
            System.out.flush();
        }
    }

    private void print(String message)
    {
        synchronized (this)
        {
            System.out.print(message);
            System.out.flush();
        }
    }

    /**
     * Handles a connection from a client.
     */
    private class SpyClientConnection extends AbstractClientConnection
    {
        private Socket server;
        private Channel incoming;
        private Channel outgoing;
        private boolean incomingStopped = false;
        private boolean outgoingStopped = false;
        private Channel currentChannel = null;

        /**
         * Creates a SpyClientConnection.
         * @param in the client socket
         * @throws IOException if failed.
         */

        private SpyClientConnection(Socket in)
                throws IOException
        {
            super(SpyProxy.this,in);
            server = new Socket(serverAddress, serverPort);
            if (isDebug() || showConnections)
            {
                println("Client "
                        + in.getInetAddress().getHostName()
                        + ":" + in.getPort() + " accepted, "
                        + " proxy socket to "
                        + server.getInetAddress().getHostName()
                        + ":" + server.getPort() + " opened");
            }
            Socket socket = getSocket();
            incoming = new Channel(socket,
                    getInputStream(),
                    server,
                    server.getOutputStream(),
                    this);
            outgoing = new Channel(
                    server, server.getInputStream(),
                    socket, socket.getOutputStream(), this);
        }

        public void initialize(AbstractServerSocketListener listener)
        {
            super.initialize(listener);
            start();
        }

        private void start()
        {
            Thread thread = new Thread(incoming);
            thread.start();
            Thread thread2 = new Thread(outgoing);
            thread2.start();
        }

        public void shutdown()
        {
            if (incoming.isAlive() || outgoing.isAlive())
            {
                incoming.shutdown();
                outgoing.shutdown();
                super.close();
                try
                {
                    server.close();
                }
                catch (Exception ex)
                {
                    unexpected(ex);
                }
            }
            server = null;
        }

        public void channelClosed(Channel channel)
        {
            if (channel == incoming)
            {
                if (isShowConnections() || isDebug())
                    println("Incoming stream " + channel.getName() + " closed, " + channel.getBytes() + " bytes.");
                incomingStopped = true;
            }
            else if (channel == outgoing)
            {
                if (isShowConnections() || isDebug())
                    println("Outgoing stream " + channel.getName() + " closed, " + channel.getBytes() + " bytes.");
                outgoingStopped = true;
            }
            else
                println("Unknown channel "
                        + channel.toString());

            if (incomingStopped && outgoingStopped)
            {
                close();
            }
        }

        public void setCurrentChannel(Channel channel)
        {
            synchronized (this)
            {
                boolean channelChanged = (currentChannel != channel);
                currentChannel = channel;
                if (channelChanged && (isShowConnections() || isDebug()))
                {
                    if (currentChannel == incoming)
                    {
                        if (isDebug()) print("\n");
                        println(" CLIENT " + incoming.getName() + "  => SERVER " + outgoing.getName());
                    }
                    else
                    {
                        if (isDebug()) print("\n");
                        println(" SERVER " + outgoing.getName() + "  => CLIENT " + incoming.getName());
                    }
                }
            } // synchronized
        }
    } // class SpyClientConnection

    private class Channel implements Runnable
    {
        private static final int BUFSZ = 4 * 1024;

        private Thread thread;
        private boolean running = true;
        private Socket in;
        private Socket out;
        private InputStream reader;
        private OutputStream writer;
        private InetAddress inAddress;
        private int inPort;
        private SpyClientConnection con;
        private long bytes;

        /**
         * Constructor
         * @param in the transmitting socket
         * @param out the receiving socket
         */
        private Channel(Socket in,InputStream inputStream, Socket out, OutputStream outputStream, SpyClientConnection con)
        {
            super();
            this.in = in;
            this.out = out;
            this.con = con;
            reader = inputStream;
            writer = outputStream;
            inAddress = this.in.getInetAddress();
            inPort = this.in.getPort();
            bytes = 0;
        }

        private void shutdown()
        {
            running = false;
        }

        public long getBytes()
        {
            return bytes;
        }

        public InetAddress getAddress()
        {
            return inAddress;
        }

        public int getPort()
        {
            return inPort;
        }

        public String getName()
        {
            return inAddress.getHostName() + ":" + inPort;
        }

        public void run()
        {
            byte[] cbuf = new byte[BUFSZ];
            // Get the thread we've ben started on.
            thread = Thread.currentThread();
            int readLength = 0;

            try
            {
                while (running &&
                        (readLength = reader.read(cbuf, 0, BUFSZ)) != -1)
                {
                    bytes += readLength;
                    con.setCurrentChannel(this);
                    if (debugBinary)
                        printBinaryBuf(cbuf, readLength);
                    if (debugText)
                        printTextBuf(cbuf, readLength);
                    writer.write(cbuf, 0, readLength);
                    writer.flush();
                }
            }
            catch (IOException e)
            {
                // Just assume the reading has stopped.
                running = false;
            }

            // we are here because the socket was in closed
            // close the corresponding recieve socket
            try
            {
                out.close();
            }
            catch (IOException ioex)
            {
                println("Channel.run() could not close socket");
            }
            con.channelClosed(this);
        }

        /**
         * method to print what's in the buffer?
         * @param cbuf array of type byte
         * @param validLength int showing what the valid length is
         */
        private void printBinaryBuf(byte[] cbuf, int validLength)
        {
            StringBuffer msg = new StringBuffer();
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
            String s = null;
            try
            {
                s = new String(cbuf, 0, validLength, "UTF-8");
            }
            catch (UnsupportedEncodingException x)
            {
                unexpected(x);
            }
            print(s);
        }

        public boolean isAlive()
        {
            return (thread != null) ? thread.isAlive() : false;
        }

    } // class Channel
}










