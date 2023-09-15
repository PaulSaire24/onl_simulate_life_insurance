package com.bbva.rbvd;

import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.RBVDR302;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

/**
 * Trx done for life simulation
 *
 */
public class RBVDT30101PETransaction extends AbstractRBVDT30101PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT30101PETransaction.class);

	/**
	 * The execute method...
	 */
	@Override
	public void execute() {
		RBVDR302 rbvdR302 = this.getServiceLibrary(RBVDR302.class);
		LOGGER.info("***** RBVDT30101PETransaction START *****");

		LifeSimulationDTO lifeSimulationDTO = new LifeSimulationDTO();
		lifeSimulationDTO.setProduct(this.getProduct());
		lifeSimulationDTO.setHolder(this.getHolder());
		lifeSimulationDTO.setInsuredAmount(this.getInsuredamount());
		lifeSimulationDTO.setExternalSimulationId(this.getExternalsimulationid());
		lifeSimulationDTO.setIsDataTreatment(this.getIsdatatreatment());
		lifeSimulationDTO.setListRefunds(this.getRefunds());
		lifeSimulationDTO.setTerm(this.getTerm());
		lifeSimulationDTO.setParticipants(this.getParticipants());
		lifeSimulationDTO.setEndorsed(this.getIsendorsed());
		lifeSimulationDTO.setSaleChannelId((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.CHANNELCODE));
		lifeSimulationDTO.setCreationUser((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE));
		lifeSimulationDTO.setUserAudit((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE));
		lifeSimulationDTO.setTraceId((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.REQUESTID));
		lifeSimulationDTO.setAap((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.AAP));

		LifeSimulationDTO response = rbvdR302.executeGetSimulation(lifeSimulationDTO);

		if(nonNull(response)) {
			this.setProduct(response.getProduct());
			this.setInsuredamount(response.getInsuredAmount());
			this.setHolder(response.getHolder());
			this.setIsdatatreatment(response.getIsDataTreatment());
			this.setExternalsimulationid(response.getExternalSimulationId());
			this.setRefunds(response.getListRefunds());
			this.setTerm(response.getTerm());
			this.setInsurancelimits(response.getInsuranceLimits());
			this.setParticipants(response.getParticipants());
			this.setIsendorsed(response.isEndorsed());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200, Severity.OK);
		} else {
			this.setSeverity(Severity.ENR);
		}

	}

}
