package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Table SC block
 */
public class StructuredContentTableEx extends AbstractStructuredContentContentEx<StructuredContentTableEx.StructuredContentTableDataEx> {

    /**
     * Table SC block data
     */
    public static class StructuredContentTableDataEx extends AbstractStructuredContentDataEx {

        private String title;
        private String caption;
        private SliceableListEx<SliceableListEx<CellData>> tableData;
        private SliceableListEx<RowAttributes> rowAttributes;
        private SliceableListEx<ColumnAttributes> columnAttributes;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getCaption() {
            return caption;
        }
        public void setCaption(String caption) {
            this.caption = caption;
        }
        @JsonProperty(value = "data")
        public SliceableListEx<SliceableListEx<CellData>> getTableData() {
            return tableData;
        }
        @JsonProperty(value = "data")
        public void setTableData(SliceableListEx<SliceableListEx<CellData>> tableData) {
            this.tableData = tableData;
        }
        public SliceableListEx<RowAttributes> getRowAttributes() {
            return rowAttributes;
        }
        public void setRowAttributes(SliceableListEx<RowAttributes> rowAttributes) {
            this.rowAttributes = rowAttributes;
        }
        public SliceableListEx<ColumnAttributes> getColumnAttributes() {
            return columnAttributes;
        }
        public void setColumnAttributes(SliceableListEx<ColumnAttributes> columnAttributes) {
            this.columnAttributes = columnAttributes;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("StructuredContentTableBlockDataEx [title=");
            builder.append(title);
            builder.append(", caption=");
            builder.append(caption);
            builder.append(", rowAttributes=");
            builder.append(rowAttributes.toString());
            builder.append(", columnAttributes=");
            builder.append(columnAttributes.toString());
            builder.append("]");
            return builder.toString();
        }

        public static class CellData {
            private String value;

            public String getValue() {
                return value;
            }
            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class RowAttributes {
            private boolean isHeading;

            @JsonProperty(value = "isHeading")
            public boolean getIsHeading() {
                return isHeading;
            }
            @JsonProperty(value = "isHeading")
            public void setIsHeading(boolean isHeading) {
                this.isHeading = isHeading;
            }

            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append("RowAttributes [isHeading=");
                builder.append(isHeading);
                builder.append("]");
                return builder.toString();
            }
        }

        public static class ColumnAttributes {
            private boolean isHeading;
            private Width<Integer> width;

            @JsonProperty(value = "isHeading")
            public boolean getIsHeading() {
                return isHeading;
            }
            @JsonProperty(value = "isHeading")
            public void setIsHeading(boolean isHeading) {
                this.isHeading = isHeading;
            }
            public Width<Integer> getWidth() {
                return width;
            }
            public void setWidth(Width<Integer> width) {
                this.width = width;
            }

            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append("ColumnAttributes [isHeading=");
                builder.append(isHeading);
                builder.append(", width=");
                builder.append(width.value);
                builder.append(width.unit);
                builder.append("]");
                return builder.toString();
            }

            public static class Width<T> {
                private T value;
                private String unit;

                public T getValue() {
                    return value;
                }
                public void setValue(T value) {
                    this.value = value;
                }
                public String getUnit() {
                    return unit;
                }
                public void setUnit(String unit) {
                    this.unit = unit;
                }
            }
        }
    }
}
