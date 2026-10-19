import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HungryTest {

    @Test
    public void eats() {
        new Hungry().eat();
    }

    @Test
    public void passes() {
        assertTrue(true);
    }

}
