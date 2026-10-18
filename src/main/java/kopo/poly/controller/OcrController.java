package kopo.poly.controller;

import kopo.poly.dto.OcrDTO;
import kopo.poly.service.IOcrService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/ocr")
@RequiredArgsConstructor
@Controller
public class OcrController {

    private final IOcrService ocrService;

    // 업로드되는 파일이 저장되는 기본 폴더 설정(자바에서 경로는 /로 표현함)
    @Value("${ocr.upload-dir}")
    private String uploadDir;


    /**
     * 이미지 인식을 위한 파일업로드 화면 호출
     */
    @GetMapping(value = "uploadImage")
    public String uploadImage() {
        log.info(this.getClass().getName() + ".uploadImage!");

        return "ocr/uploadImage";
    }

    /**
     * 파일업로드 및 이미지 인식
     */
    @PostMapping(value = "readImage")
    public String readImage(ModelMap model, @RequestParam(value = "fileUpload") MultipartFile mf)
            throws Exception {

        log.info(this.getClass().getName() + ".readImage Start!");

        if (mf == null || mf.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일을 선택해 주세요.");
        }
        String originalFileName = Optional.ofNullable(mf.getOriginalFilename()).orElse("");
        String ext = originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf('.') + 1)
                    .toLowerCase(Locale.ROOT)
                : "";
        if (!(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JPG, PNG, GIF 이미지만 허용합니다.");
        }
        Path folder = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(folder);
        // 임시 파일명을 랜덤으로 생성하여 동시 요청 간 충돌을 방지합니다.
        Path uploaded = Files.createTempFile(folder, "ocr-" + UUID.randomUUID(), "." + ext);
        String res;
        try {
            mf.transferTo(uploaded);
            if (ImageIO.read(uploaded.toFile()) == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정상적인 이미지가 아닙니다.");
            }
            OcrDTO dto = new OcrDTO();
            dto.setFileName(uploaded.getFileName().toString());
            dto.setFilePath(folder.toString());
            dto.setExt(ext);
            dto.setOrgFileName(originalFileName);
            dto.setRegId("admin");
            OcrDTO extracted = ocrService.getReadforImageText(dto);
            res = extracted == null ? "" : CmmUtil.nvl(extracted.getTextFromImage());
        } finally {
            Files.deleteIfExists(uploaded);
        }

        // 이미지로부터 인식된 문자를 JSP에 전달하기
        model.addAttribute("res", res);

        log.info(this.getClass().getName() + ".readImage End!");

        return "ocr/readImage";
    }
}

