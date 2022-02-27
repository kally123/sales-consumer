package com.sales.consumer.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.consumer.vo.SalesObjectsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class JsonMapper implements FileMapper{

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SalesObjectsRequest map(File file) {
        SalesObjectsRequest salesObjectsRequest = null;
        try {
            salesObjectsRequest = objectMapper.readValue(file, SalesObjectsRequest.class);
        } catch (IOException e) {
            log.error("Exception Occurred : ", e);
        }
        return salesObjectsRequest;
    }
}
