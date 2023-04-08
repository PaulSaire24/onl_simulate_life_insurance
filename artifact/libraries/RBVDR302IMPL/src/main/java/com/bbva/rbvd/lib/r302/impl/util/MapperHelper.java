package com.bbva.rbvd.lib.r302.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.bo.AseguradoBO;
import com.bbva.rbvd.dto.lifeinsrc.bo.DatoParticularBO;
import com.bbva.rbvd.dto.lifeinsrc.bo.SimulacionLifePayloadBO;
import com.bbva.rbvd.dto.lifeinsrc.bo.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapperHelper {

    private static final String GENERATE_ID = "generate";
    private static final String TIPO_DOCUMENTO = "05";

    protected ApplicationConfigurationService applicationConfigurationService;

    public InsuranceLifeSimulationBO mapInRequestRimacLife(LifeSimulationDTO input){

        InsuranceLifeSimulationBO simulationBo = new InsuranceLifeSimulationBO();
        SimulacionLifePayloadBO payload = new SimulacionLifePayloadBO();

        
        payload.setProducto("EASYYES01");
        payload.setMoneda("PEN");
        List<Long> listPlan = new ArrayList<>();
        listPlan.add(Long.valueOf(533628));
        payload.setPlanes(listPlan);

        DatoParticularBO datos = new DatoParticularBO();
        List<DatoParticularBO> datosParticulares = new ArrayList<>();
        datos.setEtiqueta("CUMULO_CLIENTE");
        datos.setCodigo("");
        datos.setValor("20000");
        datosParticulares.add(datos);
        payload.setDatosParticulares(datosParticulares);

        AseguradoBO asegurado = new AseguradoBO();
        asegurado.setTipoDocumento("L");
        asegurado.setNumeroDocumento("43242965");
        payload.setAsegurado(asegurado);

        List<Integer> list = Arrays.asList(1);
        payload.setPeriodosConDescuentoPrima(list);

        simulationBo.setPayload(payload);
        return simulationBo;
    }


    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
