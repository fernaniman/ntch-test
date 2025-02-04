package org.example.Service;

import org.example.Dto.ResponseDto;
import org.example.Dto.TopUpDto;
import org.example.Dto.TransactionCreateDto;
import org.example.Dto.TransactionResponseDto;
import org.example.Entity.ServiceEntity;
import org.example.Entity.TransactionHistoryEntity;
import org.example.Entity.UserBalanceEntity;
import org.example.Repository.InfoRepository;
import org.example.Repository.TransactionRepository;
import org.example.Security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TransactionService {

    private Logger log = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private Validator validator;

    @Autowired
    private InfoRepository infoRepository;

    public ResponseEntity<ResponseDto> getBalance(HttpServletRequest request) {
        ResponseDto response = new ResponseDto();

        Integer userId = jwtTokenProvider.getIdFromToken(request);
        UserBalanceEntity balance = transactionRepository.findBalanceByUserId(userId);

        log.info("get balance user {}", userId);
        response.setStatus(200);
        response.setMessage("success get balance");
        response.setData(balance);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDto> topUpBalance(TopUpDto topUpDto, HttpServletRequest request) {
        ResponseDto response = new ResponseDto();

        var violations = validator.validate(topUpDto);
        if (!violations.isEmpty()) {
            List<Map<String, Object>> violationHeaderList = new ArrayList<>();
            List<String> validateHeader = new ArrayList<>();
            for (var violation : violations) {
                log.error(violation.getMessage());
                Map<String, Object> data = new HashMap<>();
                validateHeader.add(violation.getMessage());
                data.put(violation.getPropertyPath().toString(), violation.getMessage());
                violationHeaderList.add(data);
            }

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(validateHeader.get(0));
            response.setData(violationHeaderList);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Integer userId = jwtTokenProvider.getIdFromToken(request);
        UserBalanceEntity balance = transactionRepository.updateBalance(topUpDto.getAmount(), userId, Boolean.TRUE);

        log.info("top up balance user {}", userId);
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("success top up balance");
        response.setData(balance);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDto> transaction(TransactionCreateDto transactionCreateDto, HttpServletRequest request) {
        ResponseDto response = new ResponseDto();

        ServiceEntity getServ = infoRepository.findServiceByName(transactionCreateDto.getService());
        if (getServ == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("service not found");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Integer userId = jwtTokenProvider.getIdFromToken(request);
        UserBalanceEntity getBalance = transactionRepository.findBalanceByUserId(userId);

        if (getBalance.getBalance().compareTo(getServ.getTarif()) < 0) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("balance not enough");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        transactionRepository.updateBalance(getServ.getTarif(), userId, Boolean.FALSE);
        String invoiceNumber = this.generateInvNumb();
        TransactionResponseDto saveTransaction = transactionRepository.saveTransaction(getServ, userId, invoiceNumber);

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("success transaction");
        response.setData(saveTransaction);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDto> getTransactionHistory(Integer limit, Integer offset) {
        ResponseDto response = new ResponseDto();
        List<TransactionResponseDto> transaction = transactionRepository.getTransactionHistory(limit, offset);
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("success get transaction");
        response.setData(transaction);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public String generateInvNumb() {
        StringBuilder invNumb = new StringBuilder();
        invNumb.append("INV");

        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        invNumb.append(sdf.format(currentDate));

        String lastInvoice = transactionRepository.getLastInvoiceNumber();
        invNumb.append("-");
        if(lastInvoice == null) {
            invNumb.append("001");
        } else {
            int lastNumber = Integer.parseInt(lastInvoice.substring(12)) + 1;
            String formattedNumber = String.format("%03d", lastNumber);
            invNumb.append(formattedNumber);
        }

        return invNumb.toString();
    }



}
