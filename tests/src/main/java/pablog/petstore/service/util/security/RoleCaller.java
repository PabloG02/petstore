package pablog.petstore.service.util.security;

import jakarta.ejb.Local;

import java.util.function.Supplier;

@Local
public interface RoleCaller {
    <V> V call(Supplier<V> supplier);

    void run(Runnable run);

    <V> V throwingCall(ThrowingSupplier<V> supplier) throws Throwable;

    void throwingRun(ThrowingRunnable run) throws Throwable;

    interface ThrowingRunnable {
        void run() throws Throwable;
    }

    interface ThrowingSupplier<V> {
        V get() throws Throwable;
    }
}
