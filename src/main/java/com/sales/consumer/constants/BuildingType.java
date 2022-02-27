package com.sales.consumer.constants;

import static com.sales.consumer.constants.SalesConstants.APT_STR;
import static com.sales.consumer.constants.SalesConstants.HOUSE_STR;

public enum BuildingType {
    HOUSE(HOUSE_STR),APT(APT_STR),
    A(APT_STR),H(HOUSE_STR);

    String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    BuildingType(String description) {
        this.description = description;
    }

}
