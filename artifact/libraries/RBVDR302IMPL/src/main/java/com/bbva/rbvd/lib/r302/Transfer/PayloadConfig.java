package com.bbva.rbvd.lib.r302.Transfer;

import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;

import java.math.BigDecimal;

public class PayloadConfig {

    ProductInformationDAO productInformation;
    BigDecimal sumCumulus;



    public ProductInformationDAO getProductInformation() {
        return productInformation;
    }

    public void setProductInformation(ProductInformationDAO productInformation) {
        this.productInformation = productInformation;
    }

    public BigDecimal getSumCumulus() {
        return sumCumulus;
    }

    public void setSumCumulus(BigDecimal sumCumulus) {
        this.sumCumulus = sumCumulus;
    }
}
