package org.yajul.io;

import junit.framework.TestCase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests object serialization utilities.
 * <br>
 * User: josh
 * Date: Aug 21, 2009
 * Time: 4:31:41 PM
 */
public class SerializationTest extends TestCase {

    private final static Logger log = LoggerFactory.getLogger(SerializationTest.class);

    public void testObjectSerialization() throws Exception {
        Foo f = new Foo("one", 1);
        byte[] bytes = SerializationUtil.toByteArray(f);
        assertNotNull(bytes);
        Foo f2 = (Foo) SerializationUtil.fromByteArray(bytes);
        assertNotNull(f2);
        assertEquals(f, f2);
        assertNotSame(f, f2);
        int size = SerializationUtil.sizeOf(f);
        log.debug("size = " + size);
        assertEquals(size, bytes.length);
        ByteArrayWrapper<Foo> wrapper = new ByteArrayWrapper<Foo>(f);
        ByteArrayWrapper<Foo> clone = SerializationUtil.clone(wrapper);
        assertTrue(Arrays.equals(bytes,wrapper.wrap()));
        assertTrue(Arrays.equals(bytes,clone.wrap()));
        f2 = (Foo) SerializationUtil.autoUnwrap(clone);
        assertNotSame(clone,f2);
        assertEquals(f,f2);
        assertNotSame(f,f2);
    }

    public void testCountingOutputStream() throws Exception {
        Thing t = createThing();
        NullOutputStream nos = new NullOutputStream();
        CountingObjectOutputStream oos = new CountingObjectOutputStream(nos);
        oos.writeObject(t);
        oos.flush();
        assertEquals(10, oos.getCounter(Foo.class.getName()).getCount());
        assertEquals(1, oos.getCounter(Bar.class.getName()).getCount());
        assertEquals(1, oos.getCounter(Thing.class.getName()).getCount());

        SerializationUtil.Stats stats = SerializationUtil.getStats(t);
        assertEquals(10, stats.getCounter(Foo.class.getName()).getCount());
        assertEquals(1, stats.getCounter(Bar.class.getName()).getCount());
        assertEquals(1, stats.getCounter(Thing.class.getName()).getCount());
    }

    private Thing createThing() {
        Thing t = new Thing();
        List<Foo> list = t.getFoos();
        addFoos(list);
        t.setBar(new Bar(3.14159, t));
        return t;
    }

    private static void addFoos(List<Foo> list) {
        for (int i = 0; i < 10; i++) {
            list.add(new Foo("foo-" + i, i));
        }
    }

    public void testReplacingAndResolving() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectReplacingOutputStream oros = new ObjectReplacingOutputStream(baos, new FooStubifier());
        Thing t = createThing();
        oros.writeObject(t);
        oros.flush();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectResolvingInputStream oris = new ObjectResolvingInputStream(bais, new FooDeStubifier());
        Thing t2 = (Thing) oris.readObject();
        checkFooList(t2.getFoos(), t.getFoos());

        ThingEx tex = new ThingEx();
        addFoos(tex.getFoos());
        ThingEx tex2 = SerializationUtil.clone(tex);
        checkFooList(tex2.getFoos(), tex.getFoos());
    }

    private void checkFooList(List<Foo> listA, List<Foo> listB) {
        assertEquals(listB.size(), listA.size());
        for (int i = 0; i < listA.size(); i++) {
            Foo f1 = listA.get(i);
            Foo f2 = listB.get(i);
            assertEquals(f1.getNumber(), f2.getNumber());
            assertFalse(f1.getName().equals(f2.getName()));
            assertTrue(f1.getName().startsWith("fromstub-"));
        }
    }

    public void testExternalizable() throws Exception {
        Baz b = new Baz();
        BazEx be = new BazEx();

        log.info("b = " + SerializationUtil.sizeOf(b));
        log.info("be = " + SerializationUtil.sizeOf(be));

        BazEx be2 = SerializationUtil.clone(be);
        assertEquals(be,be2);
    }


    public void testCompress() throws Exception {
        Baz b = new Baz();
        byte[] bu = SerializationUtil.toByteArray(b);
        byte[] bc = SerializationUtil.toCompressedByteArray(b,512,Deflater.BEST_SPEED,512);
        log.info(String.format("b (comp) = %d bytes %d/%d %.2f%%",bc.length,
                bc.length,bu.length,
                ((double)bc.length / (double)bu.length) * 100.0));
        Baz clone = (Baz) SerializationUtil.fromCompressedByteArray(bc,512);
        assertEquals(b,clone);

        long start = System.currentTimeMillis();
        int iterations = 2000;
        for (int i = 0; i < iterations; i++) {
            byte[] bytes = SerializationUtil.toCompressedByteArray(b,512,Deflater.BEST_SPEED,512);
            Baz another = (Baz) SerializationUtil.fromCompressedByteArray(bytes,512);
        }
        long end = System.currentTimeMillis();
        log.info("with compression, elapsed = " + (end - start));
        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            byte[] bytes = SerializationUtil.toByteArray(b);
            Baz another = (Baz) SerializationUtil.fromByteArray(bytes);
        }
        end = System.currentTimeMillis();
        log.info("no compression, elapsed = " + (end - start));        
    }

    public static enum MyEnum {
        VALUE1,
        VALUE2,
        VALUE3
    }

    public static class Baz implements Serializable {
        protected Long nullLong;
        protected Long notNullLong = 123L;
        protected Integer nullInt;
        protected Integer notNullInt = 123;
        protected MyEnum nullEnum;
        protected MyEnum myEnum = MyEnum.VALUE1;
        protected FooEx nullEx;
        protected FooEx notNullEx = new FooEx("test",456);

        public Baz() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Baz)) return false;

            Baz baz = (Baz) o;

            if (myEnum != baz.myEnum) return false;
            if (notNullEx != null ? !notNullEx.equals(baz.notNullEx) : baz.notNullEx != null) return false;
            if (notNullInt != null ? !notNullInt.equals(baz.notNullInt) : baz.notNullInt != null) return false;
            if (notNullLong != null ? !notNullLong.equals(baz.notNullLong) : baz.notNullLong != null) return false;
            if (nullEnum != baz.nullEnum) return false;
            if (nullEx != null ? !nullEx.equals(baz.nullEx) : baz.nullEx != null) return false;
            if (nullInt != null ? !nullInt.equals(baz.nullInt) : baz.nullInt != null) return false;
            if (nullLong != null ? !nullLong.equals(baz.nullLong) : baz.nullLong != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = nullLong != null ? nullLong.hashCode() : 0;
            result = 31 * result + (notNullLong != null ? notNullLong.hashCode() : 0);
            result = 31 * result + (nullInt != null ? nullInt.hashCode() : 0);
            result = 31 * result + (notNullInt != null ? notNullInt.hashCode() : 0);
            result = 31 * result + (nullEnum != null ? nullEnum.hashCode() : 0);
            result = 31 * result + (myEnum != null ? myEnum.hashCode() : 0);
            result = 31 * result + (nullEx != null ? nullEx.hashCode() : 0);
            result = 31 * result + (notNullEx != null ? notNullEx.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Baz{" +
                    "nullLong=" + nullLong +
                    ", notNullLong=" + notNullLong +
                    ", nullInt=" + nullInt +
                    ", notNullInt=" + notNullInt +
                    ", nullEnum=" + nullEnum +
                    ", myEnum=" + myEnum +
                    ", nullEx=" + nullEx +
                    ", notNullEx=" + notNullEx +
                    '}';
        }
    }

    public static class BazEx extends Baz implements Externalizable {

        public void writeExternal(ObjectOutput out) throws IOException {
            ExternalizableHelper.writeNullableLong(out, nullLong);
            ExternalizableHelper.writeNullableLong(out, notNullLong);

            ExternalizableHelper.writeNullableInteger(out, nullInt);
            ExternalizableHelper.writeNullableInteger(out, notNullInt);

            ExternalizableHelper.writeNullableEnum(out, nullEnum);
            ExternalizableHelper.writeNullableEnum(out, myEnum);

            ExternalizableHelper.writeNullable(out,nullEx);
            ExternalizableHelper.writeNullable(out,notNullEx);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            nullLong = ExternalizableHelper.readNullableLong(in);
            notNullLong = ExternalizableHelper.readNullableLong(in);

            nullInt = ExternalizableHelper.readNullableInteger(in);
            notNullInt = ExternalizableHelper.readNullableInteger(in);

            nullEnum = ExternalizableHelper.readNullableEnum(in,MyEnum.values());
            myEnum = ExternalizableHelper.readNullableEnum(in,MyEnum.values());

            nullEx = ExternalizableHelper.readNullable(in,FooEx.class);
            notNullEx = ExternalizableHelper.readNullable(in,FooEx.class);
        }
    }

    public static class Foo implements Serializable {
        protected String name;
        protected int number;

        public Foo() {
        }

        public Foo(String name, int number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public int getNumber() {
            return number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Foo)) return false;

            Foo foo = (Foo) o;

            if (number != foo.number) return false;
            if (name != null ? !name.equals(foo.name) : foo.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + number;
            return result;
        }

        @Override
        public String toString() {
            return "Foo{" +
                    "name='" + name + '\'' +
                    ", number=" + number +
                    '}';
        }
    }

    public static class FooEx extends Foo implements Externalizable {

        public FooEx() {
        }

        public FooEx(String name, int number) {
            super(name, number);
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(name);
            out.writeInt(number);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            name = (String) in.readObject();
            number = in.readInt();
        }
    }
    
    public static class FooStub implements Serializable {
        private int num;

        public FooStub(int num) {
            this.num = num;
        }
    }

    public static class Bar implements Serializable {
        private double factor;
        private Thing parent;

        public Bar(double factor, Thing parent) {
            this.factor = factor;
            this.parent = parent;
        }

        public double getFactor() {
            return factor;
        }

        public Thing getParent() {
            return parent;
        }
    }

    public static class Thing implements Serializable {
        private List<Foo> foos = new ArrayList<Foo>();
        private Bar bar;

        public Bar getBar() {
            return bar;
        }

        public void setBar(Bar bar) {
            this.bar = bar;
        }

        public List<Foo> getFoos() {
            return foos;
        }
    }


    public static class ThingEx implements Externalizable {
        private List<Foo> foos = new ArrayList<Foo>();

        public List<Foo> getFoos() {
            return foos;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            ObjectReplacingOutputStream oros = new ObjectReplacingOutputStream(out, new FooStubifier());
            oros.writeObject(foos);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            ObjectResolvingInputStream oris = new ObjectResolvingInputStream(in, new FooDeStubifier());
            foos = (List<Foo>) oris.readObject();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ThingEx)) return false;

            ThingEx thingEx = (ThingEx) o;

            if (foos != null ? !foos.equals(thingEx.foos) : thingEx.foos != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return foos != null ? foos.hashCode() : 0;
        }
    }

    private static class FooDeStubifier implements ObjectResolver {
        public Object resolveObject(Object obj) {
            if (obj instanceof FooStub) {
                FooStub fs = (FooStub) obj;
                return new Foo("fromstub-", fs.num);
            } else
                return obj;
        }
    }

    private static class FooStubifier implements ObjectReplacer {
        public Object replaceObject(Object obj) {
            if (obj instanceof Foo) {
                Foo f = (Foo) obj;
                return new FooStub(f.getNumber());
            } else
                return obj;
        }
    }
}
