package org.yajul.util;

import org.yajul.io.StreamCopier;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.Assert;

/**
 * Tests 'IdMap'.
 * <br>
 * User: josh
 * Date: Aug 29, 2008
 * Time: 9:38:38 AM
 */
public class IdMapTest extends TestCase
{
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
        Assert.assertEquals(subsetOfOne.size(),1);
        Assert.assertEquals(subsetOfOne.getOne(),one[0]);

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

        Assert.assertEquals(aggregate,idmap);
    }

    private void ensureEmpty(IdMap<Long,Thingie> idmap2)
    {
        Assert.assertEquals(idmap2.size(),0);
        Assert.assertTrue(idmap2.getCollection().isEmpty());
    }

    private void checkMap(IdMap<Long,Thingie> idmap, Thingie[] thingies)
    {
        Assert.assertEquals(idmap.size(), 100);

        for(int i = 0; i < 100; i++)
        {
            long id = (long) i;
            Assert.assertTrue(idmap.containsId(id));
            Assert.assertTrue(idmap.getCollection().contains(thingies[i]));
            Assert.assertEquals(idmap.get(id),thingies[i]);
        }
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
