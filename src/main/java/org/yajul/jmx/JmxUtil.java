package org.yajul.jmx;

import org.yajul.util.ComparatorChain;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.*;

/**
 * JMX helper methods
 * <br>
 * User: josh
 * Date: Dec 23, 2009
 * Time: 10:40:28 AM
 */
public class JmxUtil {
    public static MBeanServer locateServerWithDomain(String agentId,String serverDomain) {
        final ArrayList<MBeanServer> list = MBeanServerFactory.findMBeanServer(agentId);
        for (MBeanServer mBeanServer : list) {
            if (serverDomain.equals(mBeanServer.getDefaultDomain())) {
                return mBeanServer;
            }
        }
        throw new IllegalArgumentException("No MBeanServer for agentId '" + agentId + "' serverDomain '" + serverDomain + "' found!");
    }

    public static final Comparator<ObjectName> DOMAIN_COMPARATOR = new Comparator<ObjectName>() {
        public int compare(ObjectName o1, ObjectName o2) {
            String d1 = o1.getDomain();
            String d2 = o2.getDomain();
            return d1.compareTo(d2);
        }
    };

    public static final Comparator<ObjectName> KEY_PROPERTY_COMPARATOR = new Comparator<ObjectName>() {
        public int compare(ObjectName o1, ObjectName o2) {
            String k1 = o1.getCanonicalKeyPropertyListString();
            String k2 = o2.getCanonicalKeyPropertyListString();
            return k1.compareTo(k2);
        }
    };

    public static final Comparator<ObjectName> DOMAIN_KEY_COMPARATOR =
            new ComparatorChain<ObjectName>(DOMAIN_COMPARATOR, KEY_PROPERTY_COMPARATOR);

    public static List<ObjectName> sortByDomain(Collection<ObjectName> objectNames) {
        List<ObjectName> list = new ArrayList<ObjectName>(objectNames);
        Collections.sort(list,DOMAIN_KEY_COMPARATOR);
        return list;
    }
}
