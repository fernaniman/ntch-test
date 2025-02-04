package org.example.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionResponseDto {
    private String invoice_number;
    private String service_code;
    private String service_name;
    private String transaction_type;
    private BigDecimal total_amount;
    private String description;
    private Date created_at;
}
