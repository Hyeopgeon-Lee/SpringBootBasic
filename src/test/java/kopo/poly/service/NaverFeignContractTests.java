package kopo.poly.service;

import feign.Contract;
import feign.Feign;
import feign.Request;
import feign.Response;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NaverFeignContractTests {

    @Test
    void papagoRequestHasFormBodyRatherThanEmptyPost() {
        AtomicReference<Request> captured = new AtomicReference<>();
        INaverAPIService client = Feign.builder()
                .contract(new Contract.Default())
                .decoder((response, type) -> new kopo.poly.dto.PapagoDTO())
                .client((request, options) -> {
                    captured.set(request);
                    return Response.builder().status(200).reason("OK")
                            .request(request).body("{}", StandardCharsets.UTF_8).build();
                })
                .target(INaverAPIService.class, "http://127.0.0.1");

        client.detectLangs("hello");
        Request request = captured.get();
        assertNotNull(request);
        assertEquals("POST", request.httpMethod().name());
        assertEquals("/v1/papago/detectLangs", java.net.URI.create(request.url()).getPath());
        assertNotNull(request.body());
        String body = new String(request.body(), StandardCharsets.UTF_8);
        assertTrue(body.startsWith("query="), body);
        assertTrue(body.contains("hello"), body);
    }
}
