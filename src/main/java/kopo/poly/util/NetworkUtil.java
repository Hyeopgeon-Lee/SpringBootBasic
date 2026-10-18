package kopo.poly.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 교육용 간단 HTTP 클라이언트.
 * 외부 API 응답 오류를 정상 JSON과 구별하고 무제한 대기를 방지한다.
 */
public final class NetworkUtil {

    private NetworkUtil() {
    }

    public static String get(String apiUrl) {
        return get(apiUrl, null);
    }

    public static String get(String apiUrl, Map<String, String> requestHeaders) {
        return send("GET", apiUrl, requestHeaders, null);
    }

    public static String post(String apiUrl, Map<String, String> requestHeaders, String postParams) {
        return send("POST", apiUrl, requestHeaders, postParams);
    }

    private static String send(String method, String apiUrl, Map<String, String> headers, String body) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod(method);
            if (headers != null) {
                headers.forEach(connection::setRequestProperty);
            }
            if ("POST".equals(method)) {
                connection.setDoOutput(true);
                byte[] bytes = (body == null ? "" : body).getBytes(StandardCharsets.UTF_8);
                try (var output = connection.getOutputStream()) {
                    output.write(bytes);
                }
            }
            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IllegalStateException("External API request failed (HTTP " + code + ")");
            }
            try (InputStream input = connection.getInputStream()) {
                return readBody(input);
            }
        } catch (IOException ex) {
            // URL/credentials are intentionally not included in the error message.
            throw new IllegalStateException("External API connection failed", ex);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static String readBody(InputStream input) throws IOException {
        if (input == null) return "";
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
