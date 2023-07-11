package com.bbva.rbvd.lib.r302.Transfer;

import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;

public class PayloadConfig {

    ProductInformationDAO productInformation;


    public ProductInformationDAO getProductInformation() {
        return productInformation;
    }

    public void setProductInformation(ProductInformationDAO productInformation) {
        this.productInformation = productInformation;
    }
}
