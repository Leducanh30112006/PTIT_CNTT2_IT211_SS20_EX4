package com.example.ptit_cntt2_it211_ss20_ex4.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressResponse {
    private Integer totalCompletedLessons;
    private List<String> graduatedCourses;
}
