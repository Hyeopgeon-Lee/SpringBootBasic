package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.WeatherDTO;
import kopo.poly.service.IWeatherService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequestMapping(value = "/weather")
@RequiredArgsConstructor
@RestController
public class WeatherController {

    private final IWeatherService weatherService;

    @GetMapping(value = "getWeather")
    public WeatherDTO getWeather(HttpServletRequest request) throws Exception {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".getWeather Start!");

        String lat = CmmUtil.nvl(request.getParameter("lat")); // 자바스크립트로부터 받은 위, 경도 값
        String lon = CmmUtil.nvl(request.getParameter("lon")); // 자바스크립트로부터 받은 위, 경도 값

        validateCoordinate(lat, -90, 90, "lat");
        validateCoordinate(lon, -180, 180, "lon");

        WeatherDTO pDTO = new WeatherDTO();
        pDTO.setLat(lat);
        pDTO.setLon(lon);

        // 내가 필요한 정보만 추출한 날씨 정보 가져오기
        WeatherDTO rDTO = weatherService.getWeather(pDTO);

        if (rDTO == null) {
            rDTO = new WeatherDTO();

        }

        // 로그 찍기(추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".getWeather End!");

        return rDTO;
    }
    private static void validateCoordinate(String value, double min, double max, String parameter) {
        try {
            double number = Double.parseDouble(value);
            if (Double.isFinite(number) && number >= min && number <= max) return;
        } catch (NumberFormatException ignored) {
            // Invalid coordinates are reported as HTTP 400.
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                parameter + " must be a valid coordinate");
    }
}
