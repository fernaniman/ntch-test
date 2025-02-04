package org.example.Controller;

import org.example.Dto.ResponseDto;
import org.example.Entity.BannerEntity;
import org.example.Entity.ServiceEntity;
import org.example.Repository.InfoRepository;
import org.example.Repository.TransactionRepository;
import org.example.Service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/info")
public class InfoController {

    private Logger log = LoggerFactory.getLogger(InfoController.class);

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/banner")
    public ResponseEntity<ResponseDto> getBanner() {
        ResponseDto response = new ResponseDto();

        List<BannerEntity> banner = infoRepository.findAllBanner();

        response.setStatus(200);
        response.setMessage("success get all banner");
        response.setData(banner);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/services")
    public ResponseEntity<ResponseDto> getServices() {
        ResponseDto response = new ResponseDto();

        List<ServiceEntity> serv = infoRepository.findAllService();

        response.setStatus(200);
        response.setMessage("success get all service");
        response.setData(serv);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

