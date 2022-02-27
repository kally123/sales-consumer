package com.sales.consumer.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sales.consumer.constants.BuildingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesObject {
    private String type;
    private int id;
    private String sizeSqm;
    private long startingPrice;

    private PostalAddress postalAddress;

    @JsonIgnore
    public String getPricePerSquareMeter() {
        String pricePerSquareMeter = "";
        double price = startingPrice / Integer.parseInt(sizeSqm);
        pricePerSquareMeter = String.format("value is %.3f", price).replace(".", "");

        return pricePerSquareMeter;
    }

    public SalesObject(CsvSaleObject csvSaleObject){
        this.type = csvSaleObject.getType();
        this.sizeSqm = csvSaleObject.getSizeSqm();
        this.startingPrice = csvSaleObject.getStartingPrice();
        this.postalAddress = PostalAddress.builder()
                .city(csvSaleObject.getCity())
                .street(csvSaleObject.getStreet())
                .floor(csvSaleObject.getFloor())
                .build();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        try {
            this.type = BuildingType.valueOf(type.toUpperCase(Locale.ROOT)).getDescription();
        }
        catch (Exception e){
            this.type = BuildingType.A.getDescription();
        }
    }
}
