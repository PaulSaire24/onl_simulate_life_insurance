package com.bbva.rbvd.lib.r302.Transfer;

import java.util.List;

public class PayloadProperties {

    private String documentTypeId;
    private String documentTypeIdAsText;
    private List<Boolean> segmentLifePlans;

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public List<Boolean> getSegmentLifePlans() {
        return segmentLifePlans;
    }

    public void setSegmentLifePlans(List<Boolean> segmentLifePlans) {
        this.segmentLifePlans = segmentLifePlans;
    }

    public String getDocumentTypeIdAsText() {
        return documentTypeIdAsText;
    }

    public void setDocumentTypeIdAsText(String documentTypeIdAsText) {
        this.documentTypeIdAsText = documentTypeIdAsText;
    }
}
