package com.sales.consumer.mappers;

import com.sales.consumer.vo.SalesObjectsRequest;

import java.io.File;

public interface FileMapper {
    SalesObjectsRequest map(File file);
}
