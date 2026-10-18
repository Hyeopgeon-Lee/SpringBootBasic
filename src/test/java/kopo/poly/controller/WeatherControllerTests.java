package kopo.poly.controller;

import kopo.poly.service.IWeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherControllerTests {
    private final IWeatherService service = mock(IWeatherService.class);
    private final WeatherController controller = new WeatherController(service);

    @Test
    void invalidLatitudeReturns400() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("lat", "181");
        request.setParameter("lon", "126.9780");
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.getWeather(request));
        assertEquals(400, error.getStatusCode().value());
        verifyNoInteractions(service);
    }

    @Test
    void invalidLongitudeReturns400() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("lat", "37.5");
        request.setParameter("lon", "NaN");
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.getWeather(request));
        assertEquals(400, error.getStatusCode().value());
        verifyNoInteractions(service);
    }
}
