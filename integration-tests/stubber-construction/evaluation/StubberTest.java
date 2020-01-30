import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import dodona.reflection.AssertionStubber;

public class StubberTest {

    private StubberInterface solution;

    @Test public void testPrivate() {
        Assert.assertNull(new AssertionStubber().stub(StubberInterface.class, Stubber.class).test());
    }

    @Test public void testExisting() {
        Assert.assertEquals(1, new AssertionStubber().stub(StubberInterface.class, Stubber.class, 1).test());
    }

    @Test public void testNonExisting() {
        Assert.assertEquals(1.0, new AssertionStubber().stub(StubberInterface.class, Stubber.class, 1.0).test());
    }

    @Test public void testSupertype() {
        A a = new A();
        Assert.assertNull(new AssertionStubber().stub(StubberInterface.class, Stubber.class, a).test());
    }
        
    @Test public void testSubtype() {
        B b = new B();
        Assert.assertEquals(b, new AssertionStubber().stub(StubberInterface.class, Stubber.class, b).test());
    }

}
