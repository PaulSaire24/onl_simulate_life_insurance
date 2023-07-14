package com.bbva.rbvd.lib.r302.Transfer;

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


    public PayloadStore(String creationUser, String userAudit, InsuranceLifeSimulationBO responseRimac, LifeSimulationDTO response, String documentTypeId, ProductInformationDAO productInformation) {
        this.creationUser = creationUser;
        this.userAudit = userAudit;
        this.responseRimac = responseRimac;
        this.response = response;
        this.documentTypeId = documentTypeId;
        this.productInformation = productInformation;


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


}
