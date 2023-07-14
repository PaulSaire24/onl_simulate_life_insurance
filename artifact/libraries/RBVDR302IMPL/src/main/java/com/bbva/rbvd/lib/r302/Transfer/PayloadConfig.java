package com.bbva.rbvd.lib.r302.Transfer;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;

import java.util.List;
import java.math.BigDecimal;

public class PayloadConfig {

    ProductInformationDAO productInformation;
    BigDecimal sumCumulus;


    CustomerListASO customerListASO;

    List<InsuranceProductModalityDAO> listInsuranceProductModalityDAO;


    public List<InsuranceProductModalityDAO> getListInsuranceProductModalityDAO() {
        return listInsuranceProductModalityDAO;
    }

    public void setListInsuranceProductModalityDAO(List<InsuranceProductModalityDAO> listInsuranceProductModalityDAO) {
        this.listInsuranceProductModalityDAO = listInsuranceProductModalityDAO;
    }

    public ProductInformationDAO getProductInformation() {
        return productInformation;
    }

    public void setProductInformation(ProductInformationDAO productInformation) {
        this.productInformation = productInformation;
    }

    public CustomerListASO getCustomerListASO() {return customerListASO;}
    public void setCustomerListASO(CustomerListASO customerListASO) {this.customerListASO = customerListASO;}
    public BigDecimal getSumCumulus() {
        return sumCumulus;
    }

    public void setSumCumulus(BigDecimal sumCumulus) {
        this.sumCumulus = sumCumulus;
    }
}
