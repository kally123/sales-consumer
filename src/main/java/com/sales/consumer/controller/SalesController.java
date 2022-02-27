package com.sales.consumer.controller;

import com.sales.consumer.service.SalesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/sales-consumer/")
public class SalesController {

    @Autowired
    SalesService salesService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam String[] fileNames) {
        try {
            salesService.processSaleData(fileNames);
            return new ResponseEntity<>("Successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failure", HttpStatus.EXPECTATION_FAILED);
        }
    }
}
