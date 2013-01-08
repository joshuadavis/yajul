package org.yajul.serialization;

import org.junit.Assert;
import org.junit.Test;
import org.yajul.io.CountingObjectOutputStream;
import org.yajul.io.NullOutputStream;
import org.yajul.io.SerializationStats;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import static org.junit.Assert.*;


/**
 * Tests object serialization utilities.
 * <br>
 * User: josh
 * Date: Aug 21, 2009
 * Time: 4:31:41 PM
 */
public class SerializationTest  {

    private final static Logger log = Logger.getLogger(SerializationTest.class.getName());

    @Test
    public void testObjectSerialization() throws Exception {
        Foo f = new Foo("one", 1);
        byte[] bytes = SerializationHelper.toByteArray(f);
        assertNotNull(bytes);
        Foo f2 = (Foo) SerializationHelper.fromByteArray(bytes);
        assertNotNull(f2);
        assertEquals(f, f2);
        assertNotSame(f, f2);
        int size = SerializationStats.sizeOf(f);
        log.log(Level.FINE,"size = " + size);
        assertEquals(size, bytes.length);
        ByteArrayWrapper<Foo> wrapper = new ByteArrayWrapper<Foo>(f);
        assertTrue(wrapper.isUnwrapped());
        ByteArrayWrapper<Foo> clone = SerializationHelper.clone(wrapper);
        byte[] bytes1 = wrapper.wrap();
        assertTrue(wrapper.isWrapped());
        assertTrue(Arrays.equals(bytes, bytes1));
        assertTrue(Arrays.equals(bytes, clone.wrap()));
        f2 = (Foo) SerializationStats.autoUnwrap(clone);
        assertNotSame(clone, f2);
        assertEquals(f, f2);
        assertNotSame(f, f2);
    }

    @Test
    public void testCountingOutputStream() throws Exception {
        Thing t = createThing();
        NullOutputStream nos = new NullOutputStream();
        CountingObjectOutputStream oos = new CountingObjectOutputStream(nos);
        oos.writeObject(t);
        oos.flush();
        assertEquals(10, oos.getCounter(Foo.class.getName()).getCount());
        assertEquals(1, oos.getCounter(Bar.class.getName()).getCount());
        assertEquals(1, oos.getCounter(Thing.class.getName()).getCount());

        SerializationStats.Stats stats = SerializationStats.getStats(t);
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

    @Test
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
        ThingEx tex2 = SerializationHelper.clone(tex);
        checkFooList(tex2.getFoos(), tex.getFoos());
    }

    private void checkFooList(List<Foo> listA, List<Foo> listB) {
        assertEquals(listB.size(), listA.size());
        for (int i = 0; i < listA.size(); i++) {
            Foo f1 = listA.get(i);
            Foo f2 = listB.get(i);
            assertEquals(f1.getNumber(), f2.getNumber());
            Assert.assertFalse(f1.getName().equals(f2.getName()));
            assertTrue(f1.getName().startsWith("fromstub-"));
        }
    }

    @Test
    public void testExternalizable() throws Exception {
        Baz b = new Baz();
        BazEx be = new BazEx();

        assertEquals(b,be);

        log.info("b = " + SerializationStats.sizeOf(b));
        log.info("be = " + SerializationStats.sizeOf(be));

        BazEx be2 = SerializationHelper.clone(be);
        assertEquals(be, be2);
    }


    @Test
    public void testCompress() throws Exception {
        Baz b = new Baz();
        byte[] bu = SerializationHelper.toByteArray(b);
        byte[] bc = SerializationHelper.toCompressedByteArray(b, 512, Deflater.BEST_SPEED, 512);
        log.info(String.format("b (comp) = %d bytes %d/%d %.2f%%",bc.length,
                bc.length,bu.length,
                ((double)bc.length / (double)bu.length) * 100.0));
        Baz clone = (Baz) SerializationHelper.fromCompressedByteArray(bc, 512);
        assertEquals(b, clone);

        long start = System.currentTimeMillis();
        int iterations = 2000;
        for (int i = 0; i < iterations; i++) {
            byte[] bytes = SerializationHelper.toCompressedByteArray(b, 512, Deflater.BEST_SPEED, 512);
            Baz another = (Baz) SerializationHelper.fromCompressedByteArray(bytes, 512);
            assertEquals(another, b);
        }
        long end = System.currentTimeMillis();
        log.info("with compression, elapsed = " + (end - start));
        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            byte[] bytes = SerializationHelper.toByteArray(b);
            Baz another = (Baz) SerializationHelper.fromByteArray(bytes);
            assertEquals(another, b);
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
        protected List<FooEx> list = Arrays.asList(new FooEx[] {
            new FooEx("one",1),
            new FooEx("two",2),
            new FooEx("three",3),
        });

        public Baz() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Baz)) return false;

            Baz baz = (Baz) o;

            if (list != null ? !list.equals(baz.list) : baz.list != null) return false;
            if (myEnum != baz.myEnum) return false;
            if (!notNullEx.equals(baz.notNullEx)) return false;
            if (!notNullInt.equals(baz.notNullInt)) return false;
            if (!notNullLong.equals(baz.notNullLong)) return false;
            if (nullEnum != baz.nullEnum) return false;
            if (nullEx != null ? !nullEx.equals(baz.nullEx) : baz.nullEx != null) return false;
            if (nullInt != null ? !nullInt.equals(baz.nullInt) : baz.nullInt != null) return false;
            if (nullLong != null ? !nullLong.equals(baz.nullLong) : baz.nullLong != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = nullLong != null ? nullLong.hashCode() : 0;
            result = 31 * result + notNullLong.hashCode();
            result = 31 * result + (nullInt != null ? nullInt.hashCode() : 0);
            result = 31 * result + notNullInt.hashCode();
            result = 31 * result + (nullEnum != null ? nullEnum.hashCode() : 0);
            result = 31 * result + (myEnum != null ? myEnum.hashCode() : 0);
            result = 31 * result + (nullEx != null ? nullEx.hashCode() : 0);
            result = 31 * result + notNullEx.hashCode();
            result = 31 * result + (list != null ? list.hashCode() : 0);
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
                    ", list=" + list +
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

            ExternalizableHelper.writeList(out,list);

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

            list = ExternalizableHelper.readArrayList(in,FooEx.class);
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

    public static class FooBar implements Serializable {
        private String one;
        private MyEnum two;
        private Date three;

        public FooBar(String one, MyEnum two, Date three) {
            this.one = one;
            this.two = two;
            this.three = three;
        }

        private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException
        {
            int bits = ois.readInt();
            if (ExternalizableHelper.isNotNullBit(bits,0))
                one = ExternalizableHelper.readNullableString(ois);
            if (ExternalizableHelper.isNotNullBit(bits,1))
                two = ExternalizableHelper.readNullableEnumByte(ois,MyEnum.values());
            if (ExternalizableHelper.isNotNullBit(bits,2))
                three = new Date(ois.readLong());
        }

        private void writeObject(ObjectOutputStream oos) throws IOException
        {
            int bits = ExternalizableHelper.getNullBits(one,two,three);
            oos.writeInt(bits);
            if (ExternalizableHelper.isNotNullBit(bits,0))
                ExternalizableHelper.writeNullableString(oos,one);
            if (ExternalizableHelper.isNotNullBit(bits,1))
                ExternalizableHelper.writeEnumByte(oos,two);
            if (ExternalizableHelper.isNotNullBit(bits,2))
                oos.writeLong(three.getTime());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FooBar)) return false;

            FooBar fooBar = (FooBar) o;

            if (one != null ? !one.equals(fooBar.one) : fooBar.one != null) return false;
            if (three != null ? !three.equals(fooBar.three) : fooBar.three != null) return false;
            if (two != fooBar.two) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = one != null ? one.hashCode() : 0;
            result = 31 * result + (two != null ? two.hashCode() : 0);
            result = 31 * result + (three != null ? three.hashCode() : 0);
            return result;
        }
    }


    public static class Three implements Serializable {
        private String one;
        private MyEnum two;
        private Date three;

        public Three(String one, MyEnum two, Date three) {
            this.one = one;
            this.two = two;
            this.three = three;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Three)) return false;

            Three other = (Three) o;

            if (one != null ? !one.equals(other.one) : other.one != null) return false;
            if (three != null ? !three.equals(other.three) : other.three != null) return false;
            if (two != other.two) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = one != null ? one.hashCode() : 0;
            result = 31 * result + (two != null ? two.hashCode() : 0);
            result = 31 * result + (three != null ? three.hashCode() : 0);
            return result;
        }
    }

    @Test
    public void testNullableBits() throws Exception {
        FooBar fooBar1 = new FooBar("one",MyEnum.VALUE1,new Date());
        FooBar fooBar2 = SerializationHelper.clone(fooBar1);
        assertNotSame(fooBar1,fooBar2);
        assertEquals(fooBar1,fooBar2);
        int size = SerializationStats.sizeOf(fooBar1);
        System.out.println("size (nothing null) = " + size);

        Three three = new Three("one",MyEnum.VALUE1,new Date());
        size = SerializationStats.sizeOf(three);
        System.out.println("size (nothing null, default serialization) = " + size);

        fooBar1 = new FooBar(null, null, null);
        fooBar2 = SerializationHelper.clone(fooBar1);
        assertNotSame(fooBar1,fooBar2);
        assertEquals(fooBar1,fooBar2);
        size = SerializationStats.sizeOf(fooBar1);
        System.out.println("size (all null) = " + size);

        three = new Three(null,null,null);
        size = SerializationStats.sizeOf(three);
        System.out.println("size (all null, default serialization) = " + size);
    }

    @Test
    public void testNullSerialization() throws Exception {
        Baz b = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(b);
        oos.close();

        byte[] bytes = baos.toByteArray();
        System.out.println("null object size = " + bytes.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Baz b2 = (Baz) ois.readObject();
    }
}
