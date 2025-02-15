package com.jigumulmi.place.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateReviewImageS3KeyDto {

    private List<String> newS3KeyList;
    private List<String> trashS3KeyList;
}
