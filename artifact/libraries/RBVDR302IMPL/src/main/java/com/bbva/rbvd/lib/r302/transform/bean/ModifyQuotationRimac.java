package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.commons.RefundsDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.CommonsDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModifyQuotationRimac {

    private static final String REFUNDS_UNITTYPE_PERCENTAGE = "PERCENTAGE";


    private ModifyQuotationRimac() {
    }

    public static InsuranceLifeSimulationBO mapInRequestRimacLifeModifyQuotation(LifeSimulationDTO input, CustomerListASO responseListCustomers, BigDecimal cumulo){
        InsuranceLifeSimulationBO simulationBo = new InsuranceLifeSimulationBO();
        SimulacionLifePayloadBO payload = new SimulacionLifePayloadBO();

        List<DatoParticularBO> datoParticularBOList = new ArrayList<>();
        datoParticularBOList.add(getDatoParticularEdadAsegurado(responseListCustomers));
        datoParticularBOList.add(getSumaAseguradaCoberturaFallecimiento(input));
        datoParticularBOList.add(getDatoParticularPeriodoAnios(input));
        datoParticularBOList.add(getDatoParticularPorcentajeDevolucion(input));
        datoParticularBOList.add(getCumuloCliente(cumulo));
        datoParticularBOList.add(getDatoParticularIndEndoso());
        payload.setDatosParticulares(datoParticularBOList);

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
        datos.setValor(getRefundPercentage(input));
        return datos;
    }

    private static String getRefundPercentage(LifeSimulationDTO input) {

        String percentage;

        if(CollectionUtils.isEmpty(input.getListRefunds())){
            percentage = "0";
        }else{
            List<RefundsDTO> refunds = input.getListRefunds().stream()
                    .filter(refundsDTO -> refundsDTO.getUnit().getUnitType().equals(REFUNDS_UNITTYPE_PERCENTAGE))
                    .collect(Collectors.toList());
            BigDecimal numPercentage = refunds.get(0).getUnit().getPercentage();
            percentage = String.valueOf(numPercentage.intValue());
        }

        return percentage;
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

        if(input.getInsuredAmount() != null){
            BigDecimal insuredAmount = input.getInsuredAmount().getAmount();
            datos.setValor(String.valueOf(insuredAmount.intValue()));
        }else{
            datos.setValor("0");
        }

        return datos;
    }

    private static DatoParticularBO getDatoParticularEdadAsegurado(CustomerListASO responseListCustomers) {
        DatoParticularBO datos = new DatoParticularBO();
        datos.setEtiqueta(RBVDProperties.DATO_PARTICULAR_EDAD_ASEGURADO.getValue());
        datos.setCodigo("");

        if(responseListCustomers != null){
            datos.setValor(calculateYeardOldCustomer(responseListCustomers.getData().get(0).getBirthData().getBirthDate()));
        }else{
            datos.setValor("35");
        }

        return datos;
    }

    private static String calculateYeardOldCustomer(String birthDate){

        LocalDate hoy = LocalDate.now();
        LocalDate nacimiento = LocalDate.parse(birthDate);
        long years = ChronoUnit.YEARS.between(nacimiento, hoy);

        return Long.toString(years);
    }

    public static List<Long> planesToRequestRimac(List<InsuranceProductModalityDAO> planes){
        if(CollectionUtils.isEmpty(planes)){
            return Collections.emptyList();
        }else{
            List<String> insrcCompanyModalities = planes.stream().filter(Objects::nonNull).map(CommonsDAO::getInsuranceCompanyModalityId).collect(Collectors.toList());
            return insrcCompanyModalities.stream().filter(Objects::nonNull).map(Long::parseLong).collect(Collectors.toList());
        }

    }

}
