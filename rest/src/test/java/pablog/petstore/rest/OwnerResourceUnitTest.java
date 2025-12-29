package pablog.petstore.rest;

import pablog.petstore.domain.entities.Owner;
import pablog.petstore.domain.entities.OwnersDataset;
import pablog.petstore.rest.entity.OwnerCreationData;
import pablog.petstore.rest.entity.OwnerEditionData;
import pablog.petstore.service.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

import static pablog.petstore.domain.entities.IsEqualToOwner.containsOwnersInAnyOrder;
import static pablog.petstore.domain.entities.IsEqualToOwner.equalToOwner;
import static pablog.petstore.domain.entities.OwnersDataset.*;
import static pablog.petstore.http.util.HasHttpStatus.hasCreatedStatus;
import static pablog.petstore.http.util.HasHttpStatus.hasOkStatus;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerResourceUnitTest {
    @InjectMocks
    private OwnerResource resource;

    @Mock
    private OwnerService facade;

    @Mock
    private UriInfo uriInfo;

    @Mock
    private UriBuilder uriBuilder;

    @Test
    void testGet() {
        final Owner owner = OwnersDataset.anyOwner();

        when(facade.get(owner.getLogin())).thenReturn(owner);

        final Response response = resource.get(owner.getLogin());

        assertThat(response, hasOkStatus());
        assertThat(response.getEntity(), is(instanceOf(Owner.class)));
        assertThat((Owner) response.getEntity(), is(equalToOwner(owner)));
        verify(facade).get(owner.getLogin());
    }

    @Test
    void testGetNull() {
        assertThrows(IllegalArgumentException.class, () -> resource.get(null));
    }

    @Test
    void testGetMissing() {
        final String login = anyLogin();

        when(facade.get(login)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> resource.get(login));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testList() {
        final Owner[] owners = owners();

        when(facade.list()).thenReturn(asList(owners));

        final Response response = resource.list();

        assertThat(response, hasOkStatus());
        assertThat(response.getEntity(), is(instanceOf(List.class)));
        assertThat((List<Owner>) response.getEntity(), containsOwnersInAnyOrder(owners));
        verify(facade).list();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testListEmpty() {
        when(facade.list()).thenReturn(emptyList());

        final Response response = resource.list();

        assertThat(response, hasOkStatus());
        assertThat(response.getEntity(), is(instanceOf(List.class)));
        assertThat((List<Owner>) response.getEntity(), is(empty()));
        verify(facade).list();
    }

    @Test
    void testCreate() throws Exception {
        final OwnerCreationData newOwner = new OwnerCreationData(newOwnerLogin(), newOwnerPassword());
        final Owner createdOwner = OwnersDataset.newOwnerWithoutPets();

        final URI mockUri = new URI("http://host/api/owners/" + newOwner.getLogin());

        when(facade.create(any(Owner.class))).thenReturn(createdOwner);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
        when(uriBuilder.path(newOwner.getLogin())).thenReturn(uriBuilder);
        when(uriBuilder.build()).thenReturn(mockUri);

        final Response response = resource.create(newOwner);

        assertThat(response, hasCreatedStatus());
        assertThat(response.getHeaderString("Location"), is(equalTo(mockUri.toString())));
        verify(facade).create(any(Owner.class));
    }

    @Test
    void testCreateNull() {
        assertThrows(IllegalArgumentException.class, () -> resource.create(null));
    }

    @Test
    void testCreateExistentOwner() {
        final OwnerCreationData existentOwner = new OwnerCreationData(existentLogin(), existentPassword());

        when(facade.create(any(Owner.class))).thenThrow(new EntityExistsException());

        assertThrows(IllegalArgumentException.class, () -> resource.create(existentOwner));
    }

    @Test
    void testUpdate() {
        final Owner owner = existentOwner();
        final Owner ownerWithChangedPassword = existentOwner();
        ownerWithChangedPassword.changePassword(newPasswordForExistentOwner());

        final OwnerEditionData ownerData = new OwnerEditionData(newPasswordForExistentOwner());

        when(facade.get(owner.getLogin())).thenReturn(owner);
        when(facade.update(any(Owner.class))).thenReturn(owner);

        final Response response = resource.update(owner.getLogin(), ownerData);

        assertThat(response, hasOkStatus());
        verify(facade).get(owner.getLogin());
        verify(facade).update(any(Owner.class));
    }

    @Test
    void testUpdateNullLogin() {
        assertThrows(IllegalArgumentException.class, () -> resource.update(null, null));
    }

    @Test
    void testDelete() {
        final String login = anyLogin();

        final Response response = resource.delete(login);

        assertThat(response, hasOkStatus());
        verify(facade).remove(login);
    }

    @Test
    void testDeleteNull() {
        assertThrows(IllegalArgumentException.class, () -> resource.delete(null));
    }
}
