package com.jigumulmi.common;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    /**
     * 멀티파트 파일을 기반으로 하여 고유한 랜덤 파일 이름 생성
     * @param file MultipartFile 객체
     * @return UUID 기반의 고유 파일 이름
     */
    public static String generateUniqueFilename(@NotNull MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);

        if (StringUtils.hasText(fileExtension)) {
            return UUID.randomUUID() + "." + fileExtension;
        } else {
            return UUID.randomUUID().toString();
        }
    }
}
