package com.sales.consumer.mappers;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.sales.consumer.vo.CsvSaleObject;
import com.sales.consumer.vo.SalesObject;
import com.sales.consumer.vo.SalesObjectsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CsvMapper implements FileMapper {

    @Override
    public SalesObjectsRequest map(File file) {
        SalesObjectsRequest salesObjectsRequest = null;
        List<CsvSaleObject> beans = null;
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            beans = new CsvToBeanBuilder(reader)
                    .withType(CsvSaleObject.class)
                    .build()
                    .parse();
            if (validateData(beans)) {
                salesObjectsRequest = new SalesObjectsRequest();
                String rows = beans.get(beans.size()-1).getSizeSqm();
                salesObjectsRequest.setNumberOfSaleObjects(Integer.parseInt(rows));
                beans.remove(0);// Remove START row
                beans.remove(beans.size()-1);// Remove END row
                List<SalesObject> salesObjects = beans.stream().map(SalesObject::new).collect(Collectors.toList());
                salesObjectsRequest.setSaleObjects(salesObjects);
            }
        } catch (Exception e) {
            log.error("Exception Occurred : ", e);
        }
        return salesObjectsRequest;
    }

    private boolean validateData(List<CsvSaleObject> data) {
        return !CollectionUtils.isEmpty(data) && data.get(0).toString().contains("START") &&
                data.get(data.size()-1).toString().contains("END");
    }

}
