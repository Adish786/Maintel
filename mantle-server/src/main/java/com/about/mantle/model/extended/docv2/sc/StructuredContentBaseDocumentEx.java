package com.about.mantle.model.extended.docv2.sc;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.TaggedImage.UsageFlag;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCommerceInfoEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This serves as a base class for different kinds of SC documents. It abstracts
 * out few helper methods to get contents(sc blocks) of the document. It's up to
 * the concrete class what contents they want to expose to clients
 * {@link #getContentsStream()}. Also, it gives few convenience methods.
 *
 */
@SuppressWarnings("rawtypes")
public abstract class StructuredContentBaseDocumentEx extends BaseDocumentEx implements Serializable {
    private static final long serialVersionUID = 1L;

    public StructuredContentBaseDocumentEx() {
    }

    public StructuredContentBaseDocumentEx(StructuredContentBaseDocumentEx document) {
        super(document);
    }

    /**
     * {@link Function} which gives back stream of SC blocks, given a list Of SC
     * blocks.
     * If given list is null, returns empty stream.
     */
    @JsonIgnore
    public Function<SliceableListEx<AbstractStructuredContentContentEx<?>>, Stream<AbstractStructuredContentContentEx<?>>> ensureSCStream = (
            items) -> Objects.nonNull(items) ? items.stream() : Stream.empty();

    /**
     * Handy {@link Predicate} to filter sc blocks having null data in them.
     */
    @JsonIgnore
    public Predicate<AbstractStructuredContentContentEx> onNonNullSCData = (scb) -> scb.getData() != null;

    /**
     * Gives back count of given type in contents
     */
    @JsonIgnore
    private int countInContents(String type) {
        return (int) getContentsStreamOfType(type).count();
    }

    /**
     * Concrete class must provide implementation for what content they want to
     * expose. e.g. {@link ListStructuredContentDocumentEx} will stream contents of
     * all list items plus intro and outro , {@link StructuredContentDocumentEx}
     * will stream all SC blocks in document itself.
     */
    abstract public Stream<AbstractStructuredContentContentEx<?>> getContentsStream();

    /**
     * Convenient method to which collects list on getContentsStream()
     */
    @JsonIgnore
    public List<AbstractStructuredContentContentEx<?>> getContentsList() {
        return getContentsStream().collect(toList());
    }

    @JsonIgnore
    public AbstractStructuredContentContentEx<?> getContentBlock(String uuid) {
        return getContentsList().stream().filter(block -> block.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Returns the index of the product record in the contents list
     */
    @JsonIgnore
    public Integer getProductIndexByUuid(String uuid) {
        return getContentsStreamOfType("PRODUCTRECORD").map(product -> (String) product.getUuid()).collect(toList())
                .indexOf(uuid);
    }

    /**
     * Returns image count from contents exposed by {@link #getContentsStream()}. It
     * also counts primary image.
     */
    @JsonIgnore
    @Override
    public int calculateImageCount() {
        int imageCount = countInContents("IMAGE");
        if (!ImageEx.EMPTY.equals(getImageForUsage(UsageFlag.PRIMARY))) {
            imageCount++;
        }
        return imageCount;
    }

    /**
     * Returns boolean if any of contents streamed by {@link #getContentsStream()}
     * is of type commerce info.
     */
    @JsonIgnore
    @Override
    public boolean hasCommerceInfo() {
        return getContentsStream().anyMatch(scBlock -> scBlock instanceof StructuredContentCommerceInfoEx);
    }

    /**
     * Convenience method to get contents stream filtered on type
     */
    @JsonIgnore
    public Stream<AbstractStructuredContentContentEx<?>> getContentsStreamOfType(String type) {
        return getFilteredContentsStream(scb -> scb.getType().equalsIgnoreCase(type));
    }

    /**
     * Convenience method to get contents list filtered on type
     */
    @JsonIgnore
    public List<AbstractStructuredContentContentEx> getContentsListOfType(String type) {
        return getContentsStreamOfType(type).collect(toList());
    }

    /**
     * Generic method to filter contents stream using {@link Predicate}
     */
    @JsonIgnore
    public Stream<AbstractStructuredContentContentEx<?>> getFilteredContentsStream(
            Predicate<AbstractStructuredContentContentEx> on) {
        return getContentsStream().filter(on);
    }

}
