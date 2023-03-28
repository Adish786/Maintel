package com.about.mantle.model.journey;

public enum JourneyType {
    /**
     * Called 'Grand' Journey by the health team.  A journey with multiple sections and a root document that
     * isn't a program summary
     */
    STANDARD,
    /**
     * What the health team refers to as a "journey".  Single section, no root doc (actually root doc is a
     * program summary, which is just a placeholder to keep the same taxene structure as a Standard
     * isn't a program summary
     */
    MINI,
}
