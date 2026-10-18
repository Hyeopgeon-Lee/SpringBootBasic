package kopo.poly.service;

import feign.Param;
import feign.Body;
import feign.Headers;
import feign.RequestLine;
import kopo.poly.dto.PapagoDTO;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "PapagoAPI", url = "https://openapi.naver.com")
public interface INaverAPIService {

    @RequestLine("POST /v1/papago/detectLangs")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("query={query}")
    PapagoDTO detectLangs(
            @Param("query") String query);

    @RequestLine("POST /v1/papago/n2mt")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("source={source}&target={target}&text={text}")
    PapagoDTO translate(
            @Param("source") String source,
            @Param("target") String target,
            @Param("text") String text);
}
