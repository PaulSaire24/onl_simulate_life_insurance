package com.bbva.rbvd.lib.r302.transfer;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

import java.util.List;
import java.math.BigDecimal;

public class PayloadConfig {

    private ProductInformationDAO productInformation;
    private BigDecimal sumCumulus;
    private CustomerListASO customerListASO;

    private List<InsuranceProductModalityDAO> listInsuranceProductModalityDAO;
    private LifeSimulationDTO input;
    private PayloadProperties properties;
    private boolean isParticipant;


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

    public LifeSimulationDTO getInput() {
        return input;
    }

    public void setInput(LifeSimulationDTO input) {
        this.input = input;
    }

    public PayloadProperties getProperties() {
        return properties;
    }

    public void setProperties(PayloadProperties properties) {
        this.properties = properties;
    }

    public boolean isParticipant() {
        return isParticipant;
    }

    public void setParticipant(boolean participant) {
        isParticipant = participant;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PayloadConfig{");
        sb.append("productInformation=").append(productInformation);
        sb.append(", sumCumulus=").append(sumCumulus);
        sb.append(", customerListASO=").append(customerListASO);
        sb.append(", listInsuranceProductModalityDAO=").append(listInsuranceProductModalityDAO);
        sb.append(", input=").append(input);
        sb.append(", properties=").append(properties);
        sb.append(", participant=").append(isParticipant);
        sb.append('}');
        return sb.toString();
    }
}
