package com.bbva.rbvd.lib.r302.Transfer;

import java.util.List;

public class PayloadProperties {

    private String documentTypeId;
    private List<String> segmentLifePlans;

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public List<String> getSegmentLifePlans() {
        return segmentLifePlans;
    }

    public void setSegmentLifePlans(List<String> segmentLifePlans) {
        this.segmentLifePlans = segmentLifePlans;
    }
}
