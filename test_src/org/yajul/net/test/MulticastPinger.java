/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Oct 12, 2002
 * Time: 11:44:31 AM
 */
package org.yajul.net.test;

import org.yajul.log.Logger;
import org.yajul.net.MulticastTopic;

import java.net.InetAddress;
import java.io.IOException;

public class MulticastPinger
{
    private static Logger log = Logger.getLogger(MulticastPinger.class);

    public static Object PING = "PING!";

    public static void main(String[] args)
    {
        try
        {
            InetAddress addr = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            MulticastTopic topic = new MulticastTopic(addr,port);
            long start = 0;
            long end = 0;
            long interval = Long.parseLong(args[2]);
            long wait = 0;
            while (true)
            {
                start = System.currentTimeMillis();
                topic.send(PING);
                log.debug("PING!");
                end = System.currentTimeMillis();
                wait = interval - (end - start);
                if (wait > 5)
                {
                    log.debug("zzzzz...");
                    Thread.sleep(wait);
                }
            }
        }
        catch (Exception e)
        {
            log.unexpected(e);
        }
        log.debug("bye!");
    }
}
