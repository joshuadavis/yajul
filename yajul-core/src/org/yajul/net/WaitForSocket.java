package org.yajul.net;

import java.io.IOException;
import java.net.Socket;

/**
 * Command line program that tries to connect to the given TCP socket for the specified number of milliseconds.
 * <br>User: Joshua Davis
 * Date: Jan 13, 2007
 * Time: 6:13:31 PM
 */
public class WaitForSocket
{
    private static final int WAIT = 1000;

    public static void main(String[] args)
    {
        // args[0] - Host name or IP address
        // args[1] - Port number
        // args[2] - Number of milliseconds to wait before giving up.
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        long timeout = Long.parseLong(args[2]);
        long start = System.currentTimeMillis();
        long remaining = timeout;
        boolean connected = false;
        while (remaining > 0 && !connected)
        {
            Socket s = null;
            try
            {
                System.out.println("Attempting to connect...");
                s = new Socket(host, port);
                connected = true;
            }
            catch (IOException e)
            {
                // ignore
            }
            finally
            {
                if (s != null)
                {
                    try
                    {
                        s.close();
                    }
                    catch (IOException ignore)
                    {
                        // ignore
                    }
                }
            }
            long elapsed = System.currentTimeMillis() - start;
            if (connected)
            {
                System.out.println("Connected after waiting " + elapsed + " milliseconds.");
                System.exit(0);
            }

            remaining = timeout - elapsed;
            // Don't wait if we're already out of time.
            if (remaining <= 0)
                break;
            // Don't wait more than the remaining time.
            long wait = remaining > 0 && remaining < WAIT ? remaining : WAIT;
            try
            {
                System.out.println("Failed to connect.  Retrying in " + wait + " ms.");
                Thread.sleep(wait);
            }
            catch (InterruptedException ignore)
            {
                // ignore
            }
            // Recalculate the remaining time after we've waited. 
            remaining = timeout - (System.currentTimeMillis() - start);
        } // while
        System.exit(1); // Non zero return code means it failed.
    }
}
