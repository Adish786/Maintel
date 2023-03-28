package com.about.mantle.model.journey;

/**
 * Represents the relationship between a given document and a given journey.
 * A document can be a member of a journey, linked to a journey, or have nothing to do with a journey at all.
 */
public enum JourneyRelationshipType {
    /**
     * Document is a member of the given journey
     */
    MEMBER,
    /**
     * Document is linked to, but is not a member of, a given journey
     */
    LINKED,
    /**
     * Document has no relationship to a given journey
     */
    NONE,
}
