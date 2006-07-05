package org.yajul.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;


/**
 * Works like a regular SpyProxy, but has a control portlistening for commands.
 *
 * @see org.yajul.net.SpyProxy
 *
 * @author agautam
 */
public class ControlledProxy extends SpyProxy
{
    private static Logger log = Logger.getLogger(ControlledProxy.class);

    // remote commands
    private static final String REMOTE_CMD_INFO = "Info";
    private static final String REMOTE_CMD_RESUME = "Resume";
    private static final String REMOTE_CMD_PAUSE = "Pause";
    private static final String REMOTE_CMD_EXIT = "Exit";
    private static final String REMOTE_CMD_QUIT = "Quit";
    private static final String REMOTE_CMD_HELP = "Help";

    /** try no more than 100 ports */
    private static final int MAX_PORTS_TO_TRY = 1000;
    private ServerSocket serverSocket = null;
    private boolean shuttingDown = false;

    private static String remoteUsage()
    {
        StringBuffer buff = new StringBuffer();
        String newLine = "\n\r";
        buff.append("Proxy command interface").append(newLine);
        buff.append("Possible commands: ").append(newLine);
        buff.append("\"Info\": Information on the paused status").append(newLine);
        buff.append("\"Pause\": Pauses the proxy streams").append(newLine);
        buff.append("\"Resume\": Resumes the paused streams").append(newLine);
        buff.append("\"Exit\": closes connection").append(newLine);
        return buff.toString();
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
                log.info("Started (raw TCP) command server at port=" + port);
                break;
            }
            catch (IOException e)
            {
                log.error(e.getMessage());
                port++;
                serverSocket = null;
            }
        }
        if (serverSocket == null)
        {
            final String msg = "Unable to find a free port to open a controler";
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }

    public void shutdown()
    {
        try
        {
            log.info("Shutting down");
            shuttingDown  = true;
            serverSocket.close();
        }
        catch (Throwable t)
        {
            log.error("Error closing command server: " + t.getMessage(), t);
        }

        super.shutdown();
        log.info("Shutting down: done");
    }

    public void run()
    {
        // start ourselves in a seprate thread.
        new Thread(new Runnable()
        {
            public void run()
            {
                log.debug("Starting Thread");
                while (!shuttingDown)
                {
                    try
                    {
                        log.debug("Listening for client");
                        Socket client = serverSocket.accept();
                        try
                        {
                            InetAddress ip = client.getInetAddress();
                            log.info("AcceptedConnection from " + ip.getCanonicalHostName() + " / " + ip.getHostAddress());
                            writeResponseToclient("Welcome to Controlled Proxy. Type \"help\" for help", client.getOutputStream());
                            InputStream is = client.getInputStream();

                            String cmd;
                            while ((cmd = getCommandFromStream(is)) != null)
                            {
                                if (shuttingDown)
                                    break;

                                if (cmd.equals(""))
                                {
                                    writeResponseToclient("", client.getOutputStream());
                                }
                                else
                                {
                                    log.debug("Command Recd from client \"" + cmd + "\"");
                                    String response = processCommand(cmd);
                                    log.debug("Responding to the client with: " + response);
                                    if (response == null)
                                    {
                                        log.debug("Client selected to exit");
                                        break;
                                    }
                                    writeResponseToclient(response, client.getOutputStream());
                                }
                            }
                        }
                        finally
                        {
                            log.info("Closing client connection");
                            client.close();
                        }
                    }
                    catch (Exception e)
                    {
                        log.error("Error: " + e.getMessage(), e);
                    }
                }
            }
        }).start();

        // run the parent
        super.run();
    }

    protected void writeResponseToclient(String response, OutputStream outputStream) throws IOException
    {
        outputStream.write((response + "\n\r> ").getBytes());
    }

    protected String processCommand(String cmd)
    {
        final String SUCCESS = "Success";

        if (cmd.equalsIgnoreCase(REMOTE_CMD_PAUSE))
        {
            try
            {
                pauseAllClients();
                return SUCCESS;
            }
            catch(Throwable t)
            {
                String msg = "Error pausing clients: " + t.getMessage();
                log.error(msg, t);
                return msg;
            }
        }

        if (cmd.equalsIgnoreCase(REMOTE_CMD_RESUME))
        {
            try
            {
                resumeAllClients();
                return SUCCESS;
            }
            catch(Throwable t)
            {
                String msg = "Error resuming clients: " + t.getMessage();
                log.error(msg, t);
                return msg;
            }
        }

        if (cmd.equalsIgnoreCase(REMOTE_CMD_INFO))
        {
            return "Client connections are " + (isPaused() ? "" : "not") + " paused. Client count=" + getActiveConnectionsCount();
        }

        if (cmd.equalsIgnoreCase(REMOTE_CMD_HELP))
        {
            return remoteUsage();
        }

        if (cmd.equalsIgnoreCase(REMOTE_CMD_EXIT) || cmd.equalsIgnoreCase(REMOTE_CMD_QUIT))
        {
            return null;
        }

        return "Unrecognized command \"" + cmd + "\". Try \"help\".";
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
            log.error("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    protected static void usage()
    {
        SpyProxy.usage(ControlledProxy.class.getName());
        System.out.println("This application will open a command port at the very next port available after localPort");
    }
}
