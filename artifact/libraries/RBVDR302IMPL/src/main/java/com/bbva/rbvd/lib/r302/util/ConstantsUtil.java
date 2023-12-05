package com.bbva.rbvd.lib.r302.util;

import java.math.BigDecimal;
import java.time.ZoneId;

public class ConstantsUtil {

    public static final class Zone {
        private Zone() {}
        public static final ZoneId ZONE_ID_GTM = ZoneId.of("GMT");
    }

    public static final String AMOUNT_UNIT_TYPE = "AMOUNT";
    public static final String TEXT_UNIT_TYPE = "TEXT";

    public enum Period{
        ANNUAL("ANNUAL","ANUAL","A");
        private final String id;
        private final String name;
        private final String code;
        Period(String id, String name, String code) { this.id = id;this.name = name;this.code = code; }
        public String getId() {return id;}
        public String getName() {return name;}
        public String getCode() {return code;}
    }

    public static final class Condition {
        private Condition() {}
        public static final String YES_S = "S";
        public static final String NO_N = "N";
        public static final String TRUE = "true";
    }

    public static final String REFUNDS_UNIT_TYPE_AMOUNT = "AMOUNT";
    public static final String PARTICULAR_DATA_CLIENT_CUMULUS = "CUMULO_CLIENTE";
    public static final String REFUND_UNIT_PERCENTAGE = "PERCENTAGE";
    public static final String CUOTA = "CUOTA";
    public static final String INSURANCE_TYPE_LIFE_VALUE = "LIFE";
    public static final String DEFAULT_BANK_ID = "0011";
    public static final String DEFAULT_BRANCH_ID = "0814";
    public static final String INSURANCE_SIMULATION_VALUE = "INSURANCE_SIMULATION";
    public static final String EMPTY_VALUE = "";

    public static final class DefaultValues{
        private DefaultValues() {}
        public static final Long DEFAULT_NUM_CUOTAS = 1L;
        public static final String DEFAULT_FREQUENCY ="A";
    }

    public static final class Flag{
        private Flag() {}
        public static final String FLAG_GIFOLE_LIB_LIFE = "FLAG_GIFOLE_LIB_LIFE";
        public static final String ENABLE_GIFOLE_LIFE_ASO = "ENABLE_GIFOLE_LIFE_ASO";
    }

    public enum Role {
        CONTRANTOR("CONTRANTOR",1,"PARTICIPANT_ROLE_ID"),
        INSURED("INSURED",2,"PARTICIPANT_ROLE_ID");
        private final String name;
        private final int id;
        private final String key;

        Role(String name, int id, String key) {
            this.name = name;
            this.id = id;
            this.key = key;
        }
        public String getName() {
            return name;
        }
        public int getId() {
            return id;
        }
        public String getKey() {
            return key;
        }
    }

    public static final class CoverageTypeConstant{
        private CoverageTypeConstant() {}
        public static final String COVERAGE_ID ="_COVERAGE_ID";
        public static final String COVERAGE_NAME ="_COVERAGE_NAME";
    }

    public static final class ContactDetails{
        private ContactDetails() {}
        public static final String MOBILE_NUMBER="MOBILE_NUMBER";
        public static final String EMAIL="EMAIL";
        public static final String PHONE="PHONE";
        public static final String NOT_FOUND_EMAIL= "No se encontro correo";
        public static final String NOT_FOUND_PHOME= "No celular";
    }

    public static final class RegularExpression{
        private RegularExpression() {   }
        public static final String CONTAIN_ONLY_LETTERS=".*[a-zA-Z].*";
        public static final String CONTAIN_ONLY_NUMBERS=".*[0-9].*";
        public static final String DELIMITER = "|";
    }

    public enum Gender{
        MALE("MALE","M"),
        FEMALE("FEMALE","F");
        private final String name;
        private final String code;
        Gender(String name, String code) {this.name = name;this.code = code;}
        public String getName() {return name;}
        public String getCode() {return code;}
    }

    public static final class Numero{
        private Numero() {   }
        public static final Integer CERO = 0;
        public static final int CLIENT_BANK_LENGHT =8;
    }

    public static final class Plan {
        private Plan() {
        }

        public static final String UNO = "01";
        public static final String DOS = "02";
        public static final String TRES = "03";
    }

    public static final class Currency {
        private Currency() {
        }

        public static final String PEN = "PEN";
    }

    public enum CoverageType{
        BLOCKED ("BLO","BLO", "BLOCKED"),
        OPTIONAL ("OPC", "OPC", "OPTIONAL");
        private final String key;
        private final String id;
        private final String name;
        CoverageType(String key, String id, String name) {this.key = key;this.id = id;this.name = name;}
        public String getKey() { return key; }
        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static final class Product {
        private Product() {
        }
        public static final String EASY_YES = "840";
        public static final String DYNAMIC_LIFE = "841";
    }
}
