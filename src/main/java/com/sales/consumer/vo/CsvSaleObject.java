package com.sales.consumer.vo;

import com.opencsv.bean.CsvBindByPosition;
import com.sales.consumer.constants.BuildingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Locale;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CsvSaleObject {
    @CsvBindByPosition(position = 0)
    private String type;
    @CsvBindByPosition(position = 1)
    private String sizeSqm;
    @CsvBindByPosition(position = 2)
    private long startingPrice;

    @CsvBindByPosition(position = 3)
    private String city;
    @CsvBindByPosition(position = 4)
    private String street;
    @CsvBindByPosition(position = 5)
    private int floor;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        try {
            if(!Arrays.asList("START", "END").contains(type)) {
                this.type = BuildingType.valueOf(type.toUpperCase(Locale.ROOT)).getDescription();
            }
            else{
                this.type= type;
            }
        }
        catch (Exception e){
            this.type = BuildingType.A.getDescription();
        }
    }
}
