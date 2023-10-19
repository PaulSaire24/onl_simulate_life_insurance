package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.AseguradoBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ParticipantDTO;

public class InsuranceBean {

    private InsuranceBean () {}

    public static AseguradoBO buildInsuranceFromCustomer(CustomerBO customer, String documentType) {
        AseguradoBO insurance = new AseguradoBO();
        insurance.setTipoDocumento(documentType);
        insurance.setNumeroDocumento(customer.getIdentityDocuments().get(0).getDocumentNumber());
        insurance.setNombres(customer.getFirstName());
        insurance.setApePaterno(customer.getLastName());
        insurance.setApeMaterno(customer.getSecondLastName());

        return insurance;
    }

    public static AseguradoBO buildInsuranceFromParticipant(ParticipantDTO participant) {
        AseguradoBO insurance = new AseguradoBO();
        insurance.setTipoDocumento(participant.getIdentityDocument().getDocumentType().getId());
        insurance.setNumeroDocumento(participant.getIdentityDocument().getDocumentNumber());
        insurance.setNombres(participant.getFirstName());
        insurance.setApePaterno(participant.getLastName());
        insurance.setApeMaterno(participant.getSecondLastName());

        return insurance;
    }
}
