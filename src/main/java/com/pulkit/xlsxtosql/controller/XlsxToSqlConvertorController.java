package com.pulkit.xlsxtosql.controller;

import com.pulkit.xlsxtosql.dto.XlsxToSqlFromFileRequest;
import com.pulkit.xlsxtosql.dto.XlsxToSqlFromUrlRequest;
import com.pulkit.xlsxtosql.service.XlsxToSqlConvertorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class XlsxToSqlConvertorController {

    @Autowired
    XlsxToSqlConvertorService xlsxToSqlConvertorService;
    
    @GetMapping(value = "/hello")
    public String hello(){
        return "Hello 123";
    }

    @PostMapping(value = "/xlsxToSqlFromFile", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> xlsxToSqlFromFile(@RequestBody XlsxToSqlFromFileRequest convertRequest){
        try {
            String response = xlsxToSqlConvertorService.convertFromFile(convertRequest.getFilePath());
            return ResponseEntity.ok(response);
        }
        catch (IOException e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping(value = "/xlsxToSqlFromUrl", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> xlsxToSqlFromUrl(@RequestBody XlsxToSqlFromUrlRequest convertRequest){
        try {
            String response = xlsxToSqlConvertorService.convertFromUrl(convertRequest.getFileUrl());
            return ResponseEntity.ok(response);
        }
        catch (IOException e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
