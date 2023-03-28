package com.about.mantle.model.extended.docv2.sc.blocks;

import java.util.Objects;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

/**
 * Represents pros/cons SC block
 */
public class StructuredContentComparisonListEx extends
		AbstractStructuredContentContentEx<StructuredContentComparisonListEx.StructuredContentComparisonListDataEx> {

	/**
	 * Represents data in pros/cons SC block
	 */
	public static class StructuredContentComparisonListDataEx extends AbstractStructuredContentDataEx {

		private String comparisonListHeading;

		private String summaryText;

		private ComparisionList listA;

		private ComparisionList listB;

		public String getComparisonListHeading() {
			return comparisonListHeading;
		}

		public void setComparisonListHeading(String comparisonListHeading) {
			this.comparisonListHeading = comparisonListHeading;
		}

		public String getSummaryText() {
			return summaryText;
		}

		public void setSummaryText(String summaryText) {
			this.summaryText = summaryText;
		}

		public ComparisionList getListA() {
			return listA;
		}

		public void setListA(ComparisionList listA) {
			this.listA = listA;
		}

		public ComparisionList getListB() {
			return listB;
		}

		public void setListB(ComparisionList listB) {
			this.listB = listB;
		}

		@Override
		public String toString() {
			return "StructuredContentComparisonListDataEx [comparisonListHeading=" + comparisonListHeading
					+ ", summaryText=" + summaryText + ", listA=" + listA + ", listB=" + listB + "]";
		}

	}

	/**
	 * Represents data in a comparison list 
	 */
	public static class ComparisionList {

		private String heading;
		private SliceableListEx<String> items;

		public String getHeading() {
			return heading;
		}

		public void setHeading(String heading) {
			this.heading = heading;
		}

		public SliceableListEx<String> getItems() {
			return items;
		}

		public void setItems(SliceableListEx<String> items) {
			this.items = items;
		}

		@Override
		public String toString() {
			return "ComparisionList [heading=" + heading + ", items=" + items + "]";
		}

		@Override
		public int hashCode() {
			return Objects.hash(heading, items);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ComparisionList other = (ComparisionList) obj;
			if (heading == null) {
				if (other.heading != null)
					return false;
			} else if (!heading.equals(other.heading))
				return false;
			if (items == null) {
				if (other.items != null)
					return false;
			} else if (!items.equals(other.items))
				return false;
			return true;
		}

	}

}
