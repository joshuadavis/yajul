package org.yajul.collections;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * Weak reference map that can be used to implement the Flyweight pattern while still releasing memory
 * back to the system when needed.
 * <p/>
 * See http://en.wikibooks.org/wiki/Computer_Science_Design_Patterns/Flyweight
 * <p/>
 * The object being pooled must:
 * <ol>
 * <li>Implement equals() and hashCode()</li>
 * <li>Be immutable.  The same object may be shared by many threads.</li>
 * </ol>
 * <br>
 * User: josh
 * Date: 2/29/12
 * Time: 9:19 PM
 */
public class FlyweightPool<T>
{
    private final WeakHashMap<T, WeakReference<T>> map = new WeakHashMap<T, WeakReference<T>>();

    /**
     * Like String.intern(), returns the shareable reference to 'object'.
     *
     * @param object the object
     * @return the shared reference to the object
     */
    public T get(T object)
    {
        synchronized (map)
        {
            WeakReference<T> ref = map.get(object);
            if (ref != null)
            {
                T o = ref.get();
                if (o != null)
                {
                    return o;
                }
            }

            // Either the map didn't have the object in it, or the reference was cleaned up.
            // Add it to the map and return it.
            map.put(object, new WeakReference<T>(object));
            return object;
        }
    }

    /**
     * Strings have their own flyweight pool.
     * @param string a string (or null)
     * @return string.intern(), or null
     */
    public static String intern(String string)
    {
        return string == null ? null : string.intern();
    }
}