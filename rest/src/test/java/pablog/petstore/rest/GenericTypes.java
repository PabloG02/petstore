package pablog.petstore.rest;

import pablog.petstore.domain.entities.Owner;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import java.util.List;

public final class GenericTypes {
    private GenericTypes() {
    }

    public static class ListOwnerType extends GenericType<List<Owner>> {
        public static ListOwnerType INSTANCE = new ListOwnerType();

        public static List<Owner> readEntity(Response response) {
            return response.readEntity(INSTANCE);
        }
    }
}
