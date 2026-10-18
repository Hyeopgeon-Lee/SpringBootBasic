package kopo.poly.controller;

import kopo.poly.service.IMailService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailControllerTests {

    @Test
    void anonymousUserCannotSendMail() throws Exception {
        IMailService service = mock(IMailService.class);
        MailController controller = new MailController(service);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.sendMail(new MockHttpServletRequest(), new MockHttpSession()));
        assertEquals(403, error.getStatusCode().value());
        verifyNoInteractions(service);
    }
}
