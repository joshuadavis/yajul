package org.yajul.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Works like a regular SpyProxy, but has a control portlistening for commands.
 *
 * @see org.yajul.net.SpyProxy
 *
 * @author agautam
 */
public class ControlledProxy extends SpyProxy
{
    // remote commands
    private static final String REMOTE_CMD_RESUME = "Resume";
    private static final String REMOTE_CMD_PAUSE = "Pause";
    private static final String REMOTE_CMD_EXIT = "Exit";
    private static final String REMOTE_CMD_QUIT = "Quit";

    /** try no more than 100 ports */
    private static final int MAX_PORTS_TO_TRY = 1000;
    private ServerSocket serverSocket = null;
    private boolean shuttingDown = false;

    private static String remoteUsage()
    {
        StringBuffer buff = new StringBuffer();
        buff.append("Proxy command interface").append("\n");
        buff.append("Possible commands (case does not matter): ").append("\n");
        buff.append("\"Pause\": Pauses the proxy streams").append("\n");
        buff.append("\"Resume\": Resumes the paused streams").append("\n");
        buff.append("\"Exit\": closes connection").append("\n");
        return null;
    }

    public ControlledProxy(String serverHost, int serverPort, int proxyPort) throws UnknownHostException, IOException
    {
        super(serverHost, serverPort, proxyPort);
    }

    private void startControllerSocket()
    {
        int port = getArgLocalPortNumber() + 1;
        for(int i = 0; i < MAX_PORTS_TO_TRY; i++)
        {
            try
            {
                serverSocket = new ServerSocket(port);
                log("Started command server at port=" + port);
                break;
            }
            catch (IOException e)
            {
                log(e.getMessage());
                port++;
                serverSocket = null;
            }
        }
        if (serverSocket == null)
        {
            final String msg = "Unable to file a free port to open a controler";
            log(msg);
            throw new RuntimeException(msg);
        }
    }

    public void shutdown()
    {
        try
        {
            shuttingDown  = true;
            serverSocket.close();
        }
        catch (Throwable t)
        {
            logErr("Error closing command server: " + t.getMessage());
            t.printStackTrace(getErrStream());
        }

        super.shutdown();
    }

    public void run()
    {
        // start ourselves in a seprate thread.
        new Thread(new Runnable()
        {
            public void run()
            {
                while (!shuttingDown)
                {
                    try
                    {
                        Socket client = serverSocket.accept();
                        try
                        {
                            InetAddress ip = client.getInetAddress();
                            log("AcceptedConnection from " + ip.getCanonicalHostName() + " / " + ip.getHostAddress());
                            InputStream is = client.getInputStream();

                            String cmd;
                            while ((cmd = getCommandFromStream(is)) != null)
                            {
                                if (shuttingDown)
                                    break;

                                log("Command Recd from client \"" + cmd + "\"");
                                String response = processCommand(cmd);
                                if (response == null)
                                {
                                    break;
                                }
                                writeResponseToclient(response, client.getOutputStream());
                            }
                        }
                        finally
                        {
                            log("Closing client connection");
                            client.close();
                        }
                    }
                    catch (Exception e)
                    {
                        logErr("Error: " + e.getMessage());
                        e.printStackTrace(getErrStream());
                    }
                }
            }
        }).run();

        // run the parent
        super.run();
    }

    protected void writeResponseToclient(String response, OutputStream outputStream) throws IOException
    {
        outputStream.write((response + "\n").getBytes());
    }

    protected String processCommand(String cmd)
    {
        if (cmd.equalsIgnoreCase(REMOTE_CMD_PAUSE))
        {
//            pauseStreams();
            return "Completed\n";
        }

        if (cmd.equalsIgnoreCase(REMOTE_CMD_RESUME))
        {
//            resumeStreams();
            return "Completed\n";
        }

        if (cmd.equalsIgnoreCase(REMOTE_CMD_EXIT) || cmd.equalsIgnoreCase(REMOTE_CMD_QUIT))
        {
            return null;
        }

        return remoteUsage();
    }

    /**
     * Reads a string till '\n' or '\r' and returns that string.
     * if (EOF) -1 encountered, will return null.
     */
    protected String getCommandFromStream(InputStream is) throws IOException
    {
        StringBuffer buff = new StringBuffer();
        int r;
        r = is.read();
        while (r != -1)
        {
            if (shuttingDown)
                break;

            if (r == '\n' || r == '\r')
            {
                return buff.toString();
            }
            char c = (char) r;
            buff.append(c);

            r = is.read();
        }
        return null;
    }

    public static void main(String[] args)
    {
        try
        {
            parseApplicationArguments(args, ControlledProxy.class.getName());

            ControlledProxy proxy = new ControlledProxy(getArgServerHost(), getArgServerPortNumber(), getArgLocalPortNumber());
            proxy.setDebugBinary(isArgDebugBinary());
            proxy.setDebugText(isArgDebugText());
            proxy.startControllerSocket();
            Thread thread = new Thread(proxy);
            thread.start();
        }
        catch (Exception ex)
        {
            System.err.println("Unexpected exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static PrintStream getErrStream()
    {
        return System.err;
    }

    protected static void usage()
    {
        SpyProxy.usage(ControlledProxy.class.getName());
        System.out.println("This application will open a command port at the very next port available after localPort");
    }

    public static void logErr(String msg)
    {
        System.err.println(msg);
        System.err.flush();
    }

    public static void log(String msg)
    {
        System.out.println(msg);
        System.out.flush();
    }
}
