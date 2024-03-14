package com.mszlu.blog.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Archive {
    private Integer year;
    private Integer month;
    private Long count;
}
