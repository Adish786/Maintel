package com.about.mantle.model.extended.ugc;

import java.io.Serializable;

public class MostHelpfulFeedbacks implements Serializable {
    private static final long serialVersionUID = 1L;

    private Feedback positive;
    private Feedback critical;

    public Feedback getPositive() {
        return positive;
    }

    public void setPositive(Feedback positive) {
        this.positive = positive;
    }

    public Feedback getCritical() {
        return critical;
    }

    public void setCritical(Feedback critical) {
        this.critical = critical;
    }
}
