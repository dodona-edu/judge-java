public class Calculator {

    public int square(int x) {
        System.exit(3);
        return x * x;
    }

    public int cube(int x) {
        throw new IllegalStateException("not implemented");
    }

    public int twice(int x) {
        return 2 * x;
    }

}
