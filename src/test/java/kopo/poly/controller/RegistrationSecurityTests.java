package kopo.poly.controller;

import kopo.poly.service.IUserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationSecurityTests {

    @Test
    void registrationWithoutVerifiedEmailIsRejectedBeforeDatabaseWrite() throws Exception {
        IUserInfoService service = mock(IUserInfoService.class);
        UserInfoController controller = new UserInfoController(service);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("userId", "student");
        request.setParameter("email", "student@example.com");
        request.setParameter("authNumber", "123456");

        var response = controller.insertUserInfo(request);
        assertEquals(0, response.getResult());
        verifyNoInteractions(service);
    }
}
