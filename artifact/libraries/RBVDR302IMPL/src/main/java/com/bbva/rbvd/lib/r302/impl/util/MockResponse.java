package com.bbva.rbvd.lib.r302.impl.util;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MockResponse {

    private static final String responseRimac = "{\"payload\":{\"cotizaciones\":[{\"cotizacion\":\"1b7eeb9f-a8d9-47ec-8575-8b7071e73902\",\"diasVigencia\":\"30\",\"fechaFinVigencia\":\"2023-08-05\",\"plan\":{\"plan\":533611,\"descripcionPlan\":\"Plan 1\",\"moneda\":\"PEN\",\"primaNeta\":180,\"primaBruta\":180,\"financiamientos\":[{\"financiamiento\":23776,\"periodicidad\":\"Mensual\",\"numeroCuotas\":12,\"indicadorDescuentoPrima\":\"N\",\"montoNetoFraccionado\":180,\"montoPago\":180,\"fechaInicio\":\"2023-07-06\",\"cuotasFinanciamiento\":[{\"cuota\":1,\"monto\":15,\"fechaVencimiento\":\"2023-07-06\",\"moneda\":\"PEN\"},{\"cuota\":2,\"monto\":15,\"fechaVencimiento\":\"2023-08-06\",\"moneda\":\"PEN\"},{\"cuota\":3,\"monto\":15,\"fechaVencimiento\":\"2023-09-06\",\"moneda\":\"PEN\"},{\"cuota\":4,\"monto\":15,\"fechaVencimiento\":\"2023-10-06\",\"moneda\":\"PEN\"},{\"cuota\":5,\"monto\":15,\"fechaVencimiento\":\"2023-11-06\",\"moneda\":\"PEN\"},{\"cuota\":6,\"monto\":15,\"fechaVencimiento\":\"2023-12-06\",\"moneda\":\"PEN\"},{\"cuota\":7,\"monto\":15,\"fechaVencimiento\":\"2024-01-06\",\"moneda\":\"PEN\"},{\"cuota\":8,\"monto\":15,\"fechaVencimiento\":\"2024-02-06\",\"moneda\":\"PEN\"},{\"cuota\":9,\"monto\":15,\"fechaVencimiento\":\"2024-03-06\",\"moneda\":\"PEN\"},{\"cuota\":10,\"monto\":15,\"fechaVencimiento\":\"2024-04-06\",\"moneda\":\"PEN\"},{\"cuota\":11,\"monto\":15,\"fechaVencimiento\":\"2024-05-06\",\"moneda\":\"PEN\"},{\"cuota\":12,\"monto\":15,\"fechaVencimiento\":\"2024-06-06\",\"moneda\":\"PEN\"}]},{\"financiamiento\":23774,\"periodicidad\":\"Anual\",\"numeroCuotas\":1,\"indicadorDescuentoPrima\":\"S\",\"montoNetoFraccionado\":150,\"montoPago\":150,\"fechaInicio\":\"2023-07-06\",\"cuotasFinanciamiento\":[{\"cuota\":1,\"monto\":150,\"fechaVencimiento\":\"2023-08-07\",\"moneda\":\"PEN\"}]}],\"coberturas\":[{\"cobertura\":10920,\"descripcionCobertura\":\"Fallecimiento\",\"primaNeta\":15.83,\"primaBruta\":15.83,\"moneda\":\"PEN\",\"sumaAsegurada\":20000,\"sumaAseguradaMinima\":20000,\"SumaAseguradaMaxima\":50000,\"principal\":\"S\",\"observacionCobertura\":\"Fallecimiento\",\"condicion\":\"OBL\",\"fechaFin\":\"2023-08-05\"},{\"cobertura\":10921,\"descripcionCobertura\":\"Indemnizaci\u00f3n Adicional Por Fallecimiento Accidental\",\"primaNeta\":1.09,\"primaBruta\":1.09,\"moneda\":\"PEN\",\"sumaAsegurada\":20000,\"sumaAseguradaMinima\":20000,\"SumaAseguradaMaxima\":50000,\"principal\":\"N\",\"observacionCobertura\":\"Muerte Accidental\",\"condicion\":\"OBL\",\"fechaFin\":\"2023-08-05\"},{\"cobertura\":10923,\"descripcionCobertura\":\"Invalidez Total y Permanente por Accidente o Enfermedad\",\"primaNeta\":1.09,\"primaBruta\":1.09,\"moneda\":\"PEN\",\"sumaAsegurada\":20000,\"sumaAseguradaMinima\":20000,\"SumaAseguradaMaxima\":50000,\"principal\":\"N\",\"observacionCobertura\":\"Invalidez Total\",\"condicion\":\"OBL\",\"fechaFin\":\"2023-08-05\"}]},\"indicadorBloqueo\":0},{\"cotizacion\":\"1b7eeb9f-a8d9-47ec-8575-8b7071e73902\",\"diasVigencia\":\"30\",\"fechaFinVigencia\":\"2023-08-05\",\"plan\":{\"plan\":533612,\"descripcionPlan\":\"Plan 2\",\"moneda\":\"PEN\",\"primaNeta\":336,\"primaBruta\":336,\"financiamientos\":[{\"financiamiento\":23776,\"periodicidad\":\"Mensual\",\"numeroCuotas\":12,\"indicadorDescuentoPrima\":\"N\",\"montoNetoFraccionado\":336,\"montoPago\":336,\"fechaInicio\":\"2023-07-06\",\"cuotasFinanciamiento\":[{\"cuota\":1,\"monto\":28,\"fechaVencimiento\":\"2023-07-06\",\"moneda\":\"PEN\"},{\"cuota\":2,\"monto\":28,\"fechaVencimiento\":\"2023-08-06\",\"moneda\":\"PEN\"},{\"cuota\":3,\"monto\":28,\"fechaVencimiento\":\"2023-09-06\",\"moneda\":\"PEN\"},{\"cuota\":4,\"monto\":28,\"fechaVencimiento\":\"2023-10-06\",\"moneda\":\"PEN\"},{\"cuota\":5,\"monto\":28,\"fechaVencimiento\":\"2023-11-06\",\"moneda\":\"PEN\"},{\"cuota\":6,\"monto\":28,\"fechaVencimiento\":\"2023-12-06\",\"moneda\":\"PEN\"},{\"cuota\":7,\"monto\":28,\"fechaVencimiento\":\"2024-01-06\",\"moneda\":\"PEN\"},{\"cuota\":8,\"monto\":28,\"fechaVencimiento\":\"2024-02-06\",\"moneda\":\"PEN\"},{\"cuota\":9,\"monto\":28,\"fechaVencimiento\":\"2024-03-06\",\"moneda\":\"PEN\"},{\"cuota\":10,\"monto\":28,\"fechaVencimiento\":\"2024-04-06\",\"moneda\":\"PEN\"},{\"cuota\":11,\"monto\":28,\"fechaVencimiento\":\"2024-05-06\",\"moneda\":\"PEN\"},{\"cuota\":12,\"monto\":28,\"fechaVencimiento\":\"2024-06-06\",\"moneda\":\"PEN\"}]},{\"financiamiento\":23774,\"periodicidad\":\"Anual\",\"numeroCuotas\":1,\"indicadorDescuentoPrima\":\"S\",\"montoNetoFraccionado\":280,\"montoPago\":280,\"fechaInicio\":\"2023-07-06\",\"cuotasFinanciamiento\":[{\"cuota\":1,\"monto\":280,\"fechaVencimiento\":\"2023-08-07\",\"moneda\":\"PEN\"}]}],\"coberturas\":[{\"cobertura\":10920,\"descripcionCobertura\":\"Fallecimiento\",\"primaNeta\":23.92,\"primaBruta\":23.92,\"moneda\":\"PEN\",\"sumaAsegurada\":40000,\"sumaAseguradaMinima\":40000,\"SumaAseguradaMaxima\":80000,\"principal\":\"S\",\"observacionCobertura\":\"Fallecimiento\",\"condicion\":\"OBL\",\"fechaFin\":\"2023-08-05\"},{\"cobertura\":10921,\"descripcionCobertura\":\"Indemnizaci\u00f3n Adicional Por Fallecimiento Accidental\",\"primaNeta\":2.04,\"primaBruta\":2.04,\"moneda\":\"PEN\",\"sumaAsegurada\":40000,\"sumaAseguradaMinima\":40000,\"SumaAseguradaMaxima\":80000,\"principal\":\"N\",\"observacionCobertura\":\"Muerte Accidental\",\"condicion\":\"OBL\",\"fechaFin\":\"2023-08-05\"},{\"cobertura\":10923,\"descripcionCobertura\":\"Invalidez Total y Permanente por Accidente o Enfermedad\",\"primaNeta\":2.04,\"primaBruta\":2.04,\"moneda\":\"PEN\",\"sumaAsegurada\":40000,\"sumaAseguradaMinima\":40000,\"SumaAseguradaMaxima\":80000,\"principal\":\"N\",\"observacionCobertura\":\"Invalidez Total\",\"condicion\":\"OBL\",\"fechaFin\":\"2023-08-05\"}]},\"indicadorBloqueo\":0}],\"producto\":\"VIDADINAMICO\",\"moneda\":\"PEN\",\"planes\":[533611,533612],\"datosParticulares\":[{\"etiqueta\":\"CUMULO_CLIENTE\",\"codigo\":\"\",\"valor\":\"20000\"},{\"etiqueta\":\"EDAD_ASEGURADO\",\"valor\":\"38\",\"codigo\":\"\"},{\"etiqueta\":\"SUMA_ASEGURADA_COBERTURA_FALLECIMIENTO\",\"valor\":\"10000\",\"codigo\":\"\"},{\"etiqueta\":\"PERIODO_A\u00d1OS\",\"valor\":\"5\",\"codigo\":\"\"},{\"etiqueta\":\"PORCENTAJE_DEVOLUCION\",\"valor\":\"0\",\"codigo\":\"\"},{\"etiqueta\":\"INDICADOR_ENDOSADO\",\"valor\":\"N\",\"codigo\":\"\"}],\"financiamiento\":[{\"numeroCuotas\":12,\"financiamiento\":23776}]}}";
    private static final String DATE = "yyyy-MM-dd";
    private Gson gson;

    public MockResponse() {
        gson = new GsonBuilder()
                .setDateFormat(DATE)
                .create();
    }

    public InsuranceLifeSimulationBO getMockResponseRimacService(){
        return this.gson.fromJson(responseRimac,InsuranceLifeSimulationBO.class);
    }

}
