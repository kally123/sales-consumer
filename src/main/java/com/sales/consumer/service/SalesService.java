package com.sales.consumer.service;

import com.sales.consumer.mappers.CsvMapper;
import com.sales.consumer.mappers.FileMapper;
import com.sales.consumer.mappers.JsonMapper;
import com.sales.consumer.mappers.XmlMapper;
import com.sales.consumer.partners.CityConsumerImpl;
import com.sales.consumer.partners.PriceSquareMetersConsumerImpl;
import com.sales.consumer.partners.SaleObjectConsumer;
import com.sales.consumer.partners.SaleObjectConsumer.PriorityOrderAttribute;
import com.sales.consumer.partners.SquareMetersConsumerImpl;
import com.sales.consumer.vo.SalesObject;
import com.sales.consumer.vo.SalesObjectsRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.sales.consumer.constants.SalesConstants.*;

@Slf4j
@Service
public class SalesService {

    @Autowired
    JsonMapper jsonMapper;

    @Autowired
    XmlMapper xmlMapper;

    @Autowired
    CsvMapper csvMapper;

    public List<SalesObjectsRequest> parseInputFile(String[] fileNames) {
        List<SalesObjectsRequest> requests = new ArrayList<>();
        Arrays.stream(fileNames).forEach(fileName -> {
            String extention = FilenameUtils.getExtension(fileName);
            FileMapper fileMapper = null;
            if (JSON_EXTENSION.equalsIgnoreCase(extention)) {
                fileMapper = jsonMapper;
            } else if (XML_EXTENSION.equalsIgnoreCase(extention)) {
                fileMapper = xmlMapper;
            } else if (CSV_EXTENSION.equalsIgnoreCase(extention)) {
                fileMapper = csvMapper;
            }
            try {
                requests.add(fileMapper.map(ResourceUtils.getFile(CLASSPATH_STR + fileName)));
            } catch (FileNotFoundException e) {
                log.error("Exception Occurred : ", e);
            }
        });

        return requests;
    }


    public void processSaleData(String[] fileNames) {
        List<SalesObjectsRequest> requestsData = parseInputFile(fileNames);
        List<SaleObjectConsumer> consumers = Arrays.asList(new CityConsumerImpl(), new SquareMetersConsumerImpl(), new PriceSquareMetersConsumerImpl());
        requestsData.forEach(requestData -> {
            consumers.forEach(saleObjectConsumer -> {
                processPartnerUploadAsync(requestData, saleObjectConsumer);
            });
        });

    }

    private CompletableFuture<Void> processPartnerUploadAsync(SalesObjectsRequest requestData, SaleObjectConsumer saleObjectConsumer) {
        return CompletableFuture.runAsync(saleObjectConsumer::startSaleObjectTransaction)
                .thenApply(unused -> sortByPriorityOrderAttribute(saleObjectConsumer.getPriorityOrderAttribute(), requestData))
                .thenAccept(salesObjects -> {
                    salesObjects.forEach(so -> saleObjectConsumer.reportSaleObject(Integer.parseInt(so.getSizeSqm()), so.getPricePerSquareMeter(), so.getPostalAddress().getCity(),
                            so.getPostalAddress().getStreet(), so.getPostalAddress().getFloor()));
                })
                .thenAccept(unused -> saleObjectConsumer.commitSaleObjectTransaction())
                .thenAccept(unused -> log.info("Successfully processed data for {}", saleObjectConsumer.getClass().getName()));
    }

    /**
     * @deprecated
     * Can be used if we we dont want to use completeble future way implementation
     * @param requestData
     * @param saleObjectConsumer
     * @return
     */
    @Deprecated
    private CompletableFuture<?> processPartnerUpload(SalesObjectsRequest requestData, SaleObjectConsumer saleObjectConsumer) {
        saleObjectConsumer.startSaleObjectTransaction();
        List<SalesObject> salesObjects = sortByPriorityOrderAttribute(saleObjectConsumer.getPriorityOrderAttribute(), requestData);
        salesObjects.forEach(so -> saleObjectConsumer.reportSaleObject(Integer.parseInt(so.getSizeSqm()), so.getPricePerSquareMeter(), so.getPostalAddress().getCity(),
                so.getPostalAddress().getStreet(), so.getPostalAddress().getFloor()));
        saleObjectConsumer.commitSaleObjectTransaction();

        return CompletableFuture.completedFuture(null);
    }

    private List<SalesObject> sortByPriorityOrderAttribute(PriorityOrderAttribute priorityOrderAttribute, SalesObjectsRequest requestData) {
        List<SalesObject> salesObjects = null;
        if (PriorityOrderAttribute.City == priorityOrderAttribute) {
            salesObjects = requestData.getSaleObjects().stream().sorted(Comparator.comparing(a -> a.getPostalAddress().getCity(),
                    Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
        } else if (PriorityOrderAttribute.PricePerSquareMeter == priorityOrderAttribute) {
            salesObjects = requestData.getSaleObjects().stream().sorted(Comparator.comparing(a -> a.getPricePerSquareMeter(),
                    Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
        } else if (PriorityOrderAttribute.SquareMeters == priorityOrderAttribute) {
            salesObjects = requestData.getSaleObjects().stream().sorted(Comparator.comparing(a -> Integer.parseInt(a.getSizeSqm()),
                    Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
        } else { // Default condition
            salesObjects = requestData.getSaleObjects();
        }

        return salesObjects;
    }
}
