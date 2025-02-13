package com.jigumulmi.common;

import java.util.UUID;

public class FileUtils {

    public static String generateUniqueFilename() {
        return UUID.randomUUID().toString();
    }
}
