package com.bbva.rbvd.lib.r302.util;

import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

public class ConvertUtil {

    private ConvertUtil(){throw new IllegalStateException("Utility class");}

    //Genera la fecha a partir de la fecha de fin de vigencia
    public static Date generateDate(String fechaFinVigencia) {
        DateTime dateTime = new DateTime(fechaFinVigencia);
        dateTime.withZone(DateTimeZone.forID("America/Lima"));
        return dateTime.toDate();
    }
    public static Map<String, String> validateContactDetails(final CustomerBO customer){

        Map<String, String> contactDetails = customer.getContactDetails().
                stream().
                filter(contactDetail -> nonNull(contactDetail.getContactType().getId())).
                collect(groupingBy(
                        contactDetail -> contactDetail.getContactType().getId(),
                        mapping(ContactDetailsBO::getContact, new SingletonStringCollector())
                ));

        return contactDetails;
    }



}
