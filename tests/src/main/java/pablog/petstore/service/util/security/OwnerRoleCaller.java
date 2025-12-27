package pablog.petstore.service.util.security;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RunAs;
import jakarta.ejb.Stateless;

import java.util.function.Supplier;

@Stateless(name = "owner-caller")
@RunAs("OWNER")
@PermitAll
public class OwnerRoleCaller implements RoleCaller {
    public <V> V call(Supplier<V> supplier) {
        return supplier.get();
    }

    public void run(Runnable run) {
        run.run();
    }

    public <V> V throwingCall(ThrowingSupplier<V> supplier) throws Throwable {
        return supplier.get();
    }

    public void throwingRun(ThrowingRunnable run) throws Throwable {
        run.run();
    }
}
