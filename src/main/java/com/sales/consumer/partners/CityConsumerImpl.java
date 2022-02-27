package com.sales.consumer.partners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CityConsumerImpl implements SaleObjectConsumer {
    @Override
    public PriorityOrderAttribute getPriorityOrderAttribute() {
        return PriorityOrderAttribute.City;
    }

    @Override
    public void startSaleObjectTransaction() {
        log.info("startSaleObjectTransaction STARTED");
    }

    @Override
    public void reportSaleObject(int squareMeters, String pricePerSquareMeter, String city, String street, Integer floor) throws TechnicalException {
        log.info("reportSaleObject STARTED");
    }

    @Override
    public void commitSaleObjectTransaction() {
        log.info("commitSaleObjectTransaction Committed");
    }
}
