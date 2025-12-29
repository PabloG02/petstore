package pablog.petstore.http.util;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.StatusType;

import static jakarta.ws.rs.core.Response.Status.*;

public class HasHttpStatus extends TypeSafeMatcher<Response> {
    private final StatusType status;

    public HasHttpStatus(StatusType status) {
        this.status = status;
    }

    public HasHttpStatus(int statusCode) {
        this(Response.Status.fromStatusCode(statusCode));
    }

    public static HasHttpStatus hasHttpStatus(int statusCode) {
        return new HasHttpStatus(statusCode);
    }

    public static HasHttpStatus hasHttpStatus(StatusType status) {
        return new HasHttpStatus(status);
    }

    public static HasHttpStatus hasOkStatus() {
        return new HasHttpStatus(OK);
    }

    public static HasHttpStatus hasCreatedStatus() {
        return new HasHttpStatus(CREATED);
    }

    public static HasHttpStatus hasMethodNotAllowedStatus() {
        return new HasHttpStatus(METHOD_NOT_ALLOWED);
    }

    public static HasHttpStatus hasBadRequestStatus() {
        return new HasHttpStatus(BAD_REQUEST);
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(this.status);
    }

    @Override
    protected boolean matchesSafely(Response item) {
        return this.status.getStatusCode() == item.getStatus();
    }
}
