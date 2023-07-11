package com.bbva.rbvd.lib.r302.business.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;

public class ConvertUtil {

    private ConvertUtil(){throw new IllegalStateException("Utility class");}

    //Genera la fecha a partir de la fecha de fin de vigencia
    public static Date generateDate(String fechaFinVigencia) {
        DateTime dateTime = new DateTime(fechaFinVigencia);
        dateTime.withZone(DateTimeZone.forID("America/Lima"));
        return dateTime.toDate();
    }


}
