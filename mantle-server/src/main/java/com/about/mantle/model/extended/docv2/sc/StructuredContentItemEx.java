
package com.about.mantle.model.extended.docv2.sc;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A list item in a Structured Content LISTSC document ({@link ListStructuredContentDocumentEx}).  Contains multiple
 * content blocks.
 */
public class StructuredContentItemEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private SliceableListEx<AbstractStructuredContentContentEx<?>> contents = SliceableListEx.emptyList();
	private String quote;

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getContents() {
		return contents;
	}

	public void setContents(SliceableListEx<AbstractStructuredContentContentEx<?>> contents) {
		this.contents = contents;
	}
	
	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) { 
		this.quote = quote; 
	}

	@JsonIgnore
	public Stream<AbstractStructuredContentContentEx<?>> getContentsStreamOfType(String type) {
		return contents.stream().filter(scb -> scb.getType().equalsIgnoreCase(type));
	}

	@JsonIgnore
	public List<AbstractStructuredContentContentEx<?>> getContentsListOfType(String type) {
		return getContentsStreamOfType(type).collect(Collectors.<AbstractStructuredContentContentEx<?>>toList());
	}

}
