package com.reviewing.review.review.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {

    @NotNull(message = "Rating은 null일 수 없습니다.")
    @DecimalMin(value = "1.0", message = "Rating은 1 이상이어야 합니다.")
    private BigDecimal rating;
    @NotBlank(message = "Contents는 비어 있을 수 없습니다.")
    private String contents;

}
