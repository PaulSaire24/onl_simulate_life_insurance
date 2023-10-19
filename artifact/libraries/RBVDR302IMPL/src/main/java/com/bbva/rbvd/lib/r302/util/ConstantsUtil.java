package com.bbva.rbvd.lib.r302.util;

public class ConstantsUtil {

    public static final String AMOUNT_UNIT_TYPE = "AMOUNT";
    public static final String TEXT_UNIT_TYPE = "TEXT";
    public static final String ANNUAL_PERIOD_ID = "ANNUAL";
    public static final String ANNUAL_PERIOD_NAME = "ANUAL";
    public static final String YES_S = "S";
    public static final String NO_N = "N";
    public static final String REFUNDS_UNIT_TYPE_AMOUNT = "AMOUNT";
    public static final String CURRENCY_CODE_PEN = "PEN";
    public static final String PARTICULAR_DATA_CLIENT_CUMULUS = "CUMULO_CLIENTE";
    public static final String REFUND_UNIT_PERCENTAGE = "PERCENTAGE";
    public static final String CUOTA = "CUOTA";
    public static final String COVERAGE_ID ="_COVERAGE_ID";
    public static final String COVERAGE_NAME ="_COVERAGE_NAME";
    public static final Long DEFAULT_NUM_CUOTAS = 1L;
    public static final String DEFAULT_FREQUENCY ="A";
    public static final String FLAG_GIFOLE_LIB_LIFE = "FLAG_GIFOLE_LIB_LIFE";
    public static final int IS_CONTRACTOR =1;
    public static final int IS_INSURED =2;
    public static final int CLIENT_BANK_LENGHT =8;
    public static final String REGEX_CONTAIN_ONLY_LETTERS=".*[a-zA-Z].*";
    public static final String REGEX_CONTAIN_ONLY_NUMBERS=".*[0-9].*";
    public static final String MOBILE_NUMBER="MOBILE_NUMBER";
    public static final String EMAIL="EMAIL";

    public static final class RegularExpression{
        private RegularExpression() {   }
        public static final String CONTAIN_ONLY_LETTERS=".*[a-zA-Z].*";
        public static final String CONTAIN_ONLY_NUMBERS=".*[0-9].*";
    }

    public static final class Numero{
        private Numero() {   }
        public static final Integer CERO = 0;
    }
    public static final class Plan {
        private Plan() {
        }

        public static final String UNO = "01";
        public static final String DOS = "02";
        public static final String TRES = "03";
    }

    public enum CoverageType{
        BLOCKED ("BLO","BLO", "BLOCKED"),
        OPTIONAL ("OPC", "OPC", "OPTIONAL"),
        PENDING ("PEN", "PEN", "PENDING");

        private final String key;
        private final String id;
        private final String name;
        CoverageType(String key, String id, String name) { this.key = key; this.id = id; this.name = name;}
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
