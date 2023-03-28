package com.about.mantle.model.extended.docv2.sc;

public class TermStructuredContentDocumentEx extends StructuredContentDocumentEx {

    private static final long serialVersionUID = 1L;
    private String shortDefinition;
    private String termName;

    public String getShortDefinition() {
        return shortDefinition;
    }

    public void setShortDefinition(String shortDefinition) {
        this.shortDefinition = shortDefinition;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }
}
