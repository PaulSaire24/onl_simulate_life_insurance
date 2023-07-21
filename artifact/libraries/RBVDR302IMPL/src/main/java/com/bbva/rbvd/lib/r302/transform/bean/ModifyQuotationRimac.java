package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.CoberturaBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.DatoParticularBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.SimulacionLifePayloadBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ModifyQuotationRimac {

    public static InsuranceLifeSimulationBO mapInRequestRimacLifeModifyQuotation(LifeSimulationDTO input,CustomerListASO responseListCustomers,BigDecimal cumulo){
        InsuranceLifeSimulationBO simulationBo = new InsuranceLifeSimulationBO();
        SimulacionLifePayloadBO payload = new SimulacionLifePayloadBO();

        List<DatoParticularBO> datoParticularBOList = new ArrayList<>();
        datoParticularBOList.add(getDatoParticularEdadAsegurado(responseListCustomers));
        datoParticularBOList.add(getSumaAseguradaCoberturaFallecimiento(input));
        datoParticularBOList.add(getDatoParticularPeriodoAnios(input));
        datoParticularBOList.add(getDatoParticularPorcentajeDevolucion(input));
        datoParticularBOList.add(getDatoParticularIndEndoso());
        datoParticularBOList.add(getCumuloCliente(cumulo));
        payload.setDatosParticulares(datoParticularBOList);

        //Construir coberturas adicionales
        List<CoberturaBO> coberturas = new ArrayList<>();
        payload.setCoberturas(coberturas);

        //Constrir lista asegurados
        //List<AseguradoBO> asegurados = new ArrayList<>();
        //payload.setAsegurado(asegurados);

        simulationBo.setPayload(payload);
        return simulationBo;
    }

    public static void addFieldsDatoParticulares(InsuranceLifeSimulationBO rimacRequest, LifeSimulationDTO input, CustomerListASO responseListCustomers){

        rimacRequest.getPayload().getDatosParticulares().add(getDatoParticularEdadAsegurado(responseListCustomers));
        rimacRequest.getPayload().getDatosParticulares().add(getSumaAseguradaCoberturaFallecimiento(input));
        rimacRequest.getPayload().getDatosParticulares().add(getDatoParticularPeriodoAnios(input));
        rimacRequest.getPayload().getDatosParticulares().add(getDatoParticularPorcentajeDevolucion(input));
        rimacRequest.getPayload().getDatosParticulares().add(getDatoParticularIndEndoso());
    }

    private static DatoParticularBO getCumuloCliente(BigDecimal sumCumulus){
        DatoParticularBO datos = new DatoParticularBO();
        datos.setEtiqueta("CUMULO_CLIENTE");
        datos.setCodigo("");
        datos.setValor(sumCumulus == null ? "0" : String.valueOf(sumCumulus));
        return datos;
    }

    private static DatoParticularBO getDatoParticularIndEndoso() {
        DatoParticularBO datos = new DatoParticularBO();
        datos.setEtiqueta(RBVDProperties.DATO_PARTICULAR_INDICADOR_ENDOSADO.getValue());
        datos.setCodigo("");
        datos.setValor("N");
        return datos;
    }

    private static DatoParticularBO getDatoParticularPorcentajeDevolucion(LifeSimulationDTO input) {
        DatoParticularBO datos = new DatoParticularBO();
        datos.setEtiqueta(RBVDProperties.DATO_PARTICULAR_PORCENTAJE_DEVOLUCION.getValue());
        datos.setCodigo("");
        datos.setValor(CollectionUtils.isEmpty(input.getListRefunds()) ? "0" : String.valueOf(input.getListRefunds().get(0).getUnit().getPercentage()));
        return datos;
    }

    private static DatoParticularBO getDatoParticularPeriodoAnios(LifeSimulationDTO input) {
        DatoParticularBO datos = new DatoParticularBO();
        datos.setEtiqueta(RBVDProperties.DATO_PARTICULAR_PERIODO_ANOS.getValue());
        datos.setCodigo("");
        datos.setValor(input.getTerm() != null ? String.valueOf(input.getTerm().getNumber()) : "5");
        return datos;
    }

    private static DatoParticularBO getSumaAseguradaCoberturaFallecimiento(LifeSimulationDTO input) {
        DatoParticularBO datos = new DatoParticularBO();
        datos.setEtiqueta(RBVDProperties.DATO_PARTICULAR_SUMA_ASEGURADA_COBERTURA_FALLECIMIENTO.getValue());
        datos.setCodigo("");
        datos.setValor(input.getInsuredAmount() != null ? String.valueOf(input.getInsuredAmount().getAmount()) : "0");
        return datos;
    }

    private static DatoParticularBO getDatoParticularEdadAsegurado(CustomerListASO responseListCustomers) {
        DatoParticularBO datos = new DatoParticularBO();
        datos.setEtiqueta(RBVDProperties.DATO_PARTICULAR_EDAD_ASEGURADO.getValue());
        datos.setCodigo("");
        datos.setValor(calculateYeardOldCustomer(responseListCustomers.getData().get(0).getBirthData().getBirthDate()));
        return datos;
    }

    private static String calculateYeardOldCustomer(String birthDate){

        LocalDate hoy = LocalDate.now();
        LocalDate nacimiento = LocalDate.parse(birthDate);
        Long years = ChronoUnit.YEARS.between(nacimiento, hoy);

        return years.toString();
    }

}
