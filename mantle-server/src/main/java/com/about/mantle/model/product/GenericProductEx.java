package com.about.mantle.model.product;

import com.about.mantle.model.PriceInfo;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public class GenericProductEx extends BaseProductEx {
	
	private static final long serialVersionUID = 1L;
	
	private String brand;
	private String color;
	private Long releaseDate;
	private MeasurementInfo measurementInfo;
	private PriceInfo priceInfo;
	private IdentifierInfo identifierInfo;
	private SliceableListEx<PropertyInfo> properties = SliceableListEx.emptyList();

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Long getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Long releaseDate) {
		this.releaseDate = releaseDate;
	}

	public PriceInfo getPriceInfo() {
		return priceInfo;
	}

	public void setPriceInfo(PriceInfo priceInfo) {
		this.priceInfo = priceInfo;
	}

	public IdentifierInfo getIdentifierInfo() {
		return identifierInfo;
	}

	public void setIdentifierInfo(IdentifierInfo identifierInfo) {
		this.identifierInfo = identifierInfo;
	}

	public MeasurementInfo getMeasurementInfo() {
		return measurementInfo;
	}

	public void setMeasurementInfo(MeasurementInfo measurementInfo) {
		this.measurementInfo = measurementInfo;
	}

	public SliceableListEx<PropertyInfo> getProperties() {
		return properties;
	}

	public void setProperties(SliceableListEx<PropertyInfo> properties) {
		this.properties = SliceableListEx.emptyIfNull(properties);
	}
	
	public static class MeasurementInfo {
		
		private DimensionInfo dimensionInfo;
		private WeightInfo weightInfo;

		public DimensionInfo getDimensionInfo() {
			return dimensionInfo;
		}

		public void setDimensionInfo(DimensionInfo dimensionInfo) {
			this.dimensionInfo = dimensionInfo;
		}

		public WeightInfo getWeightInfo() {
			return weightInfo;
		}

		public void setWeightInfo(WeightInfo weightInfo) {
			this.weightInfo = weightInfo;
		}
		
		@Override
        public String toString() {
            StringBuilder sb = new StringBuilder("MeasurementInfo [");
            sb.append("dimensionInfo=").append(dimensionInfo);
            sb.append(", weightInfo=").append(weightInfo);
            sb.append(']');
            return sb.toString();
        }
		
		public static class WeightInfo {
			private String weight;
			private String unit;

			public String getWeight() {
				return weight;
			}

			public void setWeight(String weight) {
				this.weight = weight;
			}

			public String getUnit() {
				return unit;
			}

			public void setUnit(String unit) {
				this.unit = unit;
			}
			
			@Override
	        public String toString() {
	            StringBuilder sb = new StringBuilder("WeightInfo [");
	            sb.append("weight=").append(weight);
	            sb.append(", unit=").append(unit);
	            sb.append(']');
	            return sb.toString();
	        }
		}
		
		public static class DimensionInfo {
			private String unit;
			private String depth;
			private String height;
			private String width;

			public String getUnit() {
				return unit;
			}

			public void setUnit(String unit) {
				this.unit = unit;
			}

			public String getDepth() {
				return depth;
			}

			public void setDepth(String depth) {
				this.depth = depth;
			}

			public String getHeight() {
				return height;
			}

			public void setHeight(String height) {
				this.height = height;
			}

			public String getWidth() {
				return width;
			}

			public void setWidth(String width) {
				this.width = width;
			}
			
			@Override
	        public String toString() {
				StringBuilder sb = new StringBuilder("DimensionInfo [");
	            sb.append("depth=").append(depth);
	            sb.append(", height=").append(height);
	            sb.append(", width=").append(width);
	            sb.append(", unit=").append(unit);
	            sb.append(']');
	            return sb.toString();
	        }
		}
	}

	public static class PropertyInfo {
		private String name;
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
		@Override
        public String toString() {
			StringBuilder sb = new StringBuilder("PropertyInfo [");
            sb.append("name=").append(name);
            sb.append(", value=").append(value);
            sb.append(']');
            return sb.toString();
        }
	}
	public static class IdentifierInfo {
		private String id;
		private String type;

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
		
		@Override
        public String toString() {
			StringBuilder sb = new StringBuilder("IdentifierInfo [");
            sb.append("id=").append(id);
            sb.append(", type=").append(type);
            sb.append(']');
            return sb.toString();
        }
	}

}