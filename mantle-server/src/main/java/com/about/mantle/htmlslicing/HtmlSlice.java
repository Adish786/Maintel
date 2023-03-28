package com.about.mantle.htmlslicing;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * This is a cut and paste and rename of Selene's ContentBlock class
 */
public class HtmlSlice implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content;
    private Integer characterCount;
    private Integer wordCount;
    private Boolean isHeading;

    public HtmlSlice() {}

    public HtmlSlice(String content, Integer characterCount, Integer wordCount, Boolean isHeading) {
        this.content = content;
        this.characterCount = characterCount;
        this.wordCount = wordCount;
        this.isHeading = isHeading;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Integer getCharacterCount() {
        return characterCount;
    }
    public void setCharacterCount(Integer characterCount) {
        this.characterCount = characterCount;
    }
    public Integer getWordCount() {
        return wordCount;
    }
    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }
    public Boolean getIsHeading() {
        return isHeading;
    }
    public void setIsHeading(Boolean isHeading) {
        this.isHeading = isHeading;
    }

    @Override
    public int hashCode() {
        // @formatter:off
        return new HashCodeBuilder()
                .append(content)
                .append(characterCount)
                .append(wordCount)
                .append(isHeading)
                .build();
        // @formatter:on
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HtmlSlice)) return false;

        HtmlSlice other = (HtmlSlice) obj;
        // @formatter:off
        return new EqualsBuilder()
                .append(content, other.content)
                .append(characterCount, other.characterCount)
                .append(wordCount, other.wordCount)
                .append(isHeading, other.isHeading)
                .build();
        // @formatter:on
    }

    @Override
    public String toString() {
        // @formatter:off
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("content", content)
                .append("characterCount", characterCount)
                .append("wordCount", wordCount)
                .append("isHeading", isHeading)
                .build();
        // @formatter:on
    }

}

