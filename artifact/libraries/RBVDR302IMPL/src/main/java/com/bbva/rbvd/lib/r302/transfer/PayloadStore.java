package com.bbva.rbvd.lib.r302.transfer;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

public class PayloadStore {

    private String creationUser;
    private String userAudit;
    private InsuranceLifeSimulationBO responseRimac;
    private LifeSimulationDTO response;
    private String documentTypeId;
    private ProductInformationDAO productInformation;
    private CustomerListASO customer;

    public PayloadStore(String creationUser, String userAudit, InsuranceLifeSimulationBO responseRimac, LifeSimulationDTO response, String documentTypeId, ProductInformationDAO productInformation, CustomerListASO customer) {
        this.creationUser = creationUser;
        this.userAudit = userAudit;
        this.responseRimac = responseRimac;
        this.response = response;
        this.documentTypeId = documentTypeId;
        this.productInformation = productInformation;
        this.customer = customer;
    }

    public String getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(String creationUser) {
        this.creationUser = creationUser;
    }

    public String getUserAudit() {
        return userAudit;
    }

    public void setUserAudit(String userAudit) {
        this.userAudit = userAudit;
    }

    public InsuranceLifeSimulationBO getResponseRimac() {
        return responseRimac;
    }

    public void setResponseRimac(InsuranceLifeSimulationBO responseRimac) {
        this.responseRimac = responseRimac;
    }

    public LifeSimulationDTO getResponse() {
        return response;
    }

    public void setResponse(LifeSimulationDTO response) {
        this.response = response;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public ProductInformationDAO getProductInformation() {
        return productInformation;
    }

    public void setProductInformation(ProductInformationDAO productInformation) {
        this.productInformation = productInformation;
    }

    public CustomerListASO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerListASO customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "PayloadStore{" +
                "creationUser='" + creationUser + '\'' +
                ", userAudit='" + userAudit + '\'' +
                ", responseRimac=" + responseRimac +
                ", response=" + response +
                ", documentTypeId='" + documentTypeId + '\'' +
                ", productInformation=" + productInformation +
                ", customer=" + customer +
                '}';
    }

    public static final class Builder {
        private String creationUser;
        private String userAudit;
        private InsuranceLifeSimulationBO responseRimac;
        private LifeSimulationDTO response;
        private String documentTypeId;
        private ProductInformationDAO productInformation;
        private CustomerListASO customer;

        private Builder() {
        }

        public static Builder an() {
            return new Builder();
        }

        public Builder creationUser(String creationUser) {
            this.creationUser = creationUser;
            return this;
        }

        public Builder userAudit(String userAudit) {
            this.userAudit = userAudit;
            return this;
        }

        public Builder responseRimac(InsuranceLifeSimulationBO responseRimac) {
            this.responseRimac = responseRimac;
            return this;
        }

        public Builder response(LifeSimulationDTO response) {
            this.response = response;
            return this;
        }

        public Builder documentTypeId(String documentTypeId) {
            this.documentTypeId = documentTypeId;
            return this;
        }

        public Builder productInformation(ProductInformationDAO productInformation) {
            this.productInformation = productInformation;
            return this;
        }

        public Builder customer(CustomerListASO customer) {
            this.customer = customer;
            return this;
        }

        public PayloadStore build() {
            return new PayloadStore(creationUser, userAudit, responseRimac, response, documentTypeId, productInformation, customer);
        }
    }
}
