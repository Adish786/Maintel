package com.about.mantle.model.extended.attribution;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a type of work performed by an author
 */
public class Attribution implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String type;
    // Attribution field should contain the descriptor for the type value
    @Deprecated
    private String attribution;
    private String descriptor;
    private String authorId;
    private String authorType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    /**
     * Use {@link #getDescriptor()}
     */
    @Deprecated
    public String getAttribution() {
        return attribution;
    }

    /**
     * Use {@link #setDescriptor(String)}
     */
    @Deprecated
    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public String getDescriptor() {
        // Temporary until https://dotdash.atlassian.net/browse/ARC-959 is released
        return descriptor != null ? descriptor : attribution;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getAuthorType() {
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

    @Override
    public String toString() {
        return "Attribution{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", attribution='" + attribution + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorType=" + authorType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribution that = (Attribution) o;
        return Objects.equals(id, that.id)
                && Objects.equals(type, that.type)
                && Objects.equals(attribution, that.attribution)
                && Objects.equals(authorId, that.authorId)
                && Objects.equals(authorType, that.authorType);
    }

    @Override
    public int hashCode() {
        // TODO: when removing attribution be sure to replace it with descriptor in hashCode, equals, and toString
        return Objects.hash(id, type, attribution, authorId, authorType);
    }
}