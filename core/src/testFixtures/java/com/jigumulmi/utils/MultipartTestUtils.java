package com.jigumulmi.utils;

import org.springframework.mock.web.MockMultipartFile;

public class MultipartTestUtils {

    public static MockMultipartFile createMockFile(String name) {
        return new MockMultipartFile(name, "content".getBytes());
    }

}
