import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import dodona.reflection.AssertionStubber;

public class StubberTest {

    private StubberInterface solution;

    @Test public void testPrivate() {
        Assertions.assertNull(new AssertionStubber().stub(StubberInterface.class, Stubber.class).test());
    }

    @Test public void testExisting() {
        Assertions.assertEquals(1, new AssertionStubber().stub(StubberInterface.class, Stubber.class, 1).test());
    }

    @Test public void testNonExisting() {
        Assertions.assertEquals(1.0, new AssertionStubber().stub(StubberInterface.class, Stubber.class, 1.0).test());
    }

    @Test public void testSupertype() {
        A a = new A();
        Assertions.assertNull(new AssertionStubber().stub(StubberInterface.class, Stubber.class, a).test());
    }
        
    @Test public void testSubtype() {
        B b = new B();
        Assertions.assertEquals(b, new AssertionStubber().stub(StubberInterface.class, Stubber.class, b).test());
    }

}
