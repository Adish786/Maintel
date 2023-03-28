package com.about.mantle.model.RSS2;

import org.joda.time.DateTime;

import java.util.Comparator;

/**
 * Item element of Channel element
 */
public class Item {

	/**
	 * Enclosure element of Item element
	 */
	public static class Enclosure {
        private String url;
        private String length;
        private String type;

        public Enclosure(String url, String length, String type) {
            this.url = url;
            this.length = length;
            this.type = type;
        }
        
		public Enclosure() {
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getLength() {
			return length;
		}

		public void setLength(String length) {
			this.length = length;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Enclosure {url=");
			builder.append(url);
			builder.append(", length=");
			builder.append(length);
			builder.append(", type=");
			builder.append(type);
			builder.append("}");
			return builder.toString();
		}
    }

    private String title;
    private String link;
    private String description;
    private String author;
    private Item.Enclosure enclosure;
    private DateTime pubDate;

    public Item(String title, String link, String description, String author, DateTime pubDate, Item.Enclosure enclosure) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.author = author;
        this.enclosure = enclosure;
        this.pubDate = pubDate;
    }

	public Item(Item item) {
		this.title = item.getTitle();
		this.link = item.getLink();
		this.description = item.getDescription();
		this.author = item.getAuthor();
		this.enclosure = item.getEnclosure();
		this.pubDate = item.getPubDate();
	}
    
	public Item() {
	}

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Item.Enclosure getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(Item.Enclosure enclosure) {
		this.enclosure = enclosure;
	}

	public DateTime getPubDate() {
		return pubDate;
	}

	public void setPubDate(DateTime pubDate) {
		this.pubDate = pubDate;
	}

	/**
     * Compares two RSSItems by pubDate.
     *
     * Returns 0 if both items are null or have a null pubDate. 1 if only the first item is null or has a null pubDate.
     * -1 if only the second item is null or has a null pubDate. Otherwise compares the pubDate of the two items and
     * returns -1 if second item is less than the first, 0 if equal and 1 if the second item is greater than the first.
     */
    public static Comparator<Item> ItemComparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            if ((o1 == null || o1.getPubDate() == null) && (o2 == null || o2.getPubDate() == null)) {
                return 0;
            } else if (o1 == null || o1.getPubDate() == null) {
                return 1;
            } else if (o2 == null || o2.getPubDate() == null) {
                return -1;
            }
            return o2.getPubDate().compareTo(o1.getPubDate());
        }
    };
}
