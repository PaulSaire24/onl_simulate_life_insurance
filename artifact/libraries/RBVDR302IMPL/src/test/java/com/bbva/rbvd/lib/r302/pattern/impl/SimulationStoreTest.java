package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.commons.*;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/RBVDR302-app.xml",
        "classpath:/META-INF/spring/RBVDR302-app-test.xml",
        "classpath:/META-INF/spring/RBVDR302-arc.xml",
        "classpath:/META-INF/spring/RBVDR302-arc-test.xml" })

public class SimulationStoreTest {

    private PayloadStore payloadStore;
    private PostSimulation simulation;
    private PISDR350 pisdR350;
    private MockData mockData;
    private InsuranceLifeSimulationBO responseRimac;
    private LifeSimulationDTO responseInput;
   // private LifeSimulationDTO requestInput;
    @Before
    public void setUp() throws Exception {
        pisdR350 = mock(PISDR350.class);
        simulation =  new SimulationStore(pisdR350);

        mockData = MockData.getInstance();

        responseRimac = mockData.getInsuranceRimacSimulationResponse();
        responseInput = mockData.getInsuranceSimulationResponse();

        payloadStore = new PayloadStore("1234","P02X2021",responseRimac, responseInput, "",new ProductInformationDAO());
    }
    @Test
    public void endTest() {
        //given
        Map<String,Object> map = new HashMap<>();
        map.put("PISD.SELECT_INSURANCE_SIMULATION_ID_LIFE",new HashMap<>());

        payloadStore.setResponse(new LifeSimulationDTO());
        payloadStore.getResponse().setInsuredAmount(new InsuredAmountDTO());
        payloadStore.getResponse().getInsuredAmount().setAmount(BigDecimal.valueOf(455));
        payloadStore.getResponse().setHolder(new HolderDTO());
        payloadStore.getResponse().getHolder().setId("45555");
        payloadStore.getResponse().getHolder().setIdentityDocument(new IdentityDocumentDTO());
        payloadStore.getResponse().getHolder().getIdentityDocument().setDocumentType(new DocumentTypeDTO());
        payloadStore.getResponse().getHolder().getIdentityDocument().getDocumentType().setId("45454");
        payloadStore.getResponse().getHolder().getIdentityDocument().setDocumentNumber("45555");
        payloadStore.getResponse().setProduct(new InsuranceProductDTO());
        payloadStore.getResponse().setTerm(new TermDTO());
        payloadStore.getResponse().getTerm().setNumber(45);

        InstallmentsDTO installments = new InstallmentsDTO();
        installments.setPeriod(new PeriodDTO());
        installments.getPeriod().setId("fr");
        InsurancePlanDTO insurancePlan = new InsurancePlanDTO();
        insurancePlan.setInstallmentPlans(Collections.singletonList(installments));
        payloadStore.getResponse().getProduct().setPlans(Collections.singletonList(insurancePlan));


        when(this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(),new HashMap<>())).thenReturn(map);
        when(this.pisdR350.executeInsertSingleRow(Mockito.anyString(),Mockito.anyMap())).thenReturn(1);

        simulation.end(payloadStore);
        Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(), new HashMap<>());
    }

}