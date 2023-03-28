package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class TimeRangeEx implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long min;
    private Long max;

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }
}
