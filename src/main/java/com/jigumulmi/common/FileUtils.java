package com.jigumulmi.common;

import java.util.UUID;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    /**
     * 고유한 랜덤 파일 이름을 생성하는 유틸 메서드
     * @param file MultipartFile 객체
     * @return UUID 기반의 고유 파일 이름
     */
    public static String generateUniqueFilename(MultipartFile file) {
        if (file == null) {
            throw new NullPointerException("No File");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);

        if (StringUtils.hasText(fileExtension)) {
            return UUID.randomUUID() + "." + fileExtension;
        } else {
            return UUID.randomUUID().toString();
        }
    }
}
