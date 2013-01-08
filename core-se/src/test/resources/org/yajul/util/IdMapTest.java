package org.yajul.util;

import org.junit.Test;
import org.yajul.collections.CollectionUtil;
import org.yajul.collections.EntityWithId;
import org.yajul.collections.IdMap;
import org.yajul.io.StreamCopier;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests 'IdMap'.
 * <br>
 * User: josh
 * Date: Aug 29, 2008
 * Time: 9:38:38 AM
 */
public class IdMapTest
{
    @Test
    public void testIdMap() throws IOException, ClassNotFoundException
    {
        IdMap<Long,Thingie> idmap = new IdMap<Long,Thingie>();

        ensureEmpty(idmap);

        byte[] bytes = StreamCopier.serializeObject(idmap);
        //noinspection unchecked
        IdMap<Long,Thingie> idmap2 = (IdMap<Long,Thingie>) StreamCopier.unserializeObject(bytes);

        ensureEmpty(idmap2);

        Thingie[] thingies = new Thingie[100];
        for(int i = 0; i < thingies.length; i++)
        {
            thingies[i]=new Thingie(i,String.format("thing%04d",i));
            idmap.put(thingies[i]);
        }


        checkMap(idmap, thingies);

        bytes = StreamCopier.serializeObject(idmap);
        System.out.println("bytes.length=" + bytes.length);
        //noinspection unchecked
        idmap2 = (IdMap<Long,Thingie>) StreamCopier.unserializeObject(bytes);
        checkMap(idmap2,thingies);

        Thingie[] one = new Thingie[1];
        one[0] = thingies[0];

        IdMap<Long,Thingie> subsetOfOne = new IdMap<Long,Thingie>(idmap, Collections.singleton(one[0].getId()));
        assertEquals(subsetOfOne.size(), 1);
        assertEquals(subsetOfOne.getOne(), one[0]);

        IdMap<Long,Thingie> constructorCopy = new IdMap<Long, Thingie>(idmap);
        checkMap(constructorCopy,thingies);

        IdMap<Long,Thingie> constructorCopy2 = new IdMap<Long, Thingie>(idmap.values());
        checkMap(constructorCopy2,thingies);

        IdMap<Long,Thingie> aggregate = new IdMap<Long,Thingie>();
        List<Thingie> list = new ArrayList<Thingie>();
        for(int i = 0; i < thingies.length; i++)
        {
            // Aggregate bunches of 10...
            if (i % 10 == 0 && !list.isEmpty())
            {
                aggregate.aggregate(list);
                list.clear();
            }
            list.add(thingies[i]);
        }
        if (!list.isEmpty())
            aggregate.aggregate(list);

        // Aggregate null.
        aggregate.aggregate(null);
        
        assertEquals(aggregate, idmap);
    }

    private void ensureEmpty(IdMap<Long,Thingie> idmap2)
    {
        assertEquals(idmap2.size(), 0);
        assertTrue(idmap2.getCollection().isEmpty());
    }

    private void checkMap(IdMap<Long,Thingie> idmap, Thingie[] thingies)
    {
        assertEquals(idmap.size(), 100);

        for(int i = 0; i < 100; i++)
        {
            long id = (long) i;
            assertTrue(idmap.containsId(id));
            assertTrue(idmap.getCollection().contains(thingies[i]));
            assertEquals(idmap.get(id), thingies[i]);
        }

        Collection<Long> ids = idmap.getIds();
        for(int i = 0; i < 100; i++)
        {
            long id = (long) i;
            assertTrue(ids.contains(id));
        }

        Set<Long> idSet = IdMap.idSet(Arrays.asList(thingies));
        Set<Long> idSet2 = CollectionUtil.newHashSet(ids);
        assertEquals(idSet,idSet2);
    }

    public static class Thingie implements EntityWithId<Long>, Serializable
    {
        private long id;
        private String name;

        public Thingie(long id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public Long getId()
        {
            return id;
        }

        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Thingie)) return false;

            Thingie thingie = (Thingie) o;

            if (id != thingie.id) return false;
            if (name != null ? !name.equals(thingie.name) : thingie.name != null) return false;

            return true;
        }

        public int hashCode()
        {
            int result;
            result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
