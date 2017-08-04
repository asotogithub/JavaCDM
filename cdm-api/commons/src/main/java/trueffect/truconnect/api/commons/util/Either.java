package trueffect.truconnect.api.commons.util;

/**
 * Functional class that acts as a disjoint union.  This class can hold 1 of 2 possible types.
 * The Left is used for error conditions, the success is used for success conditions.
 */
public class Either<A,B> {
    private A error = null;
    private B success = null;

    private Either(A a,B b) {
        error = a;
        success = b;
    }

    public static <A,B> Either<A,B> error(A a) {
        return new Either<>(a,null);
    }

    public A error() {
        return error;
    }

    public boolean isError() {
        return error != null;
    }

    public boolean isSuccess() {
        return success != null;
    }

    public B success() {
        return success;
    }

    public static <A,B> Either<A,B> success(B b) {
        return new Either<>(null,b);
    }
}
