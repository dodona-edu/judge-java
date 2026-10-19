import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class HungryTest {

    @Test
    @org.junit.jupiter.api.Timeout(10)
    public void eats() {
        new Hungry().eat();
    }

    @Test
    public void passes() {
        assertTrue(true);
    }

}
