package org.example.Dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseTransactionHistoryDto {
    private Integer offset;
    private Integer limit;
    private List<TransactionResponseDto> records;
}
