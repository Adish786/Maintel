package com.about.mantle.model.disqus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Disqus comment data
 * JsonIgnoreProperties is used here to avoid having to deserialize all the data we don't
 * need.
 *
 * This is returned in a variety of disqus api calls
 *
 * Note at the time of writing this it is unclear what the app needs, so please see the disqus
 * documentation for more info
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DisqusPost {

	//actual comment/thread with html
    private String message;
    
    //actual comment/thread without html
    private String rawMessage;
    
    //comment/thread id
    private String id;
    
    //thread's parent (null in case of actual comment)
    private Long parent;
    
    //author of comment/thread
    private DisqusAuthor author;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRawMessage() {
		return rawMessage;
	}

    @JsonProperty(value = "raw_message")
	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	public DisqusAuthor getAuthor() {
		return author;
	}

	public void setAuthor(DisqusAuthor author) {
		this.author = author;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DisqusPost [message=");
		builder.append(message);
		builder.append(", rawMessage=");
		builder.append(rawMessage);
		builder.append(", id=");
		builder.append(id);
		builder.append(", parent=");
		builder.append(parent);
		builder.append(", author=");
		builder.append(author);
		builder.append("]");
		return builder.toString();
	}

}
