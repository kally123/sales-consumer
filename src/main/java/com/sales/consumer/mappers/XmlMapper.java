package com.sales.consumer.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.consumer.vo.PostalAddress;
import com.sales.consumer.vo.SalesObject;
import com.sales.consumer.vo.SalesObjectsRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class XmlMapper implements FileMapper {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SalesObjectsRequest map(File file) {
        SalesObjectsRequest salesObjectsRequest = new SalesObjectsRequest();
        try {
            JSONObject json = XML.toJSONObject(new FileReader(file));
            int count = json.getJSONObject("SaleObjects").getInt("count");
            JSONArray jsonArray = json.getJSONObject("SaleObjects").getJSONArray("SaleObject");
            salesObjectsRequest.setNumberOfSaleObjects(count);
            salesObjectsRequest.setSaleObjects(mapJsonObject(jsonArray));
        } catch (JSONException e) {
            log.error("Exception Occurred : ", e);
        } catch (FileNotFoundException e) {
            log.error("Exception Occurred : ", e);
        } catch (IOException e) {
            log.error("Exception Occurred : ", e);
        }
        return salesObjectsRequest;
    }

    private List<SalesObject> mapJsonObject(JSONArray jsonArray) {
        List<SalesObject> salesObjects = new ArrayList<>();
        jsonArray.toList().forEach(o -> {
            SalesObject salesObject = new SalesObject();
            Map<String, Object> keyValueMap = (HashMap) o;
            salesObject.setId(Integer.parseInt(keyValueMap.get("id").toString()));
            salesObject.setSizeSqm(keyValueMap.get("sizeSqm").toString());
            salesObject.setStartingPrice(Long.valueOf(keyValueMap.get("startingPrice").toString()));
            salesObject.setType(keyValueMap.get("type").toString());
            Map<String, Object> addressMap = (HashMap) keyValueMap.get("address");
            PostalAddress postalAddress = PostalAddress.builder()
                    .city(correctNullString(addressMap.get("city")))
                    .street(correctNullString(addressMap.get("street")))
                    .floor(correctNullInt((addressMap.get("floor"))))
                    .build();
            salesObject.setPostalAddress(postalAddress);

            salesObjects.add(salesObject);
        });
        return salesObjects;
    }

    private String correctNullString(Object obj) {
        return null == obj ? "" : obj.toString();
    }

    private int correctNullInt(Object obj) {
        return null == obj || "".equals(obj) ? 0 : Integer.parseInt(obj.toString());
    }
}
