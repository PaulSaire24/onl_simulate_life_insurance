package com.bbva.rbvd.lib.r302.transform.objects;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.DatoParticularBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ModifyQuotationRimac {

    public void addFieldsDatoParticulares(InsuranceLifeSimulationBO rimacRequest, LifeSimulationDTO input, CustomerListASO responseListCustomers){

        DatoParticularBO datos1 = new DatoParticularBO();
        DatoParticularBO datos2 = new DatoParticularBO();
        DatoParticularBO datos3 = new DatoParticularBO();
        DatoParticularBO datos4 = new DatoParticularBO();
        DatoParticularBO datos5 = new DatoParticularBO();

        datos1.setEtiqueta(RBVDProperties.DATO_PARTICULAR_EDAD_ASEGURADO.getValue());
        datos1.setCodigo("");
        datos1.setValor(calculateYeardOldCustomer(responseListCustomers.getData().get(0).getBirthData().getBirthDate()));
        rimacRequest.getPayload().getDatosParticulares().add(datos1);

        datos2.setEtiqueta(RBVDProperties.DATO_PARTICULAR_SUMA_ASEGURADA_COBERTURA_FALLECIMIENTO.getValue());
        datos2.setCodigo("");
        datos2.setValor(input.getInsuredAmount() != null ? String.valueOf(input.getInsuredAmount().getAmount()) : "0");
        rimacRequest.getPayload().getDatosParticulares().add(datos2);

        datos3.setEtiqueta(RBVDProperties.DATO_PARTICULAR_PERIODO_ANOS.getValue());
        datos3.setCodigo("");
        datos3.setValor(input.getTerm() != null ? String.valueOf(input.getTerm().getNumber()) : "5");
        rimacRequest.getPayload().getDatosParticulares().add(datos3);

        datos4.setEtiqueta(RBVDProperties.DATO_PARTICULAR_PORCENTAJE_DEVOLUCION.getValue());
        datos4.setCodigo("");
        datos4.setValor(CollectionUtils.isEmpty(input.getListRefunds()) ? "0" : String.valueOf(input.getListRefunds().get(0).getUnit().getPercentage()));
        rimacRequest.getPayload().getDatosParticulares().add(datos4);

        datos5.setEtiqueta(RBVDProperties.DATO_PARTICULAR_INDICADOR_ENDOSADO.getValue());
        datos5.setCodigo("");
        datos5.setValor("N");
        rimacRequest.getPayload().getDatosParticulares().add(datos5);
    }

    private String calculateYeardOldCustomer(String birthDate){

        LocalDate hoy = LocalDate.now();
        LocalDate nacimiento = LocalDate.parse(birthDate);
        Long years = ChronoUnit.YEARS.between(nacimiento, hoy);

        return years.toString();
    }

}
