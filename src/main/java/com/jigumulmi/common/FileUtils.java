package com.jigumulmi.common;

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class FileUtils {

    public static String generateUniqueFilename() {
        return UUID.randomUUID().toString();
    }

    /**
     * @param filename 경로가 포함되지 않은 파일 이름
     * @return 확장자가 제거된 파일 이름
     */
    public static String getFilenameWithoutExtension(String filename) {
        return StringUtils.substringBeforeLast(filename, ".");
    }

    /**
     * 
     * @param path 파일 이름이 포함된 전체 경로
     * @return 경로가 제거된 파일 이름
     */
    public static String getFilenameFromPath(String path) {
        return StringUtils.substringAfterLast(path, "/");
    }
}
