package org.example.Dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseDto {
    private Integer status;
    private String message;
    private Object data;
}
