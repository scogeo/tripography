package com.rumbleware.invites;

import com.tripography.config.TripDbTestConfig;
import com.tripography.web.config.TripographyConfig;
import com.tripography.web.config.WebConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

/**
 * @author gscott
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TripDbTestConfig.class, TripographyConfig.class} )
@Ignore
public class InviteRepositoryServiceTest {

    private InviteRepositoryService service;

    private InviteRepository mockedRepository;

    @Before
    public void initService() {
        mockedRepository = mock(InviteRepository.class);
        service = new InviteRepositoryService(mockedRepository);
    }

    @After
    public void destroyService() {
        service = null;
        mockedRepository = null;
    }

    @Test
    public void testNewInviteRequest() {
        InviteRequest request = service.newObject();
        assertNotNull(request);
    }

    /**
     * Tests the positive save case with the minimum required fields set.
     */
    @Test
    public void testSaveMinFields() {
        InviteRequest request = service.newObject();

        request.setEmail("bob@cat.com");
        request.setFullname("Bob Cat");

        InviteRequest doc = (InviteRequest)request;

        service.create(request);

        verify(mockedRepository, only()).save(doc);
    }

    /*
    @Test(expected = IllegalArgumentException.class)
    public void saveBadInviteRequest() {
        InviteRequest mockedInviteRequest = mock(InviteRequest.class);
        service.update(mockedInviteRequest);
    }
    */

    // Negative tests below.
    // TODO should really verify correct constraint violation, and not just arbitrary exception
    // Also, should probably sense these in as JSON or some other test vector approach

    @Test(expected = ConstraintViolationException.class)
    public void testNullEmail() {
        InviteRequest inviteRequest = service.newObject();
        inviteRequest.setFullname("Bob Cat");

        assertNull(inviteRequest.getEmail());
        service.create(inviteRequest);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmptyEmail() {
        InviteRequest inviterequest = service.newObject();
        inviterequest.setEmail("");
        inviterequest.setFullname("Bob Cat");

        assertEquals("", inviterequest.getEmail());
        service.create(inviterequest);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidEmail() {
        InviteRequest inviterequest = service.newObject();

        inviterequest.setEmail("bob");
        inviterequest.setFullname("Bob Cat");

        service.create(inviterequest);
    }

    @Test(expected = ConstraintViolationException.class)
    @Ignore
    public void testNullFullname() {
        InviteRequest inviterequest = service.newObject();

        inviterequest.setEmail("bob@cat.com");

        assertNull(inviterequest.getFullname());
        service.create(inviterequest);
    }

    @Test(expected = ConstraintViolationException.class)
    @Ignore
    public void testEmptyFullname() {
        InviteRequest inviterequest = service.newObject();
        inviterequest.setEmail("bob@cat.com");
        inviterequest.setFullname("");

        assertEquals("", inviterequest.getFullname());
        service.create(inviterequest);
    }

    /*
    @Test
    public void deleteInviteRequest() {
        InviteRequest inviterequest = service.newInviteRequest();
        service.delete(inviterequest);

        verify(mockedRepository, only()).delete((InviteRequestDocument)inviterequest);
    }

    @Test
    public void deleteInviteRequestId() {
        InviteRequest inviterequest = service.newInviteRequest();
        service.delete(inviterequest.getId());
        verify(mockedRepository, only()).delete(new ObjectId(inviterequest.getId()));
    }

    @Test
    public void testCount() {
        assertEquals(0, service.numberOfInviteRequests());

        when(mockedRepository.count()).thenReturn(100L);

        assertEquals(100, service.numberOfInviteRequests());
    }

    @Test
    public void findById() {
        InviteRequest goodInviteRequest = service.newInviteRequest();
        InviteRequest badInviteRequest = service.newInviteRequest();

        when(mockedRepository.findOne(new ObjectId(goodInviteRequest.getId()))).thenReturn((InviteRequestDocument) goodInviteRequest);
        when(mockedRepository.findOne(new ObjectId(badInviteRequest.getId()))).thenReturn(null);

        assertEquals(goodInviteRequest, service.findById(goodInviteRequest.getId()));
        assertNull(service.findById(badInviteRequest.getId()));
    }

    @Test
    public void testFindByEmail() {
        InviteRequestDocument goodInviteRequest = new InviteRequestDocument();
        when(mockedRepository.findByEmail("good@a.com")).thenReturn(goodInviteRequest);
        when(mockedRepository.findByEmail("bad@a.com")).thenReturn(null);

        assertEquals(goodInviteRequest, service.findByEmail("good@a.com"));
        assertNull(service.findByEmail("bad@a.com"));

    }
    */
}
