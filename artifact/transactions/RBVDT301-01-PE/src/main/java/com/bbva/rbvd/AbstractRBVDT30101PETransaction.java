package com.bbva.rbvd;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.rbvd.dto.lifeinsrc.commons.HolderDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuranceProductDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.RefundsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TermDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.InsuranceLimitsDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ParticipantDTO;
import java.util.List;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractRBVDT30101PETransaction extends AbstractTransaction {

	public AbstractRBVDT30101PETransaction(){
	}


	/**
	 * Return value for input parameter product
	 */
	protected InsuranceProductDTO getProduct(){
		return (InsuranceProductDTO)this.getParameter("product");
	}

	/**
	 * Return value for input parameter holder
	 */
	protected HolderDTO getHolder(){
		return (HolderDTO)this.getParameter("holder");
	}

	/**
	 * Return value for input parameter isDataTreatment
	 */
	protected Boolean getIsdatatreatment(){
		return (Boolean)this.getParameter("isDataTreatment");
	}

	/**
	 * Return value for input parameter externalSimulationId
	 */
	protected String getExternalsimulationid(){
		return (String)this.getParameter("externalSimulationId");
	}

	/**
	 * Return value for input parameter insuredAmount
	 */
	protected InsuredAmountDTO getInsuredamount(){
		return (InsuredAmountDTO)this.getParameter("insuredAmount");
	}

	/**
	 * Return value for input parameter refunds
	 */
	protected List<RefundsDTO> getRefunds(){
		return (List<RefundsDTO>)this.getParameter("refunds");
	}

	/**
	 * Return value for input parameter term
	 */
	protected TermDTO getTerm(){
		return (TermDTO)this.getParameter("term");
	}

	/**
	 * Return value for input parameter participants
	 */
	protected List<ParticipantDTO> getParticipants(){
		return (List<ParticipantDTO>)this.getParameter("participants");
	}

	/**
	 * Set value for InsuranceProductDTO output parameter product
	 */
	protected void setProduct(final InsuranceProductDTO field){
		this.addParameter("product", field);
	}

	/**
	 * Set value for InsuredAmountDTO output parameter insuredAmount
	 */
	protected void setInsuredamount(final InsuredAmountDTO field){
		this.addParameter("insuredAmount", field);
	}

	/**
	 * Set value for HolderDTO output parameter holder
	 */
	protected void setHolder(final HolderDTO field){
		this.addParameter("holder", field);
	}

	/**
	 * Set value for Boolean output parameter isDataTreatment
	 */
	protected void setIsdatatreatment(final Boolean field){
		this.addParameter("isDataTreatment", field);
	}

	/**
	 * Set value for String output parameter externalSimulationId
	 */
	protected void setExternalsimulationid(final String field){
		this.addParameter("externalSimulationId", field);
	}

	/**
	 * Set value for List<RefundsDTO> output parameter refunds
	 */
	protected void setRefunds(final List<RefundsDTO> field){
		this.addParameter("refunds", field);
	}

	/**
	 * Set value for TermDTO output parameter term
	 */
	protected void setTerm(final TermDTO field){
		this.addParameter("term", field);
	}

	/**
	 * Set value for InsuranceLimitsDTO output parameter insuranceLimits
	 */
	protected void setInsurancelimits(final InsuranceLimitsDTO field){
		this.addParameter("insuranceLimits", field);
	}

	/**
	 * Set value for List<ParticipantDTO> output parameter participants
	 */
	protected void setParticipants(final List<ParticipantDTO> field){
		this.addParameter("participants", field);
	}
}
