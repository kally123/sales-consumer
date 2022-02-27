package com.sales.consumer.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesObjectsRequest {
    private int numberOfSaleObjects;
    private List<SalesObject> saleObjects;

}
