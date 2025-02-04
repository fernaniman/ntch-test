package org.example.Dto;

import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class TopUpDto {
    @NotNull(message = "amount cannot be null")
    @NumberFormat
    @Positive(message = "amount cannot be less than 0")
    @DecimalMin(value = "0.01", message = "amount cannot be less than 0.01")
    private BigDecimal amount;
}
