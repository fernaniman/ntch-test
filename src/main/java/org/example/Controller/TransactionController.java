package org.example.Controller;

import org.example.Dto.ResponseDto;
import org.example.Dto.TopUpDto;
import org.example.Dto.TransactionCreateDto;
import org.example.Service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/")
public class TransactionController {

    private Logger log = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/balance")
    public ResponseEntity<ResponseDto> getBalance(HttpServletRequest request) {
        return transactionService.getBalance(request);
    }

    @PostMapping("/topup")
    public ResponseEntity<ResponseDto> topUpBalance(@RequestBody TopUpDto topUpDto, HttpServletRequest request) {
        return transactionService.topUpBalance(topUpDto, request);
    }

    @PostMapping("/transaction")
    public ResponseEntity<ResponseDto> transaction(@RequestBody TransactionCreateDto transactionCreateDto, HttpServletRequest request) {
        return transactionService.transaction(transactionCreateDto, request);
    }

    @GetMapping("/transaction/history")
    public ResponseEntity<ResponseDto> getTransactionHistory(@Nullable @RequestParam("limit") Integer limit, @Nullable @RequestParam("offset") Integer offset) {
        return transactionService.getTransactionHistory(limit, offset);
    }
}
