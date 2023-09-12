package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.DatoParticularBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.AseguradoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.SimulacionLifePayloadBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuotationRimac {

    private QuotationRimac(){}

    public static InsuranceLifeSimulationBO mapInRequestRimacEasyyesLife(LifeSimulationDTO input, BigDecimal sumCumulus){

        InsuranceLifeSimulationBO simulationBo = new InsuranceLifeSimulationBO();
        SimulacionLifePayloadBO payload = new SimulacionLifePayloadBO();

        payload.setMoneda("PEN");

        DatoParticularBO datos = new DatoParticularBO();
        List<DatoParticularBO> datosParticulares = new ArrayList<>();
        datos.setEtiqueta("CUMULO_CLIENTE");
        datos.setCodigo("");
        datos.setValor(sumCumulus == null ? "0" : String.valueOf(sumCumulus));
        datosParticulares.add(datos);
        payload.setDatosParticulares(datosParticulares);

        AseguradoBO asegurado = new AseguradoBO();
        asegurado.setTipoDocumento(input.getHolder().getIdentityDocument().getDocumentType().getId());
        asegurado.setNumeroDocumento(input.getHolder().getIdentityDocument().getDocumentNumber());
        payload.setAsegurado(asegurado);

        List<Integer> list = Arrays.asList(1);
        payload.setPeriodosConDescuentoPrima(list);

        simulationBo.setPayload(payload);
        return simulationBo;
    }

    public static InsuranceLifeSimulationBO mapInRequestRimacDynamicLife(LifeSimulationDTO input, BigDecimal sumCumulus,String productName, boolean isParticipant){
        InsuranceLifeSimulationBO requestRimac = new InsuranceLifeSimulationBO();
        SimulacionLifePayloadBO payload = new SimulacionLifePayloadBO();

        payload.setMoneda(ConstantsUtil.CURRENCY_CODE_PEN);
        payload.setProducto(productName);

        DatoParticularBO datos = new DatoParticularBO();
        List<DatoParticularBO> datosParticulares = new ArrayList<>();
        datos.setEtiqueta(ConstantsUtil.DATO_PARTICULAR_CUMULO_CLIENTE);
        datos.setCodigo("");
        datos.setValor(sumCumulus == null ? "0" : String.valueOf(sumCumulus));
        datosParticulares.add(datos);
        payload.setDatosParticulares(datosParticulares);

        AseguradoBO asegurado = new AseguradoBO();
        if(isParticipant){
            asegurado.setTipoDocumento(input.getParticipants().get(0).getIdentityDocument().getId());
            asegurado.setNumeroDocumento(input.getParticipants().get(0).getIdentityDocument().getDocumentNumber());
            payload.setAsegurado(asegurado);
        }else {
            asegurado.setTipoDocumento(input.getHolder().getIdentityDocument().getDocumentType().getId());
            asegurado.setNumeroDocumento(input.getHolder().getIdentityDocument().getDocumentNumber());
            payload.setAsegurado(asegurado);
        }


        requestRimac.setPayload(payload);
        return requestRimac;
    }

}
